package com.daqsoft.venuesmodule.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.bean.VenueLevelBean
import com.daqsoft.provider.bean.VenueTypeBean
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.VenueLsAdapter
import com.daqsoft.venuesmodule.databinding.ActivityVenuesBinding
import com.daqsoft.venuesmodule.databinding.ItemVenuesListBinding
import com.daqsoft.venuesmodule.model.VenueSortsModel
import com.daqsoft.venuesmodule.model.VenuesViewModel
import com.daqsoft.venuesmodule.repository.bean.ActivityInfo
import com.daqsoft.venuesmodule.repository.bean.OrderRoomInfo
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.RxView
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 文化场馆列表Activity
 * @author 黄熙
 * @date 2019/12/18 0018
 * @version 1.0.0
 * @since JDK 1.8
 *
 * @更新人     邓益千
 * @更新日期   2020年4月27日
 * @更新内容   根据地区编码，自动筛选
 */
@Route(path = ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY)
class VenuesActivity : TitleBarActivity<ActivityVenuesBinding, VenuesViewModel>() {

    /**
     * 地区编码
     */
    @JvmField
    @Autowired
    var region: String = ""
    /**
     * 关键字
     */
    var keyWord = ""
    @JvmField
    @Autowired
    var siteId: String? = ""

    @JvmField
    @Autowired
    var tag: String? = ""

    @JvmField
    @Autowired
    var type: String? = ""

    @JvmField
    @Autowired
    var name: String? = ""
    /**
     * 活动室适配器
     */
    private var activityAdapter: ViewPagerAdapter? = null
    /**
     * 活动室集合列表
     */
    var activityViewList = mutableListOf<ActivityInfo>()
    /**
     * 文化场馆的适配器
     */
    var adapter: VenueLsAdapter? = null

    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    /**
     * 文化馆类型窗口
     */
    private var venuesTypePopWindow: ListPopupWindow<Any>? = null
    /**
     * 文化馆等级窗口
     */
    private var venuesLevelPopWindow: ListPopupWindow<Any>? = null

    /**
     * 排序窗口
     */
    private var sortsPopWindow: ListPopupWindow<Any>? = null

    /**
     * 排序
     */
    val sorts = VenueSortsModel.sorts

    override fun setTitle(): String = if (!name.isNullOrEmpty()) {
        name ?: ""
    } else {
        getString(R.string.venues_list_name)
    }

    override fun getLayout(): Int = R.layout.activity_venues

    override fun injectVm(): Class<VenuesViewModel> = VenuesViewModel::class.java

    override fun initView() {
        mModel?.mPresenter?.value?.loading = false
        mBinding?.recyVenues.visibility = View.GONE
        adapter = VenueLsAdapter(this@VenuesActivity)
        mBinding.recyVenues.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyVenues.adapter = adapter
        // 加载更多控件
        adapter!!.setOnLoadMoreListener {
            mModel?.currPage = mModel?.currPage + 1
            mModel?.getVenusList()
        }

        // 刷新控件
        mBinding?.swprefreshVenues.setOnRefreshListener {
            //            mBinding?.swprefreshVenues.isRefreshing = true
            mModel?.currPage = 1
            mModel?.getVenusList()
        }
        mBinding?.txtSearchVenue?.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        // 定位当前信息


        initViewModel()

        initViewEvent()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    private fun initViewModel() {
        if (!type.isNullOrEmpty()) {
            mModel.venueType = type
        }
        if (!tag.isNullOrEmpty()) {
            mModel.tag = tag
        }
        // 文化馆列表
        mModel.venuesList.observe(this, Observer {
            dissMissLoadingDialog()
//            mBinding.swprefreshVenues.isRefreshing = false
            mBinding.swprefreshVenues.finishRefresh()
            pageDeal(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding?.recyVenues.visibility = View.VISIBLE
            mBinding.swprefreshVenues.finishRefresh()
        })
        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow = AreaSelectPopupWindow.getInstance(context, false) { item ->
                    mModel.areaSiteSwitch = item.siteId
                    mModel.currPage = 1
                    showLoadingDialog()
                    mBinding.tvArea.text = item.name
                    mBinding.recyVenues.visibility = View.GONE
                    mModel.getVenusList()
                }
                for (index in it.indices) {
                    if (region == it[index].region) {
                        mBinding.tvArea.text = it[index].name
                        areaListPopWindow!!.defSelected(index)
                        break
                    }
                }
                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })
        // 类型
        mModel.venuesTypeLiveData.observe(this, Observer {
            if (venuesTypePopWindow == null) {
                it.add(0, VenueTypeBean("", "不限", "", "", "all"))
                venuesTypePopWindow = ListPopupWindow.getInstance(
                    mBinding.tvType, it as List<VenueTypeBean>?,
                    resources.getDimension(R.dimen.dp_220).toInt()
                ) { item ->
                    showLoadingDialog()
                    var value = item as VenueTypeBean
                    mBinding.tvType.text = value.name
                    mModel.venueType = value.id
                    mModel.currPage = 1
                    mBinding.recyVenues.visibility = View.GONE
                    mModel.getVenusList()
                    // 重新请求等级数据
                    venuesLevelPopWindow?.clearData()
                    if (!value.id.isNullOrEmpty()) {
                        mModel.getVenueLevels("${value.value}_level")
                    } else {
                        mBinding.tvLevel.visibility = View.GONE
                    }
                }
                if (!type.isNullOrEmpty()) {
                    for (item in it) {
                        if (item.id == type) {
                            mBinding.tvType.text = item.name
                            mModel.getVenueLevels("${item.value}_level")
                        }
                    }
                }
            }
        })

        // 等级
        mModel.venuesLevelLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.tvLevel.visibility = View.VISIBLE
                it.add(0, VenueLevelBean("", "不限", "", "", "all"))
                if (venuesLevelPopWindow == null) {
                    venuesLevelPopWindow = ListPopupWindow.getInstance(
                        mBinding.tvLevel,
                        it as List<VenueLevelBean>?
                    ) { item ->
                        showLoadingDialog()
                        var value = item as VenueLevelBean
                        mBinding.tvLevel.text = value.name
                        mModel.venueLevel = value.value
                        mModel.currPage = 1
                        mBinding.recyVenues.visibility = View.GONE
                        mModel.getVenusList()
                    }
                } else {
                    venuesLevelPopWindow?.updateDatas(it as List<VenueLevelBean>?)
                }
            } else {
                venuesLevelPopWindow = null
                mBinding.tvLevel.visibility = View.GONE
            }
        })

        // 收藏
        mModel.collectVenueLiveData.observe(this, Observer {
            if (adapter != null) {
                adapter?.notifyCollectStatus(it)
            }
        })

        // 取消收藏
        mModel.canceCollectLiveData.observe(this, Observer {
            if (adapter != null) {
                adapter?.notifyCollectStatus(it)
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        mModel.topAdsLiveData.observe(this, Observer {
            if (it == null || it.adInfo.isNullOrEmpty()) {
                mBinding.vVenueTopNoAdv.visibility = View.VISIBLE
                mBinding.vVenueListTopAdv.visibility = View.GONE
            } else {
                mBinding.cbanerVenuesTopAdv
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return AdvImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return com.daqsoft.provider.R.layout.layout_common_adv
                        }
                    }, it.adInfo)
                    .setCanLoop(it.adInfo.size > 1)
                    .setPointViewVisible(it.adInfo.size > 1)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener { it2 ->
                        run {
                            // 跳转事件
                            MenuJumpUtils.adJump(it.adInfo[it2])
                        }
                    }
                    .setPageIndicator(null)

                if (it.adInfo.isNotEmpty()) {
                    mBinding.vVenueTopNoAdv.visibility = View.GONE
                    mBinding.vVenueListTopAdv.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getChildRegions()
        mModel.getVenueLsTopAds()
        if (!siteId.isNullOrEmpty()) {
            mModel.areaSiteSwitch = siteId!!
        }
        location()
        if (type != "") {
            mModel.venueType = type
        }
        if (tag != "") {
            mModel.tag = tag
        }
        mModel.getVenueTypes()
    }

    private fun initViewEvent() {
        if (sortsPopWindow == null) {
            sortsPopWindow = ListPopupWindow.getInstance(
                mBinding.tvSort, sorts as List<ValueKeyBean>?
            ) { item ->
                showLoadingDialog()
                var value = item as ValueKeyBean
                mBinding.tvSort.text = item.name
                mModel.orderType = value.value
                mModel.currPage = 1
                mBinding.recyVenues.visibility = View.GONE
                mModel.getVenusList()
            }
        }
        RxView.clicks(mBinding.tvArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow!!.show(mBinding.tvArea)
                }
            }
        RxView.clicks(mBinding.tvType).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (venuesTypePopWindow != null) {
                    venuesTypePopWindow!!.show()
                }
            }
        RxView.clicks(mBinding.tvLevel).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (venuesLevelPopWindow != null) {
                    venuesLevelPopWindow!!.show()
                }
            }
        RxView.clicks(mBinding.tvSort).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (sortsPopWindow != null) {
                    sortsPopWindow!!.show()
                }
            }
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 1)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_VENUE)
                .navigation()
        }
        mBinding?.recyVenues.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.swprefreshVenues.isEnabled = topRowVerticalPosition >= 0
            }
        })

        mBinding?.appbarVenues.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                if (p1 >= 0) {
                    mBinding.swprefreshVenues.setEnabled(true);
                } else {
                    mBinding.swprefreshVenues.setEnabled(false);
                }
            }

        })
        adapter?.onItemClick = object : VenueLsAdapter.OnItemClickListener {
            override fun onItemClick(id: String, position: Int, status: Boolean) {
                if (status) {
                    mModel?.canceCollect(id, position)
                } else {
                    mModel?.collect(id, position)
                }
            }

        }
        mBinding.vTopFoodMapMode.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 1)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_VENUE)
                .navigation()
        }
        mBinding.vVenuesTopToSerach.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
    }


    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(data: MutableList<VenuesListBean>) {
        if (mModel.currPage == 1) {
            mBinding?.recyVenues.smoothScrollToPosition(0)
            mBinding?.recyVenues.visibility = View.VISIBLE
            adapter!!.clear()
            adapter!!.emptyViewShow = data.isNullOrEmpty()

        }
        if (!data.isNullOrEmpty()) {
            adapter!!.add(data)
        }
        if (data.isNullOrEmpty() || data.size < mModel.pageSize) {
            adapter!!.loadEnd()
        } else {
            adapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }

    /**
     * ViewPager翻页监听器
     */
    class ViewPagerPageChangeListener(
        mBinding: ItemVenuesListBinding,
        datas: MutableList<OrderRoomInfo>
    ) : ViewPager
    .OnPageChangeListener {

        var mBinding = mBinding
        var datas = datas
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            Timber.e(position.toString())
            mBinding.tvItemVenuesPageName.setText(datas.get(position).name)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

    }


    /**
     * 定位自己的位置
     */
    fun location() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                try {
                    var selfLocation = LatLng(lat_, lon_)
                    adapter?.selfLocation = selfLocation
                    mModel?.lat = lat_.toString()
                    mModel?.lng = lon_.toString()
                } catch (e: Exception) {
                }
                mModel.getVenusList()
            }

            override fun onError(errormsg: String) {
                mModel.getVenusList()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsRepository.instance.statisticsPage(title, 2)
        GaoDeLocation.getInstance().release()
    }
}
