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
     * 地址编码
     */
    var mAdCode: String? = ""

    /**
     * 首页头图
     */
    var appIndexLog: HomeAd? = null

    /**
     * 最大缓存池数目
     */
    var maxViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    /**
     *  轮询机器人信息工具
     *   每隔 15s
     */
    var cutdownDisable: Disposable? = null

    /**
     * 5s 后隐藏机器人
     */
    var cutDownHideItRobot: Disposable? = null

    /**
     * 第一次显示机器人
     */
    var isFirstShowItRobot: Boolean = true

    /**
     * 页面是否可见
     */
    var isPageVisible: Boolean = false

    /**
     * 当前定位经纬度
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
                                    ToastUtils.showMessage("非常抱歉，未授权应用获取摄像头权限，无法正常使用二维码扫码功能~")
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
     * 初始化 下拉刷新
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
     * 初始化 recycle view
     */
    private fun initRecycleView() {
        with(mBinding.rvHomeTemplate) {
            // 复用池大小
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
                // 没有配置页面模板信息
//                ToastUtils.showMessage("")
            }
        })
        // 发现身边
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
        // 活动
        mModel.recommendedActivitiesLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCPopularActivitiesStyle
            adapter.viewPager2Adapter.menus.clear()
            adapter.viewPager2Adapter.menus.addAll(it.second)
            adapter.viewPager2Adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(it.first)
        })

        // 精品路线列表
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

        // 精品线路
        mModel.lineList.observe(this, Observer {
            dissMissLoadingDialog()
            val adapter = mModel.templateMaps[it.first] as BoutiqueRouteAdapter
            adapter.dataLine.clear()
            adapter.dataLine.addAll(it.second)
            adapter.notifyItemChanged(0)
        })
        // 导游导览
        mModel.guideInfoData.observe(this, Observer {
            if (it?.second != null) {
                val adapter = mModel.templateMaps[it.first] as TourGuideTitleAdapter
                adapter.tourBean = it.second
                adapter.notifyItemChanged(0)
            }
        })

        // 文旅品牌
        mModel.branchBeanList.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as CultureTourismAdapter
            adapter.cultureTourism.clear()
            adapter.cultureTourism.addAll(it.second)
            adapter.notifyItemChanged(0)
        })

        // 话题
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
        // 城市名片相关数据获取
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


        // 资讯
        mModel.informationLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCInformationStyle
            adapter.informationStyleView.setInformationDataChanged(it.second)
        })

        // 故事标签
        mModel.storyTagLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCStoryStyle
            adapter.storyStyleView.setStoryTypeDataChanged(it.second)
        })

        // 故事
        mModel.storyLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCStoryStyle
            adapter.storyStyleView.setStoryDataChanged(it.second)
        })

        // 城市名片
        mModel.cityCardLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCCityCardStyle
            adapter.cityCardStyleView.setCityCardDataChanged(it.second)
        })

        // 栏目
        mModel.columnLiveData.observe(this, Observer {
            val adapter = mModel.templateMaps[it.first] as SCColumnStyle
            adapter.columnStyleView.setColumnDataChanged(it.second)
        })
        // 机器人
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
     * @param obj 样式实体
     * @param pos 位置信息
     */
    private fun buildIndexTemplateView(obj: StyleTemplate, pos: Int) {
        when (obj.moduleType) {
            // 运营专区
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
            // 菜单
            TemplateApi.MoudleType.menu, TemplateApi.MoudleType.bottomMenu -> {

            }
            // 顶部导航菜单
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
            // 轮播图
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
            // 功能组件
            TemplateApi.MoudleType.component -> {

            }
            // 栏目标题
            TemplateApi.MoudleType.channelTitle -> {
                obj.layoutDetail?.run {
                    when (menuCode) {
                        // 活动
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

                        // 资讯
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

                        // 故事
                        TemplateApi.BusinessType.TIME_STORY -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCStoryStyle(layoutHelper, this)
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getStoryTagList(pos)
                            mModel.getStoryList(pos)
                        }

                        // 城市名片
                        TemplateApi.BusinessType.CITY_CARD -> {
                            val layoutHelper = SingleLayoutHelper()
                            val adapter = SCCityCardStyle(layoutHelper, this)
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getCityCardList(pos)
                        }

                        // 栏目
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
                        // 导游导览
                        TemplateApi.BusinessType.TOUR_GUIDE -> {
                            val adapter = TourGuideTitleAdapter(SingleLayoutHelper()).apply {
                                commonTemlate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getGuideInfo(obj.layoutDetail?.menuValue, pos)
                        }
                        // 精品线路
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
                        // 文旅品牌
                        TemplateApi.BusinessType.BRAND -> {
                            val adapter = CultureTourismAdapter(SingleLayoutHelper()).apply {
                                commonTemplate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getBranchList(pos)
                        }
                        // 话题
                        TemplateApi.BusinessType.CONVERSATION -> {
                            val adapter = TopicAdapter(SingleLayoutHelper()).apply {
                                commonTemplate = obj.layoutDetail
                            }
                            mModel.templateMaps[pos] =
                                adapter as DelegateAdapter.Adapter<RecyclerView.ViewHolder>
                            delegateAdapter.addAdapter(adapter)
                            mModel.getTopicList(pos)
                        }
                        // 发现身边
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
                    Timber.e("获取定位位置出错了")
                }
            })
    }




    private var activityPosition : Int = -1
    /**
     * 活动收藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun activityCollection(event: ActivityCollection) {
        if (activityPosition >= 0){
            mModel.getRecommendedActivities("", activityPosition)
        }
    }
}