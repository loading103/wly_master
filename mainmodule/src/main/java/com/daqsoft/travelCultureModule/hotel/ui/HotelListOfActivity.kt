package com.daqsoft.travelCultureModule.hotel.ui

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainHotelListSetActivityBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath.ZMAIN_HOTEL_LIST
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.databinding.ItemListPopupWindowBinding
import com.daqsoft.provider.databinding.ItemListPopupWindowLeftBinding
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.service.GaoDeLocation.OnGetCurrentLocationLisner
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.hotel.adapter.HotelLsAdapter
import com.daqsoft.travelCultureModule.hotel.bean.HotelLsTypeBean
import com.daqsoft.travelCultureModule.hotel.bean.HotelTypeModel
import com.daqsoft.travelCultureModule.hotel.util.TabBarUtils
import com.daqsoft.travelCultureModule.hotel.viewmodel.HotelListViewModel
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.main_hotel_list_set_activity.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.support.v4.onRefresh
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * @Description 资源--安逸住酒店列表
 * @ClassName   HotelListActivity
 * @Author      PuHua
 * @Time        2020/2/24 9:49
 */
@Route(path = ZMAIN_HOTEL_LIST)
class HotelListOfActivity :
    TitleBarActivity<MainHotelListSetActivityBinding, HotelListViewModel>() {
    /**
     * 用户当前位置
     */
    var selfLocation: LatLng? = null

    /**站点 Id*/
    @JvmField
    @Autowired
    var region: String = ""

    @JvmField
    @Autowired
    var siteId: String? = ""
    private val headers = arrayOf("地区", "类型", "排序")

    private val popupViews: MutableList<View> = mutableListOf()


    //主界面
    var content: View? = null

    //第一个筛选栏
    var firstview: View? = null

    //第二个筛选栏
    var secondview: View? = null

    //第三个筛选栏
    var threedview: View? = null

    var selfLat = ""
    var selfLon = ""

    //排序方式
    var sortType = ""

    //区域选择
    var areaSiteSwitch = ""

    //星级
    var level = ""

    //酒店类型
    var type = ""

    //酒店设施
    var eqt = ""

    // 特色服务
    var special = ""

    /**
     * 酒店列表筛选类型
     */
    val hotelLsTypeBean: HotelLsTypeBean by lazy {
        HotelLsTypeBean()
    }

    //选择指示器
    var current_position = 0

    var currPage = 1
    var pageSize = 10

    var secondData: MutableList<MutableList<ResourceTypeLabel>> = mutableListOf()

    var mRecyclerview: RecyclerView? = null

    var tabUtils = TabBarUtils()

    /**
     * 景区的适配器
     */
    var adapter: HotelLsAdapter? = null

    var areaListPopWindow: AreaSelectPopupWindow? = null

    override fun getLayout(): Int = R.layout.main_hotel_list_set_activity

    override fun setTitle(): String = getString(R.string.main_hotel_title)

    override fun injectVm(): Class<HotelListViewModel> = HotelListViewModel::class.java

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel

        initSortPopWindow()

        content = layoutInflater.inflate(R.layout.content_view_hotel, null)
        mRecyclerview = content!!.findViewById(R.id.rv_activity)
        adapter = HotelLsAdapter(this@HotelListOfActivity)
        rv_activity!!.layoutManager = GridLayoutManager(this, 1)
        rv_activity!!.adapter = adapter



        tabUtils.bindLayout(tab_t, fragm_t, popupViews, this, layout_bar)

        fragm_t.setOnClickListener {
            tabUtils.closeMenu()
            return@setOnClickListener
        }

        mBinding.refreshHotel.setOnRefreshListener {
            currPage = 1
            mModel.getList(getParams())
        }
        adapter!!.lister = object : HotelLsAdapter.OnHotelLsItemClickListener {
            override fun onCollectClick(id: Int, position: Int, collectionStatus: Boolean) {
                onCollectClickL(id, position, collectionStatus)
            }

        }
        adapter!!.setOnLoadMoreListener {
            currPage++
            mModel.getList(getParams())
        }

        mBinding.txtHotelSearch?.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.vHotelTopToSerach.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.viewArea.onNoDoubleClick {
            tabUtils?.closeMenu()
            areaListPopWindow?.show(mBinding.viewArea)

        }
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            notifyCollectStatus(it, false)
        })
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            notifyCollectStatus(it, true)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mBinding?.hotelAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            mBinding.refreshHotel.isEnabled = p1 >= 0
        })

        mBinding?.rvActivity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.refreshHotel.isEnabled = topRowVerticalPosition >= 0
            }
        })
    }


    /**请求多个接口*/
    fun getDatas() {
        //如果站点Id不是空，默认选择区域
        if (!siteId.isNullOrEmpty()) {
            areaSiteSwitch = siteId!!
        }
        mModel.getHotelTopAds()
        mModel.getList(getParams())
        mModel.getChildRegions()
        mModel.getSelectLabel()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    override fun initData() {
        showLoadingDialog()
        GaoDeLocation.getInstance().init(this, object : OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String,
                result: String,
                lat: Double,
                lon: Double,
                adcode: String
            ) {
                selfLat = lat.toString()
                selfLon = lon.toString()
                adapter?.selfLocation = LatLng(lat, lon)
                getDatas()
            }

            override fun onError(errorMsg: String) {
                getDatas()
            }
        })

        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow = AreaSelectPopupWindow.getInstance(this, false) { item ->
                    currPage = 1
                    showLoadingDialog()
                    mBinding.viewArea.text = item.name
                    region = item.region
                    areaSiteSwitch = item.siteId
                    mModel.getList(getParams())
                }
                //站点ID不是空
                if (!region.isNullOrEmpty()) {
                    for (index in it.indices) {
                        //从数据中找出与region匹配的数据
                        if (region == it[index].region) {
                            //默认选中
                            areaListPopWindow!!.defSelected(index)
                            mBinding.viewArea.text = it[index].name
                            break
                        }
                    }
                }


                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })

        mModel.selectLabels.observe(this, Observer {
            secondData = it
            for (item in secondData) {
                item.add(0, ResourceTypeLabel("", "", "", "", "全部").setSelects(true))
            }
            secondData.add(0, HotelTypeModel.scenicLevel)
            current_position = 0
            sconed2adapter.add(secondData.get(0))
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.topAdsLiveData.observe(this, Observer { it1 ->
            if (it1 == null || it1.adInfo.isNullOrEmpty()) {
                mBinding.vHotelListTopNoAdv.visibility = View.VISIBLE
                mBinding.vHotelListTopAdv.visibility = View.GONE
            } else {
                val ads = it1.adInfo
                mBinding.cbanerScenicTopAdv
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return AdvImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return com.daqsoft.provider.R.layout.layout_common_adv
                        }
                    }, ads)
                    .setCanLoop(ads.size > 1)
                    .setPointViewVisible(ads.size > 1)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener { it2 ->
                        // 跳转事件
                        MenuJumpUtils.adJump(ads[it2])
                    }
                    .setPageIndicator(null).startTurning(3000)

                if (!it1.adInfo.isNullOrEmpty()) {
                    mBinding.vHotelListTopNoAdv.visibility = View.GONE
                    mBinding.vHotelListTopAdv.visibility = View.VISIBLE
                }
            }
        })

        mModel.hotelList.observe(this, Observer {
            dissMissLoadingDialog()
            if (mBinding.rvActivity.visibility == View.GONE) {
                mBinding.rvActivity.visibility = View.VISIBLE
            }
            if (currPage == 1) {
                adapter!!.clear()
//                mBinding.refreshHotel.setRefreshing(false)
                mBinding.refreshHotel.finishRefresh()
            }
            adapter!!.add(it.datas!!)
            if (adapter!!.getData().size >= it.page!!.total) {
                adapter!!.loadEnd()
            } else {
                adapter!!.loadComplete()
            }
        })
    }

    private fun initSortPopWindow() {
        firstview = layoutInflater.inflate(R.layout.list_popup_window, null, false)
        var cityView = firstview!!.findViewById<RecyclerView>(R.id.window_list_choose_recycler)
        cityView.layoutManager = GridLayoutManager(this, 1)
        cityView.setAdapter(firstadapter)

        secondview = layoutInflater.inflate(R.layout.layout_label_select, null, false)
        var second1View = secondview!!.findViewById<RecyclerView>(R.id.rv_first)
        second1View.layoutManager = GridLayoutManager(this, 1)
        second1View.setAdapter(sconed1adapter)
        sconed1adapter.add(HotelTypeModel.firstType)

        var second2View = secondview!!.findViewById<RecyclerView>(R.id.rv_secend)
        second2View.layoutManager = GridLayoutManager(this, 1)
        second2View.setAdapter(sconed2adapter)

        //确定与重置
        var resetview = secondview!!.findViewById<TextView>(R.id.tv_reset)
        var sureview = secondview!!.findViewById<TextView>(R.id.tv_sure)
        resetview.setOnClickListener {
            for (data in secondData) {
                for (index in 0 until data.size) {
                    data.get(index).select = index == 0
                }
            }
            hotelLsTypeBean.clearAll()
            sconed2adapter.notifyDataSetChanged()
        }

        sureview.setOnClickListener {
            tabUtils.closeMenu()
            showLoadingDialog()
            currPage = 1
            level = hotelLsTypeBean.level!!
            type = hotelLsTypeBean.type!!
            eqt = hotelLsTypeBean.eqt!!
            special = hotelLsTypeBean.special!!
            mModel.getList(getParams())
        }

        threedview = layoutInflater.inflate(R.layout.list_popup_window, null, false)
        var sortView = threedview!!.findViewById<RecyclerView>(R.id.window_list_choose_recycler)
        sortView.layoutManager = GridLayoutManager(this, 1)
        threedadapter.add(HotelTypeModel.sorts)
        sortView.setAdapter(threedadapter)

        //init popupViews
        popupViews.add(firstview!!)
        popupViews.add(secondview!!)
        popupViews.add(threedview!!)
    }


    /**
     * 第一个筛选栏对应的adapter
     */
    var firstadapter = object : RecyclerViewAdapter<ItemListPopupWindowBinding, ChildRegion>
        (R.layout.item_list_popup_window) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemListPopupWindowBinding,
            position: Int,
            item: ChildRegion
        ) {
            mBinding.tvItem.text = item.name
            mBinding.tvItem.isSelected = item.selected != 0
            RxView.clicks(mBinding.tvItem).throttleFirst(1, TimeUnit.SECONDS).subscribe {
                for (index in getData()) {
                    if (index == item) {
                        if (item.selected == 0) {
                            item.selected = 1
                        } else {
                            return@subscribe
                        }
                    } else {
                        index.selected = 0
                    }
                }
                notifyDataSetChanged()
                tabUtils.setTabText(if (position == 0) headers[0] else item.name)
//                tabUtils.closeMenu()
                //重新请求
                currPage = 1
                areaSiteSwitch = item.siteId
//                mModel.getList(getParams())
            }
        }
    }

    private fun onCollectClickL(id: Int, position: Int, collectionStatus: Boolean) {
        showLoadingDialog()
        if (collectionStatus) {
            mModel.collectionCancelScenic(id.toString(), position)
        } else {
            mModel.collectionScenic(id.toString(), position)
        }
    }

    /**
     * 第三个筛选栏对应的adapter
     */
    var threedadapter = object : RecyclerViewAdapter<ItemListPopupWindowBinding, ValueKeyBean>
        (R.layout.item_list_popup_window) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemListPopupWindowBinding,
            position: Int,
            item: ValueKeyBean
        ) {
            mBinding.tvItem.text = item.name
            mBinding.tvItem.isSelected = item.select
            RxView.clicks(mBinding.tvItem).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    for (index in getData()) {
                        if (index == item) {
                            if (!item.select) {
                                item.select = true
                            } else {
                                item.select = true
//                                return@subscribe
                            }
                        } else {
                            index.select = false
                        }
                    }
                    notifyDataSetChanged()
                    tabUtils.setTabText(if (position === 0) headers[2] else item.name)
                    tabUtils.closeMenu()
                    //重新请求
                    currPage = 1
                    sortType = item.value
                    mModel.getList(getParams())
                }
        }
    }

    /**
     * 第二个筛选栏对应的adapter
     */
    var sconed1adapter =
        object : RecyclerViewAdapter<ItemListPopupWindowLeftBinding, ResourceTypeLabel>
            (R.layout.item_list_popup_window_left) {
            @SuppressLint("CheckResult")
            override fun setVariable(
                mBinding: ItemListPopupWindowLeftBinding,
                position: Int,
                item: ResourceTypeLabel
            ) {
                mBinding.tvItem.text = item.labelName
                mBinding.tvItem.isSelected = item.select
//                if (item.select) {
//                    mBinding.tvItem.backgroundColorResource = R.color.f5
//                } else {
//                    mBinding.tvItem.backgroundColorResource = R.color.white
//                }
                mBinding.tvItem.setOnClickListener {
                    for (index in getData()) {
                        if (index == item) {
                            if (!item.select) {
                                item.select = true
                                sconed2adapter.clear()
                                current_position = position
                                sconed2adapter.add(secondData.get(position))
                            } else {
                                return@setOnClickListener
                            }
                        } else {
                            index.select = false
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

    /**
     * 第二个筛选栏对应的adapter
     */
    var sconed2adapter = object : RecyclerViewAdapter<ItemListPopupWindowBinding, ResourceTypeLabel>
        (R.layout.item_list_popup_window) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemListPopupWindowBinding,
            position: Int,
            item: ResourceTypeLabel
        ) {
            mBinding.tvItem.text = item.labelName
            mBinding.tvItem.isSelected = item.select
            mBinding.tvItem.setOnClickListener {
                for (index in getData()) {
                    if (index == item) {
                        if (!item.select) {
                            item.select = true
                            hotelLsTypeBean.setValue(current_position, item.id)
                        } else {
                            hotelLsTypeBean.clearValue(current_position)
                        }
                    } else {
                        index.select = false
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["sortType"] = sortType
        if (sortType == "" || sortType == "disNum") {
            param["lat"] = selfLat
            param["lng"] = selfLon
        }
        param["areaSiteSwitch"] = areaSiteSwitch
        param["level"] = level
        param["type"] = type
        param["eqt"] = eqt
        param["special"] = special
        param["pageSize"] = pageSize.toString()
        param["currPage"] = currPage.toString()
        return param
    }

    /**
     * 更新收藏状态
     */
    fun notifyCollectStatus(position: Int, status: Boolean) {
        try {
            if (position < adapter!!.getData().size) {
                if (adapter!!.getData()[position].vipResourceStatus != null) {
                    adapter!!.getData()[position].vipResourceStatus.collectionStatus = status
                    adapter!!.notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.cbanerScenicTopAdv.isVisible) {
            mBinding.cbanerScenicTopAdv.startTurning(3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBinding.cbanerScenicTopAdv.isVisible) {
            mBinding.cbanerScenicTopAdv.stopTurning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsRepository.instance.statisticsPage(title, 2)
        GaoDeLocation.getInstance().release()
    }
}

