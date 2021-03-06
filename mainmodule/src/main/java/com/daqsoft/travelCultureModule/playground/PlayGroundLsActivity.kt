package com.daqsoft.travelCultureModule.playground
import android.util.Log
import com.daqsoft.travelCultureModule.playground.viewmodel.PlayGroundLsViewModel
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
import com.daqsoft.mainmodule.databinding.ActivityPlaygroundLsBinding
import com.daqsoft.provider.bean.FoodBean
import com.daqsoft.provider.bean.PlayGroundBean
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
import com.daqsoft.travelCultureModule.playground.adapter.PlayGroundLsAdapter
import com.daqsoft.travelCultureModule.playground.view.PlayGroundSelectPopupWindow
import com.google.android.material.appbar.AppBarLayout

/**
 * ??????????????????
 */
@Route(path = MainARouterPath.MAIN_PLAYGROUND_LS)
class PlayGroundLsActivity : TitleBarActivity<ActivityPlaygroundLsBinding, PlayGroundLsViewModel>() {

    // ????????????
    @JvmField
    @Autowired
    var region: String = ""

    // ??????Id
    @JvmField
    @Autowired
    var siteId: String? = ""

    var playGroundLsAdapter: PlayGroundLsAdapter? = null
    // ????????????
    var sortPopupWindow: ListPopupWindow<Any>? = null

    val sorts = mModel.sorts

    // ????????????????????????
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    // ??????????????????
    private var playGroundListPopWindow: PlayGroundSelectPopupWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_playground_ls
    }

    override fun setTitle(): String {
        return getString(R.string.main_playground_title)
    }

    override fun injectVm(): Class<PlayGroundLsViewModel> {
        return PlayGroundLsViewModel::class.java
    }

    override fun initView() {
        playGroundLsAdapter = PlayGroundLsAdapter(this@PlayGroundLsActivity)
        mBinding.recyFoods.layoutManager = LinearLayoutManager(this@PlayGroundLsActivity, LinearLayoutManager.VERTICAL, false)
        mBinding.recyFoods.adapter = playGroundLsAdapter
        initViewModel()
        initViewEvent()
        initSortPopupWindow()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    private fun initViewEvent() {
        mBinding.swprefreshFoods.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getPlaygroundLs()
        }
        playGroundLsAdapter?.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getPlaygroundLs()
        }
        playGroundLsAdapter?.onFoodLsItemClickListener =
            object : PlayGroundLsAdapter.OnFoodLsItemClickListener {
                override fun onCollectClick(id: String, postion: Int, status: Boolean) {
                    if (AppUtils.isLogin()) {
                        showLoadingDialog()
                        if (status) {
                            mModel.collectionCancelScenic(id, postion)
                        } else {
                            mModel.collectionScenic(id, postion)
                        }
                    } else {
                        ToastUtils.showMessage("????????????????????????????????????")
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
            playGroundListPopWindow?.show(mBinding?.tvType)
        }
        mBinding.txtSearchFood.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()

        }
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 3)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
                .navigation()
        }
        mBinding?.appbarFoodsTop.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if (p1 >= 0) {
                mBinding.swprefreshFoods.setEnabled(true);
            } else {
                mBinding.swprefreshFoods.setEnabled(false);
            }
        })
        mBinding.vTopFoodMapMode.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 3)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
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
     * ?????????????????????
     */
    private fun initSortPopupWindow() {
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, sorts) { item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.sortType = item.value
                showLoadingDialog()
                Log.e("adCode----------3","1111")
                mModel.getPlaygroundLs()
            }
        }
        if (playGroundListPopWindow == null) {
            playGroundListPopWindow = PlayGroundSelectPopupWindow.getInstance(
                this@PlayGroundLsActivity,
                false,
                object : PlayGroundSelectPopupWindow.WindowDataBack {
                    override fun select(type: String,apply: String, eqt: String, feature: String) {
                        // ????????????
                        mModel.type = type
                        // ????????????
                        mModel.eqt = eqt
                        mModel.applyType = apply
                        mModel.feature = feature
                        mModel.currPage = 1
                        showLoadingDialog()
                        Log.e("adCode----------2","1111")
                        mModel.getPlaygroundLs()
                    }

                    override fun reset() {
                        mModel.type = ""
                        mModel.eqt = ""
                        mModel.currPage = 1
//                    showLoadingDialog()
//                    mModel.getFoodsLs()
                    }
                })
            playGroundListPopWindow!!.firstData = mModel.types
        }
    }
    //eqt=1640&type=entertainmentType3&region=&keyword=&areaSiteSwitch=&sortType=&lng=104.08495&lat=30.50938&applyType=1635&feature=1641&currPage=1&pageSize=10
    private fun initViewModel() {
        // ????????????
        mModel.foodTypesLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                var datas: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "??????"))
                it?.forEach {it1->
                    temp.add(ResourceTypeLabel("", "", "", it1.value, it1.name))
                    datas.add(ResourceTypeLabel("", "", "", it1.value, it1.name))
                }
                playGroundListPopWindow!!.firstData[0]!!.childList = datas
                playGroundListPopWindow?.secondData = temp

            } catch (e: Exception) {
            }
        })
        // ????????????
        mModel.foodServiceToolsLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "??????"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    playGroundListPopWindow!!.firstData[1]!!.childList = it
                }
//                playGroundListPopWindow?.secondData = temp
            } catch (e: Exception) {
            }
        })
        // ????????????
        mModel.playServiceLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "??????"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    playGroundListPopWindow!!.firstData[2]!!.childList = it
                }
//                playGroundListPopWindow?.secondData = temp
            } catch (e: Exception) {
            }
        })
        // ????????????
        mModel.featureLiveData.observe(this, Observer {
            try {
                var temp: MutableList<ResourceTypeLabel> = mutableListOf()
                temp.add(0, ResourceTypeLabel("", "", "", "", "??????"))
                if (!it.isNullOrEmpty()) {
                    temp.addAll(it)
                    playGroundListPopWindow!!.firstData[3]!!.childList = it
                }
//                playGroundListPopWindow?.secondData = temp
            } catch (e: Exception) {
            }
        })
        // ??????????????????
        mModel.playgroundList.observe(this, Observer {
            pageDealed(it)
        })
        // ??????
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "??????", "", "", emptyList()))
                areaListPopWindow = AreaSelectPopupWindow.getInstance(this, false) { item ->
                    mModel.currPage = 1
                    showLoadingDialog()
                    mBinding.tvArea.text = item.name
                    mModel.region = item.region
                    mModel.areaSiteSwitch = item.siteId
                    Log.e("adCode----------1","1111")
                    mModel.getPlaygroundLs()
                }
                // ??????ID?????????
                if (!region.isNullOrEmpty()) {
                    for (index in it.indices) {
                        // ?????????????????????region???????????????
                        if (region == it[index].region) {
                            // ????????????
                            areaListPopWindow!!.defSelected(index)
                            mBinding.tvArea.text = it[index].name
                            break
                        }
                    }
                }


                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "??????", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            playGroundLsAdapter?.notifyCollectStatus(it, false)
        })
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            playGroundLsAdapter?.notifyCollectStatus(it, true)
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
                            // ????????????
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
                playGroundLsAdapter?.selfLocation = LatLng(lat, lon)
                playGroundLsAdapter?.region =adcode
                // ??????Id?????????
                if (!region.isNullOrEmpty()) {
//                    mModel.region = region
                }
                if (!siteId.isNullOrEmpty()) {
                    mModel.areaSiteSwitch = siteId!!
                }

                mModel.getPlaygroundLs()
            }

            override fun onError(errorMsg: String) {
                dissMissLoadingDialog()
            }
        })
        mModel.getFoodTypes()
        mModel.getFoodServiceTools()
        mModel.getEntEqtType()
        mModel.getFeatureType()

//        mModel.getPlaygroundLs()
        mModel.getChildRegion()

    }

    private fun pageDealed(it: MutableList<PlayGroundBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyFoods.isVisible) {
            mBinding.recyFoods.visibility = View.VISIBLE
        }
//        mBinding.swprefreshFoods.isRefreshing = false
        mBinding.swprefreshFoods.finishRefresh()
        if (mModel.currPage == 1) {
            mBinding.recyFoods.smoothScrollToPosition(0)
            playGroundLsAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            playGroundLsAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || playGroundLsAdapter?.getData()?.size==mModel.totleNumber) {
            playGroundLsAdapter?.loadEnd()
        } else {
            playGroundLsAdapter?.loadComplete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            playGroundListPopWindow = null
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