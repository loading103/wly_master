package com.daqsoft.travelCultureModule.food

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityFoodLsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.FoodBean
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.food.adapter.FoodLsAdapter
import com.daqsoft.travelCultureModule.food.view.TypeSelectPopupWindow
import com.daqsoft.travelCultureModule.food.viewmodel.FoodLsViewModel
import com.google.android.material.appbar.AppBarLayout
import org.jetbrains.anko.support.v4.onRefresh

/**
 * @Description 美食列表
 * @ClassName   FoodLsActivity
 * @Author      luoyi
 * @Time        2020/4/10 9:12
 *
 * 更新人       邓益千
 * 更新日期     2020年4月27日
 * 更新内容     根据站点信息，默认选中area
 */
@Route(path = MainARouterPath.MAIN_FOOD_LS)
class FoodLsActivity : TitleBarActivity<ActivityFoodLsBinding, FoodLsViewModel>() {


    var foodLsAdapter: FoodLsAdapter? = null

    //排序弹窗
    var sortPopupWindow: ListPopupWindow<Any>? = null
    val sorts = mModel.sorts
    /**
     * 地区编码
     */
    @JvmField
    @Autowired
    var region: String = ""

    @JvmField
    @Autowired
    var siteId: String? = ""
    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    /**
     * 类型选择弹窗
     */
    private var typeListPopWindow: TypeSelectPopupWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_food_ls
    }

    override fun setTitle(): String {
        return getString(R.string.main_food_title)
    }

    override fun injectVm(): Class<FoodLsViewModel> {
        return FoodLsViewModel::class.java
    }

    override fun initView() {
        foodLsAdapter = FoodLsAdapter(this@FoodLsActivity)
        mBinding.recyFoods.layoutManager =
            LinearLayoutManager(this@FoodLsActivity, LinearLayoutManager.VERTICAL, false)
        mBinding.recyFoods.adapter = foodLsAdapter
        initViewModel()
        initViewEvent()
        initSortPopupWindow()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    private fun initViewEvent() {

        mBinding.swprefreshFoods.setOnRefreshListener {
//            mBinding.swprefreshFoods.isRefreshing = true
            mModel.currPage = 1
            mModel.getFoodsLs()
        }
        foodLsAdapter?.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getFoodsLs()
        }
        foodLsAdapter?.onFoodLsItemClickListener =
            object : FoodLsAdapter.OnFoodLsItemClickListener {
                override fun onCollectClick(id: String, postion: Int, status: Boolean) {
                    if (AppUtils.isLogin()) {
                        showLoadingDialog()
                        if (status) {
                            mModel.collectionCancelScenic(id, postion)
                        } else {
                            mModel.collectionScenic(id, postion)
                        }
                    } else {
                        ToastUtils.showMessage("该操作需要登录，请先登录")
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }

            }
        mBinding.tvArea.onNoDoubleClick {
            if (areaListPopWindow != null) {
                areaListPopWindow!!.show(mBinding.tvArea)
            }
        }
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        mBinding.tvType.onNoDoubleClick {
            typeListPopWindow?.show(mBinding?.tvType)
        }
        mBinding.txtSearchFood.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()

        }
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 3)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_RESTAURANT)
                .navigation()
        }
        mBinding?.appbarFoodsTop.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                if (p1 >= 0) {
                    mBinding.swprefreshFoods.setEnabled(true);
                } else {
                    mBinding.swprefreshFoods.setEnabled(false);
                }
            }

        })
        mBinding.vTopFoodMapMode.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 3)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_RESTAURANT)
                .navigation()
        }
        mBinding.vFoodsTopToSerach.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }

        mBinding?.recyFoods.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.swprefreshFoods.isEnabled = topRowVerticalPosition >= 0
            }
        })
    }

    /**
     * 初始化排序弹窗
     */
    private fun initSortPopupWindow() {
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, sorts) { item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.sortType = item.value
                showLoadingDialog()
                mModel.getFoodsLs()
            }
        }
        if (typeListPopWindow == null) {
            typeListPopWindow = TypeSelectPopupWindow.getInstance(
                this@FoodLsActivity,
                false,
                object : TypeSelectPopupWindow.WindowDataBack {
                    override fun select(type: String, eqt: String) {
                        // 餐厅类型
                        mModel.type = type
                        // 餐厅设施
                        mModel.eqt = eqt
                        mModel.currPage = 1
                        showLoadingDialog()
                        mModel.getFoodsLs()
                    }

                    override fun reset() {
                        mModel.type = ""
                        mModel.eqt = ""
                        mModel.currPage = 1
//                    showLoadingDialog()
//                    mModel.getFoodsLs()
                    }
                })
            typeListPopWindow!!.firstData = mModel.types
        }
    }

    private fun initViewModel() {
        // 美食类型
        mModel.foodTypesLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "不限"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    typeListPopWindow!!.firstData[0]!!.childList = it
                }
                typeListPopWindow?.secondData = temp

            } catch (e: Exception) {
            }
        })
        // 服务设施
        mModel.foodServiceToolsLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "不限"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    typeListPopWindow!!.firstData[1]!!.childList = it
                }
                typeListPopWindow?.secondData = temp
            } catch (e: Exception) {
            }
        })
        // 美食列表
        mModel.foodList.observe(this, Observer {
            pageDealed(it)
        })
        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow = AreaSelectPopupWindow.getInstance(this, false) { item ->
                    mModel.currPage = 1
                    showLoadingDialog()
                    mBinding.tvArea.text = item.name
                    mModel.region = item.region
                    mModel.areaSiteSwitch = item.siteId
                    mModel.getFoodsLs()
                }
                //站点ID不是空
                if (!region.isNullOrEmpty()) {
                    for (index in it.indices) {
                        //从数据中找出与region匹配的数据
                        if (region == it[index].region) {
                            //默认选中
                            areaListPopWindow!!.defSelected(index)
                            mBinding.tvArea.text = it[index].name
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
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            foodLsAdapter?.notifyCollectStatus(it, false)
        })
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            foodLsAdapter?.notifyCollectStatus(it, true)
        })
        mModel.topAdsLiveData.observe(this, Observer {
            if (it == null || it.adInfo.isNullOrEmpty()) {
                mBinding.vFoodsNoAdv.visibility = View.VISIBLE
                mBinding.vFoodListTopAdv.visibility = View.GONE
            } else {
                mBinding.cbanerFoodTopAdv
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

                if (!it.adInfo.isNullOrEmpty()) {
                    mBinding.vFoodsNoAdv.visibility = View.GONE
                    mBinding.vFoodListTopAdv.visibility = View.VISIBLE
                }
            }
        })
    }


    override fun initData() {
        showLoadingDialog()
        mModel.getFoodLsTopAds()
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String,
                result: String,
                lat: Double,
                lon: Double,
                adcode: String
            ) {
                mModel.lat = lat
                mModel.lng = lon
                foodLsAdapter?.selfLocation = LatLng(lat, lon)
                //站点Id不是空
                if (!region.isNullOrEmpty()) {
//                    mModel.region = region
                }
                if (!siteId.isNullOrEmpty()) {
                    mModel.areaSiteSwitch = siteId!!
                }

                mModel.getFoodsLs()
            }

            override fun onError(errorMsg: String) {
                dissMissLoadingDialog()
            }
        })

        mModel.getFoodTypes()
        mModel.getFoodServiceTools()
        mModel.getChildRegion()

    }

    private fun pageDealed(it: MutableList<FoodBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyFoods.isVisible) {
            mBinding.recyFoods.visibility = View.VISIBLE
        }
//        mBinding.swprefreshFoods.isRefreshing = false
        mBinding.swprefreshFoods.finishRefresh()
        if (mModel.currPage == 1) {
            mBinding.recyFoods.smoothScrollToPosition(0)
            foodLsAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            foodLsAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            foodLsAdapter?.loadEnd()
        } else {
            foodLsAdapter?.loadComplete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            typeListPopWindow = null
            sortPopupWindow = null
            areaListPopWindow = null
            StatisticsRepository.instance.statisticsPage(title, 2)
            GaoDeLocation.getInstance().release()
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.cbanerFoodTopAdv.isVisible) {
            mBinding.cbanerFoodTopAdv.startTurning(3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBinding.cbanerFoodTopAdv.isVisible) {
            mBinding.cbanerFoodTopAdv.stopTurning()
        }
    }
}