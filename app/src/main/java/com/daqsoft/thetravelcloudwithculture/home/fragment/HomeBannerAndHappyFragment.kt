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
 * @des ????????????????????????
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HomeBannerAndHappyFragment : BaseFragment<FragmentHomeMenuBannerBinding, HomeBannerAndHappyFragmentViewModel>() {
    /**
     * ????????????????????????
     */
    private val xBranchGalleryAdapter = XActivityGalleryAdapter()
    /**
     * ?????????
     */
    var timer: Timer? = null
    /**
     * ????????????
     */
    var timerTask: TimerTask? = null
    /**
     * ??????
     */
    var progress: Int = 0
    /**
     * ????????????
     */
    var index: Int = 0
    /**
     * ??????????????????
     */
    var progressList = mutableListOf<ProgressBar>()
    /**
     * ?????????????????????
     */
    var happyAdapter = object : RecyclerViewAdapter<ItemHomeHappyBinding, HomeContentBean>(R.layout.item_home_happy) {
        override fun setVariable(mBinding: ItemHomeHappyBinding, position: Int, item: HomeContentBean) {
            var imageUrl: String = item?.getContentCoverImageUrl()
            //            // ????????????
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
                            .withString("contentTitle", "????????????")
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
        // ??????
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
                        // ????????????
                        if(AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("mTitle", "??????")
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
        // ???????????????????????????
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
        // ????????????(????????????????????????)
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
        // ????????????
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
        // ??????
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerView.layoutManager = linearLayoutManager
        mBinding.recyclerView.adapter = adapter
        // ????????????
        mBinding.xBranchActivity.setAdapter(xBranchGalleryAdapter)
        mBinding.xBranchActivity.setPageTransformer(BranchScalePageTransformer())

        // ??????????????????????????????
        var happyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerHomeHappy.apply {
            layoutManager = happyLayoutManager
            adapter = happyAdapter
        }
        // ????????????
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
                        .withString("contentTitle", "????????????")
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
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }

    /**
     * ???????????????
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
     * ?????????????????????
     */
    var chooseHomeContentBean: HomeContentBean? = null

    /**
     * ????????????
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
        //??????timer???timerTask????????????null???
        if (timer == null && timerTask == null) {
            //??????timer???timerTask
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    //??????progress??????
                    progress++
                    // ????????????????????????????????????0???????????????
                    if (progress == 5) {
                        progress = 0
                        if (index < 3) {
                            index++
                        } else {
                            index = 0
                        }
                        handler.sendEmptyMessage(0)
                    }
                    //?????????????????????
                    progressBar.progress = progress
                }
            }
            /*????????????timer,???????????????????????????????????????
            ??????????????????????????????????????????????????????????????????Date??????????????????????????????????????????????????????
            ????????????????????????????????????????????????????????????????????????*/
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
     * ????????????
     */
    fun EndTimer() {
        /*????????????????????????????????????????????????Null????????????????????????????????????????????????Progress??????????????????????????????????????????????????????
        * ???????????????cancel????????????????????????????????????
        * ????????????????????????timer????????????cancel?????????null*/
        timer?.cancel()
        timerTask?.cancel()
        timer = null
        timerTask = null
    }


}


/**
 * @des ?????????????????????viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class HomeBannerAndHappyFragmentViewModel : BaseViewModel() {
    /**
     * ??????
     */
    var homeAd = MutableLiveData<HomeAd>()

    /**
     * ????????????
     */
    var homeModules = MutableLiveData<HomeModule>()
    /**
     * ????????????
     */
    val activities = MutableLiveData<MutableList<ActivityBean>>()
    /**
     * ???????????????
     */
    var homeContents = MutableLiveData<MutableList<HomeContentBean>>()

    var currPage = 1

    /**
     * ????????????
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
     * ????????????????????????????????????
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
     * ????????????????????????
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
     * ??????????????????
     */
    fun getActivityList() {
        val param = HashMap<String, String>()
        // orderType  ??????(????????????) 1 ???????????? 2 ???????????? 3 ????????????(?????????????????????) 4 ?????? 5 ???????????? 6 ????????????
        param["orderType"] = "1"
        mPresenter.value?.loading = true
        HomeRepository.service.getActivityList(param).excute(object : BaseObserver<ActivityBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {
                activities.postValue(response.datas)
            }
        })
    }

    /**
     * ?????????
     */
    var changeContent: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            currPage += 1
            getContentList(currPage.toString())
        }
    }

}