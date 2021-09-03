package com.daqsoft.thetravelcloudwithculture.sc.fragment

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.adapter.SCPopularActivitiesAdapter
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner.PageIndicatorAlign
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.databinding.*
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.thetravelcloudwithculture.home.view.gallery.BranchScalePageTransformer
import com.daqsoft.thetravelcloudwithculture.sc.adapter.AdsRecyAdapter
import com.daqsoft.thetravelcloudwithculture.sc.viewholder.FoundAroundHolder
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import com.daqsoft.travelCultureModule.net.MainRepository
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @des 首页菜单和运营位
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class SCHomeBannerAndHappyFragment :
    BaseFragment<FragmentHomeMenuBannerScBinding, SCHomeBannerAndHappyFragmentViewModel>() {
    /**
     * 热门活动的适配器
     */
    private val xBranchGalleryAdapter by lazy { SCPopularActivitiesAdapter() }
    /**
     * 计时器
     */
    var timer: Timer? = null
    /**
     * 计时任务
     */
    var timerTask: TimerTask? = null
    /**
     * 进度
     */
    var progress: Int = 0
    /**
     * 引导坐标
     */
    var index: Int = 0
    /**
     * 进度框数据集
     */
    var progressList = mutableListOf<ProgressBar>()
    /**
     * 安逸内容集合
     */
    var mainSubs: MutableList<SubChanelChildBean> = mutableListOf()
    /**
     * 当前安逸内容集合
     */
    var currentSubs: MutableList<SubChanelChildBean> = mutableListOf()
    /**
     * 安逸内容页码
     */
    var currentPage: Int = 1
    /**
     * 安逸内容适配器
     */
    var happyAdapter = object :
        RecyclerViewAdapter<ItemHomeHappyBinding, SubChanelChildBean>(R.layout.item_home_happy) {
        override fun setVariable(
            mBinding: ItemHomeHappyBinding,
            position: Int,
            item: SubChanelChildBean
        ) {
            var imageUrl: String? = item?.backgroundImg
            //            // 显示图片
            activity?.let {
//                Glide.with(it)
//                    .load(imageUrl)
//                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
//                    .into(mBinding.ivHappy)
                GlideModuleUtil.loadDqImageWaterMark(imageUrl,it,mBinding.ivHappy)
            }
            mBinding.name = item.name
            progressList.add(mBinding.barProgress)
            for (progress in progressList) {
                progress.visibility = View.GONE
            }
            progressList.get(index).visibility = View.VISIBLE
            endTimer()
            if (index < progressList.size) {
                StartTimer(progressList.get(index))
            }
            mBinding.root.onModuleNoDoubleClick(
                resources.getString(R.string.home_city_introduce),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_CONTENT)
                    .withString("channelCode", item.channelCode)
                    .withString("titleStr", item.name)
                    .navigation()
            }
        }
    }

    override fun injectVm(): Class<SCHomeBannerAndHappyFragmentViewModel> =
        SCHomeBannerAndHappyFragmentViewModel::class.java


    override fun getLayout(): Int = R.layout.fragment_home_menu_banner_sc

    override fun initData() {
        mModel.getAds()
        mModel.getHomeModule(Constant.HOME_OPERATION)
//        mModel.getContentList("1")
        mModel.getMainSubSet()
        mModel.getActivityList()

    }

    override fun notifyData() {
        super.notifyData()
        // 广告
        mModel.homeAd.observe(this, Observer { it ->
            var images = mutableListOf<String>()
            if (!it.adInfo.isNullOrEmpty()) {
                var homeAd = it.adInfo
                mBinding.bannerTopAdv.visibility = View.VISIBLE
                for (img in it.adInfo) {
                    if (!img.imgUrl.isNullOrEmpty())
                        images.add(img.imgUrl!!)
                }
                mBinding?.bannerTopAdv
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.holder_img_adv_90
                        }
                    }, images)
                    .setCanLoop(true)
                    .setPointViewVisible(true)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener {
                        // 跳转事件
                        var item = homeAd.get(it)
                        item?.let { adv ->
                            MenuJumpUtils.adJump(adv)
                            StatisticsRepository.instance.statistcsModuleClick(
                                "首页广告",
                                ProviderApi.REGION_ADV
                            )
                        }
                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
            }

        })
        // 功能模块(第一个广告图下面)
        mModel.homeModules.observe(this, Observer {
            if (it.resources != null && !it.resources.isNullOrEmpty()) {
                mBinding.recyclerView.visibility = View.VISIBLE
                adsAdapter = AdsRecyAdapter(context!!, it.resources as MutableList<HomeMenu>)
                val linearLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                mBinding.recyclerView.layoutManager = linearLayoutManager
                mBinding.recyclerView.adapter = adsAdapter
            } else {
                mBinding.recyclerView.visibility = View.GONE
            }
        })
        // 热门活动
        mModel.activities.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeActivity.visibility = View.VISIBLE
                xBranchGalleryAdapter.menus.clear()
                xBranchGalleryAdapter.menus.addAll(it)
                xBranchGalleryAdapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeActivity.visibility = View.GONE
            }
        })
        mModel.mainSubSetLd.observe(this, Observer {
            if (it != null && !it.subset.isNullOrEmpty()) {
                mBinding.llHomeHappy.visibility = View.VISIBLE
                if (it.subset.size > 1) {
                    mBinding.llHappyChange.visibility = View.VISIBLE
                } else {
                    mBinding.llHappyChange.visibility = View.GONE
                }
                progressList.clear()
                endTimer()
                index = 0
                currentPage = 1
                mainSubs.clear()
                mainSubs.addAll(it.subset)
                setCurrentHappyPageData()
            } else {
                mBinding.llHomeHappy.visibility = View.GONE
            }
        })
        mModel.foundArouds.observe(this, Observer {
            val founds = it
            if (!founds.isNullOrEmpty()) {
                mBinding.llHomeFound.visibility = View.VISIBLE
                mBinding.circleIndicator.total = founds.size
                mBinding?.bannerFound
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return FoundAroundHolder(itemView!!, currentLoction!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.item_home_found
                        }
                    }, founds)
                    .setCanLoop(founds.size > 1)
                    .setPointViewVisible(false)
                    .setPageIndicatorAlign(PageIndicatorAlign.ALIGN_PARENT_LEFT)
                    .setOnItemClickListener {
                        StatisticsRepository.instance.statistcsModuleClick(
                            resources.getString(R.string.home_found),
                            ProviderApi.REGION_MAIN_COLUMNS
                        )
                        var path = ""
                        when (founds[it].resourceType) {
                            // 场馆
                            ResourceType.CONTENT_TYPE_VENUE -> {
                                path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                            }
                            // 农家乐
                            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                                path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                            }
                            // 	活动室
                            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                                path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
                            }
                            // 酒店
                            ResourceType.CONTENT_TYPE_HOTEL -> {
                                path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                            }
                            // 景区
                            ResourceType.CONTENT_TYPE_SCENERY -> {
                                path = MainARouterPath.MAIN_SCENIC_DETAIL
                            }
                            // 景点
                            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                                path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
                            }
                            // 餐饮
                            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                                path = MainARouterPath.MAIN_FOOD_DETAIL
                            }
                            // 活动
                            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                                path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                            }
                            // 特产
                            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                                path = MainARouterPath.MAIN_SPECIAL_DETAIL
                            }
                            else -> {
                                toast("功能开发中，敬请期待!")
                            }
                        }
                        if (!path.isNullOrEmpty())
                            ARouter.getInstance().build(path)
                                .withString("id", founds[it].resourceId)
                                .navigation()
                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
                    .setOnPageChangeListener(object : OnPageChangeListener {
                        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                        }

                        override fun onPageSelected(index: Int) {
                            mBinding.circleIndicator.setSteps(index)
                        }

                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView?,
                            newState: Int
                        ) {
                        }

                    })

            } else {
                mBinding.llHomeFound.visibility = View.GONE
            }
        })
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        mBinding.llHomeHappyMore.setOnClickListener {
            ARouter.getInstance().build(ARouterPath.ServiceModule.CONTENT_SUB_ACITIVTY)
                .withString("channelCode", "systemChannel")
                .withString("titleStr", getString(R.string.home_city_introduce))
                .navigation()
        }
        mBinding.llHappyChange.onNoDoubleClick {
            if (!mainSubs.isNullOrEmpty()) {
                index = 0
                progressList.clear()
                endTimer()
                var size = mainSubs.size
                currentPage += 1
                var startPage = (currentPage - 1) * 4
                if (startPage in mainSubs.indices) {
                    setCurrentHappyPageData()
                } else {
                    currentPage = 1
                    setCurrentHappyPageData()
                }
            }
        }
        // 资讯
//        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        mBinding.recyclerView.layoutManager = linearLayoutManager
//        mBinding.recyclerView.adapter = adapter
        // 热门活动
        mBinding.xBranchActivity.setAdapter(xBranchGalleryAdapter)
//        mBinding.xBranchActivity.setPageTransformer(BranchScalePageTransformer())
        mBinding.xBranchActivity.setPageTransformer(false, BranchScalePageTransformer())
        mBinding.xBranchActivity.pageMargin =
            resources.getDimensionPixelSize(com.daqsoft.mainmodule.R.dimen.dp_8)
        // 安逸四川的适配器处理
        var happyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerHomeHappy.apply {
            layoutManager = happyLayoutManager
            adapter = happyAdapter
        }
        // 活动更多
        mBinding.llHomeActivityMore.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_LS)
                .navigation()
        }
        mBinding.ivHomeHappy.setOnClickListener {
            chooseHomeContentBean?.let {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_CONTENT)
                    .withString("channelCode", it.channelCode)
                    .withString("titleStr", it.name)
                    .navigation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        endTimer()
    }

    var currentLoction: LatLng? = null

    public fun getFoundAround(lat: Double, lon: Double) {
        currentLoction = LatLng(lat, lon)
        mModel.getFounds(lat, lon)
    }

    /**
     * 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }

    /**
     * 模块适配器
     */
    private val adapter = object :
        RecyclerViewAdapter<ItemHomeModuleBinding, HomeMenu>(
            R.layout.item_home_module
        ) {
        override fun setVariable(mBinding: ItemHomeModuleBinding, position: Int, item: HomeMenu) {
            mBinding.url = item.imgUrl
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    activity?.let { it1 -> JumpUtils.menuPageJumpUtils(item,childFragmentManager) }
                }
        }

    }

    private var adsAdapter: AdsRecyAdapter? = null

    /**
     * 选中的当前内容
     */
    var chooseHomeContentBean: SubChanelChildBean? = null

    /**
     * 开始计时
     */
    fun StartTimer(progressBar: ProgressBar) {
        if (!currentSubs.isNullOrEmpty() && (index in currentSubs.indices)) {
            var imageUrl = currentSubs[index]?.backgroundImg
            progressBar.max = 5
            progressBar.progress = 0
            chooseHomeContentBean = currentSubs[index]
            activity?.let {
                GlideModuleUtil.loadDqImageWaterMark(imageUrl,it,mBinding.ivHomeHappy)
//                Glide.with(it)
//                    .load(imageUrl)
//                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
//                    .into(mBinding.ivHomeHappy)
            }
            mBinding.happyName = chooseHomeContentBean?.name
            //如果timer和timerTask已经被置null了
            if (timer == null && timerTask == null && chooseHomeContentBean != null) {
                //新建timer和timerTask
                timer = Timer()
                timerTask = object : TimerTask() {
                    override fun run() {
                        //每次progress加一
                        progress++
                        // 如果进度条满了的话就再置0，实现循环
                        if (progress == 5) {
                            progress = 0
                            if (index < currentSubs!!.size) {
                                index++
                            } else {
                                index = 0
                            }
                            handler.sendEmptyMessage(0)
                        }
                        //设置进度条进度
                        progressBar.progress = progress
                    }
                }
                /*开始执行timer,第一个参数是要执行的任务，
            第二个参数是开始的延迟时间（单位毫秒）或者是Date类型的日期，代表开始执行时的系统时间
            第三个参数是计时器两次计时之间的间隔（单位毫秒）*/
                timer?.schedule(timerTask, 1000, 1000)
            }
        } else {
            index = 0
        }
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            happyAdapter.notifyDataSetChanged()
        }
    }

    private fun setCurrentHappyPageData() = try {
        var size = mainSubs.size
        var startPage = (currentPage - 1) * 4
        currentSubs.clear()
        if (startPage + 4 < size) {
            currentSubs.addAll(mainSubs.subList(startPage, startPage + 4))
        } else {
            currentSubs.addAll(mainSubs.subList(startPage, size))
        }
        happyAdapter?.clear()
        happyAdapter?.add(currentSubs)
    } catch (e: Exception) {
    }


    /**
     * 结束计时
     */
    fun endTimer() {
        /*这里很奇怪的是如果仅仅是把值赋成Null的话计时并没有停止，循环一次过后Progress就每次都加二了，循环两次过后就是加三
        * 如果仅仅是cancel掉的话也不能再进行调用了
        * 所以想要彻底重置timer的话需要cancel后再置null*/
        timer?.cancel()
        timerTask?.cancel()
        timer = null
        timerTask = null
    }


}


/**
 * @des 首页第一部分的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class SCHomeBannerAndHappyFragmentViewModel : BaseViewModel() {
    /**
     * 广告
     */
    var homeAd = MutableLiveData<HomeAd>()

    /**
     * 首页模块
     */
    var homeModules = MutableLiveData<HomeModule>()
    /**
     * 活动列表
     */
    val activities = MutableLiveData<MutableList<ActivityBean>>()
    /**
     * 安逸走四川
     */
    var homeContents = MutableLiveData<MutableList<HomeContentBean>>()
    /**
     * 新版安逸走四川
     */
    var mainSubSetLd: MutableLiveData<SubChannelBean> = MutableLiveData()
    var currPage = 1

    var foundArouds = MutableLiveData<MutableList<FoundAroundBean>>()

    /**
     * 获取广告
     */
    fun getAds() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.INDEX_TOP_ADV)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    homeAd.postValue(response.data)
                }
            })
    }

    /**
     * 获取内容（安逸走四川等）
     */
    fun getContentList(currPage: String) {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["channelCode"] = Constant.HOME_CONTENT_TYPE_systemChannel
        param["pageSize"] = "4"
        param["currPage"] = currPage
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    homeContents.postValue(response.datas)

                }
            })
    }

    /**
     * 获取首页模块列表
     */
    fun getHomeModule(location: String) {

        mPresenter.value?.loading = false

        HomeRepository.service.getHomeModule(location).excute(object :
            BaseObserver<HomeModule>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeModule>) {
                homeModules.postValue(response.data)
            }
        })
    }

    /**
     *获取精彩瞬间栏目信息
     */
    fun getMainSubSet() {
        MainRepository.service.getActivitySubset("systemChannel")
            .excute(object : BaseObserver<SubChannelBean>() {
                override fun onSuccess(response: BaseResponse<SubChannelBean>) {
                    mainSubSetLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<SubChannelBean>) {
                    mainSubSetLd.postValue(null)
                }

            })
    }

    /**
     * 获取活动列表
     */
    fun getActivityList() {
        val param = HashMap<String, String>()
        // orderType  为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
        param["orderType"] = "1"
        param["pageSize"] = "3"
        param["currPage"] = "1"
        mPresenter.value?.loading = false
        HomeRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }
            })
    }

    /**
     * 获取周边
     */
    fun getFounds(lat: Double, lon: Double) {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["distance"] = "2"
        param["size"] = "5"
        param["latitude"] = "$lat"
        param["longitude"] = "$lon"
        HomeRepository.service.getFoundList(param)
            .excute(object : BaseObserver<FoundAroundBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<FoundAroundBean>) {
                    foundArouds.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<FoundAroundBean>) {
                    super.onFailed(response)
                }

            })
    }


}