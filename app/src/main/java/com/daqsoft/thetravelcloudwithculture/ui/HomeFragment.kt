package com.daqsoft.thetravelcloudwithculture.ui

import android.Manifest
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.uiTemplate.banner.AdsBannerAdapter
import com.daqsoft.provider.uiTemplate.banner.IndexTopBannerAdapter
import com.daqsoft.provider.uiTemplate.menu.topMenu.TopMenuAdapter
import com.daqsoft.provider.uiTemplate.operation.OperationTemplateAdapter
import com.daqsoft.provider.uiTemplate.titleBar.activity.SCPopularActivitiesStyle
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.ActivityCollection
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.BoutiqueRouteAdapter
import com.daqsoft.provider.uiTemplate.titleBar.cityCard.SCCityCardStyle
import com.daqsoft.provider.uiTemplate.titleBar.column.SCColumnStyle
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.CultureTourismAdapter
import com.daqsoft.provider.uiTemplate.titleBar.found.FoundTemplateAdapter
import com.daqsoft.provider.uiTemplate.titleBar.information.SCInformationStyle
import com.daqsoft.provider.uiTemplate.titleBar.story.SCStoryStyle
import com.daqsoft.provider.uiTemplate.titleBar.topic.TopicAdapter
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.TourGuideTitleAdapter
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeTemplateBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardBean
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.vm.HomeFragmentVm
import com.daqsoft.travelCultureModule.itrobot.view.ItRobotWindowView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber
import java.lang.ref.SoftReference
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   HomeFragment
 * @Author      luoyi
 * @Time        2020/10/9 9:32
 */
class HomeFragment : BaseFragment<FragmentHomeTemplateBinding, HomeFragmentVm>() {

    lateinit var delegateAdapter: DelegateAdapter

    /**
     * ????????????
     */
    var mAdCode: String? = ""

    /**
     * ????????????
     */
    var appIndexLog: HomeAd? = null

    /**
     * ?????????????????????
     */
    var maxViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    /**
     *  ???????????????????????????
     *   ?????? 15s
     */
    var cutdownDisable: Disposable? = null

    /**
     * 5s ??????????????????
     */
    var cutDownHideItRobot: Disposable? = null

    /**
     * ????????????????????????
     */
    var isFirstShowItRobot: Boolean = true

    /**
     * ??????????????????
     */
    var isPageVisible: Boolean = false

    /**
     * ?????????????????????
     */
    var currentLocation: LatLng? = null
    var permissions: RxPermissions? = null

    val indexTopBannerAdapter: IndexTopBannerAdapter by lazy {
        IndexTopBannerAdapter(SingleLayoutHelper()).apply {
            onIndexTopBannerItemClickListener = object : IndexTopBannerAdapter.OnIndexTopBannerItemClickListener {
                override fun goToScanCode() {
                    if (permissions != null) {
                        permissions?.request(Manifest.permission.CAMERA)
                            ?.subscribe {
                                if (it) {
                                    ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_SCAN_ACTIVITY)
                                        .navigation()
                                } else {
                                    ToastUtils.showMessage("?????????????????????????????????????????????????????????????????????????????????????????????~")
                                }
                            }

                    }
                }

            }
        }
    }

    var bannerAdapterMaps: MutableList<AdsBannerAdapter> = mutableListOf()

    override fun getLayout(): Int {
        return R.layout.fragment_home_template
    }

    override fun injectVm(): Class<HomeFragmentVm> {
        return HomeFragmentVm::class.java
    }

    override fun initView() {
        permissions = RxPermissions(this)
        initSmatrLayout()
        initViewModel()
        initRecycleView()
        location()
        initViewEvent()

        EventBus.getDefault().register(this)
    }



    private fun initViewEvent() {
        mBinding.itrobotScHomeWindow.onClickHideListener =
            object : ItRobotWindowView.OnClickHideListener {
                override fun onClickHide() {
                    mBinding.imgShowRobot.visibility = View.VISIBLE
                    mBinding.itrobotScHomeWindow.visibility = View.GONE
                    cutDownHideItRobot?.dispose()
                }
            }
        mBinding.imgShowRobot.onNoDoubleClick {
            if (mBinding.itrobotScHomeWindow.isCanVisable) {
                mBinding.itrobotScHomeWindow.visibility = View.VISIBLE
                mBinding.imgShowRobot.visibility = View.GONE
            } else {
                MainARouterPath.toItRobotPage()
            }
            hitItRobotInfo()
        }
    }

    /**
     * ????????? ????????????
     */
    private fun initSmatrLayout() {
        mBinding.rflHome.setOnRefreshListener {
            initData()
        }
        mBinding.rvHomeTemplate.run {
            itemAnimator?.addDuration = 0
            itemAnimator?.removeDuration = 0
            itemAnimator?.changeDuration = 0
            itemAnimator?.moveDuration = 0
        }
    }

    /**
     * ????????? recycle view
     */
    private fun initRecycleView() {
        with(mBinding.rvHomeTemplate) {
            // ???????????????
            val viewPool = RecyclerView.RecycledViewPool()
            setRecycledViewPool(viewPool)
            viewPool.setMaxRecycledViews(0, 20)
            viewPool.setMaxRecycledViews(13, 20)
            viewPool.setMaxRecycledViews(12, 20)

            // layoutManager
            val virtualLayoutManager = VirtualLayoutManager(context)
            layoutManager = virtualLayoutManager
            // adapter
            delegateAdapter = DelegateAdapter(virtualLayoutManager, false)
            adapter = delegateAdapter


            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(this@HomeFragment).resumeRequests()
                    } else {
                        Glide.with(this@HomeFragment).pauseRequests()
                    }
                }
            })
        }
    }


    private fun initViewModel() {
        mModel.homeTemplateLd.observe(this, Observer {
            mBinding.rflHome.finishRefresh()
            delegateAdapter.clear()
            delegateAdapter.addAdapter(indexTopBannerAdapter)

            if (!it.isNullOrEmpty()) {
                for (index in it.indices) {
                    var template = it[index]
                    template?.let { obj ->
                        if (!obj.moduleType.isNullOrEmpty()) {
                            buildIndexTemplateView(obj, index)
                        }
                    }
                }
            } else {
                // ??????????????????????????????
//                ToastUtils.showMessage("")
            }
        })
        // ????????????
        mModel.foundArouds.observe(this, Observer {
            if (it != null) {
                if (!it.second.isNullOrEmpty()) {
                    var adapter = mModel.templateMaps[it.first] as? FoundTemplateAdapter
                    adapter?.founds?.clear()
                    adapter?.founds?.addAll(it.second)
//                    adapter?.notifyDataSetChanged()
                    adapter?.notifyItemChanged(0)
                }
            }
        })
        // ??????
        mModel.recommendedActivitiesLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCPopularActivitiesStyle
            adapter.viewPager2Adapter.menus.clear()
            adapter.viewPager2Adapter.menus.addAll(it.second)
            adapter.viewPager2Adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(it.first)
        })

        // ??????????????????
        mModel.lineTypeList.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.second.size > 0) {
                val adapter = mModel.templateMaps[it.first] as BoutiqueRouteAdapter
                val item = it.second[0]
                item.select = true
                mModel.lineType = item.tagId
                adapter.dataLineType.clear()
                adapter.dataLineType.addAll(it.second)
                adapter.notifyItemChanged(0)
                mModel.getContentList(it.first)
            }
        })

        // ????????????
        mModel.lineList.observe(this, Observer {
            dissMissLoadingDialog()
            val adapter = mModel.templateMaps[it.first] as BoutiqueRouteAdapter
            adapter.dataLine.clear()
            adapter.dataLine.addAll(it.second)
            adapter.notifyItemChanged(0)
        })
        // ????????????
        mModel.guideInfoData.observe(this, Observer {
            if (it?.second != null) {
                val adapter = mModel.templateMaps[it.first] as TourGuideTitleAdapter
                adapter.tourBean = it.second
                adapter.notifyItemChanged(0)
            }
        })

        // ????????????
        mModel.branchBeanList.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as CultureTourismAdapter
            adapter.cultureTourism.clear()
            adapter.cultureTourism.addAll(it.second)
            adapter.notifyItemChanged(0)
        })

        // ??????
        mModel.topicList.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as TopicAdapter
            adapter.topicBean.clear()
            adapter.topicBean.addAll(it.second)
            adapter.notifyItemChanged(0)
        })

        mModel.appIndexAds.observe(this, Observer
        {
            if (it != null) {
                appIndexLog = it
            }
            mModel.getCityCard(mAdCode!!)
        })
        // ??????????????????????????????
        mModel.cityCards.observe(this, Observer
        {

            if (it.size > 0) {
                var item: CityCardBean? = it[0]
                if (item != null) {
                    it[0].appIndexLog = appIndexLog
                }
            }
            indexTopBannerAdapter.datas.clear()
            indexTopBannerAdapter.datas.addAll(it)
            indexTopBannerAdapter.notifyItemChanged(0)
//            delegateAdapter.notifyDataSetChanged()
        })
        mModel.topMenus.observe(this, Observer
        {
            // pos

        })


        // ??????
        mModel.informationLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCInformationStyle
            adapter.informationStyleView.setInformationDataChanged(it.second)
        })

        // ????????????
        mModel.storyTagLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCStoryStyle
            adapter.storyStyleView.setStoryTypeDataChanged(it.second)
        })

        // ??????
        mModel.storyLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCStoryStyle
            adapter.storyStyleView.setStoryDataChanged(it.second)
        })

        // ????????????
        mModel.cityCardLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCCityCardStyle
            adapter.cityCardStyleView.setCityCardDataChanged(it.second)
        })

        // ??????
        mModel.columnLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCColumnStyle
            adapter.columnStyleView.setColumnDataChanged(it.second)
        })
        // ?????????
        mModel.itRobotInfoLiveData.observe(this, Observer {
            if (it != null) {

                if (!it.greetings.isNullOrEmpty()) {
                    mBinding.itrobotScHomeWindow.setData(it)
                    mBinding.itrobotScHomeWindow.visibility = View.VISIBLE
                    mBinding.imgShowRobot.visibility = View.GONE
                    hitItRobotInfo()
                }
            }
            isPageVisible = true
            getItRobotInfo()

        })
    }

    /**
     * @param obj ????????????
     * @param pos ????????????
     */
    private fun buildIndexTemplateView(obj: StyleTemplate, pos: Int) {
        when (obj.moduleType) {
            // ????????????
            TemplateApi.MoudleType.operation -> {
                if (obj.layoutDetail != null) {
                    var adapter: OperationTemplateAdapter =
                        OperationTemplateAdapter(SingleLayoutHelper())
                            .apply {
                                commonTemlate = obj.layoutDetail!!
                            }
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // ??????
            TemplateApi.MoudleType.menu, TemplateApi.MoudleType.bottomMenu -> {

            }
            // ??????????????????
            TemplateApi.MoudleType.topMenu -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    var adapter: TopMenuAdapter = TopMenuAdapter(SingleLayoutHelper()).apply {
                        fragmentManger = SoftReference(childFragmentManager)
                        menus.clear()
                        menus.addAll(obj.layoutDetails)
                    }
                    delegateAdapter.addAdapter(adapter)
                    adapter.notifyItemChanged(0)
                }
            }
            // ?????????
            TemplateApi.MoudleType.carousel -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    var adapter: AdsBannerAdapter = AdsBannerAdapter(LinearLayoutHelper()).apply {
                        adses.clear()
                        adses.addAll(obj.layoutDetails)
                    }
                    bannerAdapterMaps.add(adapter)
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // ????????????
            TemplateApi.MoudleType.component -> {

            }
            // ????????????
            TemplateApi.MoudleType.channelTitle -> {
                obj.layoutDetail?.run {
                    when (menuCode) {
                        // ??????
                        TemplateApi.BusinessType.HOT_ACTIVITY -> {
                            activityPosition = pos

                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCPopularActivitiesStyle(layoutHelper, this)
                            adapter.viewPager2Adapter.setOnCollectionLisener { item, position ->
                                if (item.userResourceStatus.collectionStatus) {
                                    mModel.collectionCancel(item.id, pos)
                                } else {
                                    mModel.collection(item.id, pos)
                                }
                            }
                            mModel.templateMaps[pos] = adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getRecommendedActivities("", pos)
                        }

                        // ??????
                        TemplateApi.BusinessType.INFORMATION -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCInformationStyle(layoutHelper, this)
                            adapter.setChangeTheBatch(object : SCInformationStyle.ChangeTheBatch {
                                override fun changeTheBatch() {
                                    mModel.informationPager++
                                    mModel.getInformationList(pos)
                                }
                            })
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getInformationList(pos)
                        }

                        // ??????
                        TemplateApi.BusinessType.TIME_STORY -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCStoryStyle(layoutHelper, this)
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getStoryTagList(pos)
                            mModel.getStoryList(pos)
                        }

                        // ????????????
                        TemplateApi.BusinessType.CITY_CARD -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCCityCardStyle(layoutHelper, this)
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getCityCardList(pos)
                        }

                        // ??????
                        TemplateApi.BusinessType.CUSTOM_INFORMATION_CHANNEL -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCColumnStyle(layoutHelper, this)
                            adapter.setChangeTheBatch(object : SCColumnStyle.ChangeTheBatch {
                                override fun changeTheBatch() {
                                    mModel.getColumnList(pos)
                                }
                            })
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getColumnList(pos)
                        }
                        // ????????????
                        TemplateApi.BusinessType.TOUR_GUIDE -> {
                            val adapter = TourGuideTitleAdapter(SingleLayoutHelper()).apply {
                                commonTemlate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getGuideInfo(obj.layoutDetail?.menuValue, pos)
                        }
                        // ????????????
                        TemplateApi.BusinessType.ROUTER -> {
                            val adapter = BoutiqueRouteAdapter(SingleLayoutHelper()).apply {
                                commonTemplate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getLineTypeList(pos)
                            adapter.setChangeType(object : BoutiqueRouteAdapter.ChangeType {
                                override fun changeType(type: String) {
                                    mModel.lineType = type
                                    mModel.getContentList(pos)
                                }
                            })
                        }
                        // ????????????
                        TemplateApi.BusinessType.BRAND -> {
                            val adapter = CultureTourismAdapter(SingleLayoutHelper()).apply {
                                commonTemplate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getBranchList(pos)
                        }
                        // ??????
                        TemplateApi.BusinessType.CONVERSATION -> {
                            val adapter = TopicAdapter(SingleLayoutHelper()).apply {
                                commonTemplate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getTopicList(pos)
                        }
                        // ????????????
                        TemplateApi.BusinessType.FOUND_AROUND -> {
                            currentLocation?.let {
                                val adapter: FoundTemplateAdapter =
                                    FoundTemplateAdapter(SingleLayoutHelper()).apply {
                                        commonTemlate = obj.layoutDetail
                                        currentPostion = it
                                    }
                                delegateAdapter.addAdapter(adapter)
                                mModel.templateMaps[pos] = adapter
                                mModel.getFounds(it.latitude, it.longitude, pos)
                            }

                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mModel.getHomeTemplate()
    }

    fun getItRobotInfo() {
        var serviceRobot: Boolean = SPUtils.getInstance().getBoolean(SPKey.SITE_IT_ROBOT, false)
        if (!serviceRobot) {
            return
        }
        if (isFirstShowItRobot) {
            mBinding.imgShowRobot.visibility = View.VISIBLE
        }

        cutdownDisable = null
        cutdownDisable = Observable.interval(
            if (isFirstShowItRobot) {
                2
            } else {
                12
            }, TimeUnit.SECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { isPageVisible }
            .subscribe {
//                isPageVisible = false
                isFirstShowItRobot = false
                mModel?.getItRobotGreeting()
                cutdownDisable?.dispose()
            }

    }

    private fun hitItRobotInfo() {
        cutDownHideItRobot?.dispose()
        cutDownHideItRobot = null
        cutDownHideItRobot = Observable.interval(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { isPageVisible }
            .subscribe {
//                isPageVisible = false
                if (mBinding.imgShowRobot.visibility == View.GONE) {
                    mBinding.imgShowRobot.visibility = View.VISIBLE
                    mBinding.itrobotScHomeWindow.visibility = View.GONE
                }
                cutdownDisable?.dispose()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
        cutdownDisable?.dispose()
        cutDownHideItRobot?.dispose()

        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        try {
            isPageVisible = false
            cutdownDisable?.dispose()
            cutDownHideItRobot?.dispose()
        } catch (e: Exception) {

        }
        bannerAdapterMaps.forEach {
            it?.stopTurning()
        }
    }

    override fun onResume() {
        super.onResume()
        bannerAdapterMaps.forEach {
            it?.startTuring()
        }
        try {
            isPageVisible = true
            getItRobotInfo()
        } catch (e: Exception) {
        }

    }

    private fun location() {
        GaoDeLocation.getInstance()
            .init(context, object : GaoDeLocation.OnGetCurrentLocationLisner {

                override fun onResult(
                    adCode: String, result: String, lat_: Double,
                    lon_: Double, adcode: String
                ) {
                    mAdCode = adCode
                    currentLocation = LatLng(lat_, lon_)
                    mModel.getAppIndexAds()
                    mModel.getHomeTemplate()

                }

                override fun onError(errormsg: String) {
                    mAdCode = BaseApplication.defaultAdCode
                    mModel.getAppIndexAds()
                    mModel.getHomeTemplate()
                    Timber.e("???????????????????????????")
                }
            })
    }




    private var activityPosition : Int = -1
    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun activityCollection(event: ActivityCollection) {
        if (activityPosition >= 0){
            mModel.getRecommendedActivities("", activityPosition)
        }
    }
}