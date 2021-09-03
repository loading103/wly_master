package com.daqsoft.thetravelcloudwithculture.ws

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.*
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.uiTemplate.titleBar.activity.ActivitiesStyleFiveAdapter
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.ActivityCollection
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.XJActivityCollectionActivity
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.XJActivityCollectionHome
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeMenuBannerWsBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeHappyBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeModuleWsBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.thetravelcloudwithculture.sc.viewholder.FoundAroundHolder
import com.daqsoft.thetravelcloudwithculture.ui.MainActivity
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.ActivityImaqeHolder
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.RotesImaqeHolder
import com.daqsoft.thetravelcloudwithculture.ws.adapter.WSADbannerAdapter
import com.daqsoft.thetravelcloudwithculture.ws.adapter.WSActivityGalleryAdapter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * @ClassName    WSHomeBannerAndHappyFragment
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/1/4
 */
class WSHomeBannerAndHappyFragment :
    BaseFragment<FragmentHomeMenuBannerWsBinding, WSHomeBannerAndHappyFragmentViewModel>() {
    /**
     * 热门活动的适配器
     */
    private val xBranchGalleryAdapter = WSActivityGalleryAdapter()

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
//    var index: Int = 0
    var currentPos: Int = 0

    /**
     * 进度框数据集
     */
    var progressList = mutableListOf<ProgressBar>()
    var progressMap: HashMap<Int, ProgressBar> = hashMapOf()

    val viewPager2Adapter by lazy {
        WSActivityGalleryAdapter()
    }

    val bannerAdapter by lazy { WSADbannerAdapter() }

    /**
     * 安逸内容适配器
     */
    var happyAdapter = object :
        RecyclerViewAdapter<ItemHomeHappyBinding, HomeContentBean>(R.layout.item_home_happy) {
        override fun setVariable(
            mBinding: ItemHomeHappyBinding,
            position: Int,
            item: HomeContentBean
        ) {

            mBinding.name = item.title

            var imageUrl: String = item?.getContentCoverImageUrl()
            // 显示图片
            activity?.let {
                GlideModuleUtil.loadDqImageWaterMark(imageUrl, it, mBinding.ivHappy)
            }
            progressMap[position] = mBinding.barProgress
            if (position == currentPos) {
                mBinding.barProgress.visibility = View.GONE
                startTimer(progressMap[currentPos]!!, itemCount, imageUrl, item.title)
            } else {
                mBinding.barProgress.visibility = View.GONE
            }
            // 处理进度调
//            EndTimer()
//            if (!progressList.isNullOrEmpty()) {
//                StartTimer(progressList.get(index), itemCount)
//                progressList.add(mBinding.barProgress)
//                for (progress in progressList) {
//                    progress.visibility = View.GONE
//                }
//                progressList.get(index).visibility = View.VISIBLE
//            }
//            if (!item.imageUrls.isNullOrEmpty()) {
//                mBinding.url = item.imageUrls.get(0).url
//                activity?.let {
//                    Glide.with(it)
//                        .load(item.imageUrls.get(0).url)
//                        .placeholder(R.mipmap.common_image_screen_no_data)
//                        .into(mBinding.ivHappy)
//                }
//
//            }


            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (item.contentType.equals("IMAGE")) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                            .withString("id", item.id.toString())
                            .navigation()
                    } else {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                            .withString("id", item.id.toString())
                            .withString("contentTitle", "资讯详情")
                            .navigation()
                    }
                }
        }

        override fun payloadUpdateUi(
            mBinding: ItemHomeHappyBinding,
            position: Int,
            payloads: MutableList<Any>
        ) {
            if (payloads[0] == "updateProgress") {
                var item = getData()[position]
                if (item != null) {
                    if (position != currentPos) {
                        mBinding.barProgress.progress = 0
                        mBinding.barProgress.visibility = View.GONE
                    } else {
                        mBinding.barProgress.progress = 0
                        mBinding.barProgress.visibility = View.GONE
                        var imageUrl: String = item.getContentCoverImageUrl()
                        EndTimer()
                        startTimer(progressMap[position]!!, itemCount, imageUrl, item.title)
                    }
                }
            }
        }
    }

    override fun injectVm(): Class<WSHomeBannerAndHappyFragmentViewModel> =
        WSHomeBannerAndHappyFragmentViewModel::class.java


    override fun getLayout(): Int = R.layout.fragment_home_menu_banner_ws

    override fun initData() {
        mModel.getAds()
        mModel.getHomeModule(Constant.HOME_OPERATION)
        mModel.getContentList("1")
        mModel.getActivityList()

    }

    override fun notifyData() {
        super.notifyData()
        // 广告
        mModel.homeAd.observe(this, Observer {

            bannerAdapter.setDatas(it.adInfo)
            mBinding.banner.apply {
                offscreenPageLimit = 3
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.run {
                    val dm: DisplayMetrics = resources.displayMetrics
                    val width: Int = dm.widthPixels
                    val margin = this@apply.marginLeft + this@apply.marginRight
                    val padding = (width - margin) / 20
                    setPadding(padding, 0, padding, 0)
                    clipToPadding = false
                }
                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
                    override fun transformPage(page: View, position: Float) {
                        val scaleValue = Math.pow(0.9, Math.abs(position).toDouble()).toFloat()
                        val pageWidth = page.width
                        page.translationX = (-pageWidth * 0.9f * position)
                        page.translationZ = -Math.abs(position)
                        page.scaleX = scaleValue
                        page.scaleY = scaleValue
                        val alphaValue = Math.min(1 - Math.abs(position) * 0.2f, 1f)
                        if (position < -2 || position > 2) {
                            page.alpha = 0f
                        } else {
                            page.alpha = alphaValue
                        }
                        val absPosition = Math.abs(position)
                        if (absPosition > 2 && absPosition < 3) {
                            page.alpha = alphaValue
                        }
                    }
                })
                setPageTransformer(compositePageTransformer)
                adapter = bannerAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        val item = bannerAdapter.getData(bannerAdapter.getRealPosition(position))
//                        mBinding.title.text = it.adInfo[position].type
//                        mBinding.content.text = it.adInfo[position].type
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }

                })

                currentItem = 1
            }
            bannerAdapter.notifyDataSetChanged()
            startLoop()
        })
        // 更新安逸四川的内容
        mModel.homeContents.observe(this, Observer {
            /* if (!it.isNullOrEmpty() && !it[0].imageUrls.isNullOrEmpty()) {
                 mBinding.llHomeHappy.visibility = View.VISIBLE
                 chooseHomeContentBean = it[0]
                 happyAdapter.clear()
                 happyAdapter.add(it)
             } else {
                 if (mModel.currPage > 1) {
                     mModel.currPage = 1
                     toast("无更多数据!")
                 } else {
                     mBinding.llHomeHappy.visibility = View.GONE
                 }
             }*/
        })
        // 功能模块(第一个广告图下面)
        mModel.homeModules.observe(this, Observer {
            if (it.resources != null && !it.resources.isNullOrEmpty()) {
                mBinding.recyclerView.visibility = View.VISIBLE
                adapter.clear()
                adapter.add(it.resources as MutableList<HomeMenu>)
                adapter.notifyDataSetChanged()
            } else {
                mBinding.recyclerView.visibility = View.GONE
            }
        })
        // 热门活动
        mModel.activities.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeActivity.visibility = View.VISIBLE
                viewPager2Adapter.menus.clear()
                viewPager2Adapter.menus.addAll(it)
                if (!viewPager2Adapter.menus.isNullOrEmpty()) {
                    mBinding.llHomeActivityStyleFive.llHomePopularActFive.visibility = View.VISIBLE
                    setSplashAdsView(it)
                    var activityAdapter = ActivitiesStyleFiveAdapter().apply {
                        emptyViewShow = false
                    }
                    // 设置 活动列表
                    mBinding.llHomeActivityStyleFive.recyHomePopularActFives.run {
                        adapter = activityAdapter
                        activityAdapter.add(viewPager2Adapter.menus)
                        activityAdapter.onActivityItemClickListener = object : ActivitiesStyleFiveAdapter.OnActivityItemClickListener {
                            override fun OnItemCLick(item: ActivityBean,position: Int) {
                                mBinding.llHomeActivityStyleFive.cbBanner.setCurrentItem(position,true)
                            }
                        }
                    }
                }
                viewPager2Adapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeActivity.visibility = View.GONE
            }
        })

        mModel.collectLiveData.observe(this, Observer {
            if (it >= 0) {
                toast("收藏成功~")
            } else {
                toast("收藏失败，请稍后再试~")
            }
            viewPager2Adapter.notifyUpdateCollectStatus(it, true)
            viewPager2Adapter.notifyDataSetChanged()
        })

        mModel.canceCollectLiveData.observe(this, Observer {
            viewPager2Adapter.notifyUpdateCollectStatus(it, false)
            if (it >= 0) {
                toast("取消收藏成功~")
            } else {
                toast("取消收藏失败，请稍后再试~")
            }
            viewPager2Adapter.notifyDataSetChanged()
        })
        viewPager2Adapter.setOnCollectionLisener { item, position ->
            if (item.userResourceStatus.collectionStatus) {
                mModel.collectionCancel(item.id, position)
            } else {
                mModel.collection(item.id, position)
            }
        }
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
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT)
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
    /**
     * 设置热门活动banner
     */
    private fun setSplashAdsView(adInfos: MutableList<ActivityBean>) {
        mBinding.llHomeActivityStyleFive.cbBanner?.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): ActivityImaqeHolder? {
                return activity?.let { ActivityImaqeHolder(itemView, it) }
            }
            override fun getLayoutId(): Int {
                return R.layout.item_home_banner_activity_five_ws
            }
        }, adInfos as MutableList<ActivityBean>)?.setCanLoop(true)?.startTurning(5000)?.startTurning()
            ?.setOnItemClickListener { pos ->
                try {
                    if (pos < adInfos.size) {
                        var adInfo = adInfos[pos]
                        toActivityDetail(adInfo)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }?.setPageIndicatorPadding(0, 0, 0, resources.getDimension(R.dimen.dp_20).toInt())
            ?.setPointViewVisible(false)?.onPageChangeListener = object : com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }

            override fun onPageSelected(index: Int) {
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

            }

        }
    }
    fun startLoop(initialDelay: Long = 0, period: Long = 3L){
        val count: Int = bannerAdapter.itemCount
        if (count == 0) {
            return
        }
        Observable.interval(initialDelay, period, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Long) {
                    val next: Int = (mBinding.banner.currentItem + 1) % count
                    mBinding.banner.setCurrentItem(next,true)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }


    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        mBinding.llHomeHappyMore.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "systemChannel")
                .navigation()
        }
        // 资讯
//        val linearLayoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)

        val linearLayoutManager = LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerView.layoutManager = linearLayoutManager
        mBinding.recyclerView.adapter = adapter
        // 热门活动
        mBinding.viewPager2.visibility = View.GONE


        // 安逸四川的适配器处理
        happyAdapter?.emptyViewShow = false
        var happyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerHomeHappy.apply {
            layoutManager = happyLayoutManager
            adapter = happyAdapter
        }
        // 活动更多
        mBinding.llHomeActivityMore.setOnClickListener {
            (activity as MainActivity).changeTab(1)
        }
        mBinding.ivHomeHappy.onNoDoubleClick {
            chooseHomeContentBean = happyAdapter.getData()[currentPos]
            if (chooseHomeContentBean != null) {
                if (chooseHomeContentBean!!.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", chooseHomeContentBean!!.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", chooseHomeContentBean!!.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
        }
    }

    private fun toActivityDetail(item: ActivityBean) {
        if (!item.jumpUrl.isNullOrEmpty()) {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", item.jumpName)
                .withString("html", item.jumpUrl)
                .navigation()
        } else {
            when (item.type) {
                // 志愿活动
                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                        .withString("id", item.id)
                        .withString("classifyId", item.classifyId)
                        .navigation()
                }
                // 预订活动
                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                    // 预订
                    when (item.method) {
                        // 付费预订活动
                        ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                .withString("jumpUrl", item.jumpUrl)
                                .navigation()
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", item.id)
                                .withString("classifyId", item.classifyId)
                                .withString("region", item.region)
                                .navigation()
                        }
                    }
                }
                else -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                        .withString("id", item.id)
                        .withString("classifyId", item.classifyId)
                        .navigation()
                }

            }
        }
    }

    /**
     * 模块适配器
     */
    private val adapter = object :
        RecyclerViewAdapter<ItemHomeModuleWsBinding, HomeMenu>(
            R.layout.item_home_module_ws
        ) {
        override fun getItemCount(): Int {
            if (getData().size >= 4) {
                return 4
            }
            return super.getItemCount()
        }

        override fun setVariable(mBinding: ItemHomeModuleWsBinding, position: Int, item: HomeMenu) {
            item?.let {
                mBinding.url = item.imgUrl
                if (!item.externalLink.isNullOrEmpty() && item.externalLink.endsWith("mzx")) {
                    try {
                        var time = SPUtils.getInstance().getString(SPUtils.Config.APP_READ_MRZX, "")
                        if (time.isNullOrEmpty()) {
                            mBinding.vTipPoint.visibility = View.VISIBLE
                        } else {
                            var timeDate: Date? = DateUtil.getFormatDateByString("yyyy-MM-dd", time)
                            if (timeDate != null) {
                                if (!DateUtil.isSameDate(timeDate, Date())) {
                                    mBinding.vTipPoint.visibility = View.VISIBLE
                                } else {
                                    mBinding.vTipPoint.visibility = View.GONE
                                }
                            } else {
                                mBinding.vTipPoint.visibility = View.GONE
                            }
                        }

                    } catch (e: Exception) {
                        mBinding.vTipPoint.visibility = View.GONE
                    }

                } else {
                    mBinding.vTipPoint.visibility = View.GONE
                }
            }

            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (!item.externalLink.isNullOrEmpty() && item.externalLink.endsWith("mzx")) {
                        SPUtils.getInstance().put(
                            SPUtils.Config.APP_READ_MRZX,
                            DateUtil.getDqDateString("yyyy-MM-dd", Date())
                        )
                        mBinding.vTipPoint.visibility = View.GONE
                    }
                    activity?.let { it1 -> JumpUtils.menuPageJumpUtils(item, childFragmentManager) }
                }
        }

    }

    /**
     * 活动收藏 （活动）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun activityCollection(event: XJActivityCollectionActivity) {
        mModel.getActivityList()
    }

    /**
     * 活动收藏 （详情）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun activityCollectionDetail(event: ActivityCollection) {
        mModel.getActivityList()
    }

    /**
     * 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }

    var currentLoction: LatLng? = null
    public fun getFoundAround(lat: Double, lon: Double) {
        currentLoction = LatLng(lat, lon)
        mModel.getFounds(lat, lon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 选中的当前内容
     */
    var chooseHomeContentBean: HomeContentBean? = null

    /**
     * 开始计时
     */
    fun startTimer(progressBar: ProgressBar, count: Int, imageUrl: String, name: String) {
        progressBar.max = 5
        progressBar.progress = 0
        activity?.let {
            GlideModuleUtil.loadDqImageWaterMarkNoPlace(imageUrl, it, mBinding.ivHomeHappy)
        }
        mBinding.happyName = "" + name
        //如果timer和timerTask已经被置null了
        if (timer == null && timerTask == null) {
            //新建timer和timerTask
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    //每次progress加一
                    progress++
                    // 如果进度条满了的话就再置0，实现循环
//                    progressBar.progress = progress
                    if (progress == 5) {
                        // 切换到下一个
                        progress = 0
                        handler.sendEmptyMessage(1)
                    }
                }
            }
            /*开始执行timer,第一个参数是要执行的任务，
            第二个参数是开始的延迟时间（单位毫秒）或者是Date类型的日期，代表开始执行时的系统时间
            第三个参数是计时器两次计时之间的间隔（单位毫秒）*/
            timer?.schedule(timerTask, 1000, 1000)
        }
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == 1) {
                // 切换到下一个
                currentPos += 1
                if (currentPos >= happyAdapter.itemCount) {
                    currentPos = 0
                }
                happyAdapter.notifyItemRangeChanged(0, happyAdapter.itemCount, "updateProgress")
            } else {
                // 修改进度条

            }


        }
    }

    /**
     * 结束计时
     */
    fun EndTimer() {
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
class WSHomeBannerAndHappyFragmentViewModel : BaseViewModel() {
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

    var currPage = 1

    val canceCollectLiveData = MutableLiveData<Int>()
    val collectLiveData = MutableLiveData<Int>()
    var foundArouds = MutableLiveData<MutableList<FoundAroundBean>>()

    /**
     * 获取广告
     */
    fun getAds() {
        mPresenter.value?.loading = true
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
        val param = HashMap<String, String>()
        param["channelCode"] = Constant.HOME_CONTENT_TYPE_systemChannel
        param["pageSize"] = "4"
        param["currPage"] = currPage
        mPresenter?.value?.isNeedToastMessage = false
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

        mPresenter.value?.loading = true

        HomeRepository.service.getHomeModule(location).excute(object :
            BaseObserver<HomeModule>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeModule>) {
                homeModules.postValue(response.data)
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
        mPresenter.value?.loading = true
        HomeRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }
            })
    }

    /**
     * 换一批
     */
    var changeContent: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            currPage += 1
            getContentList(currPage.toString())
        }
    }


    /**
     * 收藏接口
     */
    fun collection(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("收藏成功~")
                    collectLiveData.postValue(position)
                    EventBus.getDefault().post(XJActivityCollectionHome(true))
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    collectLiveData.postValue(-1)

                    toast.postValue("收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_ACTIVITY
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("取消收藏成功~")
                    canceCollectLiveData.postValue(position)
                    EventBus.getDefault().post(XJActivityCollectionHome(false))
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    collectLiveData.postValue(-1)
                    toast.postValue("取消收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 获取周边
     */
    fun getFounds(lat: Double, lon: Double) {
        mPresenter.value?.loading = false
        val param = java.util.HashMap<String, String>()
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