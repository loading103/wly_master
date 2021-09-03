package com.daqsoft.thetravelcloudwithculture.home.fragment

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeMenuBannerBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeHappyBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeModuleBinding
import com.daqsoft.thetravelcloudwithculture.home.adapters.XActivityGalleryAdapter
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.bean.HomeModule
import com.daqsoft.thetravelcloudwithculture.home.view.gallery.BranchScalePageTransformer
import com.daqsoft.thetravelcloudwithculture.ui.MainActivity
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @des 首页菜单和运营位
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HomeBannerAndHappyFragment : BaseFragment<FragmentHomeMenuBannerBinding, HomeBannerAndHappyFragmentViewModel>() {
    /**
     * 热门活动的适配器
     */
    private val xBranchGalleryAdapter = XActivityGalleryAdapter()
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
     * 安逸内容适配器
     */
    var happyAdapter = object : RecyclerViewAdapter<ItemHomeHappyBinding, HomeContentBean>(R.layout.item_home_happy) {
        override fun setVariable(mBinding: ItemHomeHappyBinding, position: Int, item: HomeContentBean) {
            var imageUrl: String = item?.getContentCoverImageUrl()
            //            // 显示图片
            activity?.let {
                Glide.with(it)
                    .load(imageUrl)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(mBinding.ivHappy)
            }
            mBinding.name = item.title
            progressList.add(mBinding.barProgress)
            for (progress in progressList) {
                progress.visibility = View.GONE
            }
            progressList.get(index).visibility = View.VISIBLE
            EndTimer()
            StartTimer(progressList.get(index))
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
    }

    override fun injectVm(): Class<HomeBannerAndHappyFragmentViewModel> =
        HomeBannerAndHappyFragmentViewModel::class.java


    override fun getLayout(): Int = R.layout.fragment_home_menu_banner

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
            var images = mutableListOf<String>()
            if (!it.adInfo.isNullOrEmpty()) {
                var homeAd = it.adInfo
                mBinding.bannerTopAdv.visibility = View.VISIBLE
                for (img in it.adInfo) {
                    if(!img.imgUrl.isNullOrEmpty())
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
                    .setCanLoop(false)
                    .setPointViewVisible(true)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener {
                        // 跳转事件
                        if(AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("mTitle", "详情")
                                .withString("html", homeAd.get(it).jumpUrl)
                                .navigation()
                        }else{
                            ToastUtils.showUnLoginMsg()
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
            }

        })
        // 更新安逸四川的内容
        mModel.homeContents.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeHappy.visibility = View.VISIBLE
                happyAdapter.clear()
                happyAdapter.add(it)
                happyAdapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeHappy.visibility = View.GONE
            }
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
                xBranchGalleryAdapter.menus.clear()
                xBranchGalleryAdapter.menus.addAll(it)
                xBranchGalleryAdapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeActivity.visibility = View.GONE
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
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerView.layoutManager = linearLayoutManager
        mBinding.recyclerView.adapter = adapter
        // 热门活动
        mBinding.xBranchActivity.setAdapter(xBranchGalleryAdapter)
        mBinding.xBranchActivity.setPageTransformer(BranchScalePageTransformer())

        // 安逸四川的适配器处理
        var happyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerHomeHappy.apply {
            layoutManager = happyLayoutManager
            adapter = happyAdapter
        }
        // 活动更多
        mBinding.llHomeActivityMore.setOnClickListener {
            (activity as MainActivity).changeTab(1)
        }
        mBinding.ivHomeHappy.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
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
                    activity?.let { it1 -> JumpUtils.menuPageJumpUtils(item,childFragmentManager)}
                }
        }

    }

    /**
     * 选中的当前内容
     */
    var chooseHomeContentBean: HomeContentBean? = null

    /**
     * 开始计时
     */
    fun StartTimer( progressBar: ProgressBar) {
        var imageUrl = mModel.homeContents.value?.get(index)?.getContentCoverImageUrl()
        progressBar.max = 5
        progressBar.progress = 0
        activity?.let {
            chooseHomeContentBean = mModel.homeContents.value?.get(index)
            Glide.with(it)
                .load(imageUrl)
                .placeholder(R.mipmap.common_image_screen_no_data)
                .into(mBinding.ivHomeHappy)
        }
        mBinding.happyName = mModel.homeContents.value?.get(index)?.title
        //如果timer和timerTask已经被置null了
        if (timer == null && timerTask == null) {
            //新建timer和timerTask
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    //每次progress加一
                    progress++
                    // 如果进度条满了的话就再置0，实现循环
                    if (progress == 5) {
                        progress = 0
                        if (index < 3) {
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
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            happyAdapter.notifyDataSetChanged()
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
class HomeBannerAndHappyFragmentViewModel : BaseViewModel() {
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
        HomeRepository.service.getActivityList(param).excute(object : BaseObserver<ActivityBean>(mPresenter) {
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

}