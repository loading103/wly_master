package com.daqsoft.thetravelcloudwithculture.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.AnimatorUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.MineServiceBean
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentMineBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineServieBinding
import com.daqsoft.thetravelcloudwithculture.ui.vm.MineFragmentVm
import com.daqsoft.travelCultureModule.story.story.MineStoryAdapter
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.usermodule.repository.constant.IntentConstant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class MineFragment : BaseFragment<FragmentMineBinding, MineFragmentVm>(), View.OnClickListener {
    private var showNotice=true
    private var readNumber="0";
    lateinit var  animatorUtil: AnimatorUtil
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.mNameTv, R.id.iv_edit_info -> {
                // 点击名字
                ARouter.getInstance().build(ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
                    .navigation()
            }
            R.id.mLoginTv -> {
                // 登录
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
            R.id.ll_collect -> {
                // 收藏
                ARouter.getInstance().build(ARouterPath.UserModule.USER_COLLECTION_ACTIVITY)
                    .navigation()
            }
            R.id.ll_interest -> {
                // 关注
                ARouter.getInstance().build(ARouterPath.UserModule.USER_FOCUS_ACTIVITY)
                    .navigation()
            }
            R.id.ll_story -> {
                // 故事
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }

            }
            R.id.cl_point -> {
                // 积分
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }

            }
        }
    }

    /**
     * 更多服务数据
     */
    var serviceList = ArrayList<MineServiceBean>()

    override fun getLayout(): Int = R.layout.fragment_mine

    override fun initData() {
        mModel.getAds()
        initServiceData()

    }

    var storyAdapter: MineStoryAdapter? = null

    override fun initView() {
        EventBus.getDefault().register(this)
        // 四川站点屏蔽 我的订单
        animatorUtil=AnimatorUtil(activity)
        if (BaseApplication.appArea == "sc") {
            mBinding.mOrderTv.visibility = View.GONE
            mBinding.mConsumptionTv.visibility = View.VISIBLE
        }else   if (BaseApplication.appArea == "ws") {
            mBinding.mOrderTv.visibility = View.VISIBLE
            mBinding.mConsumptionTv.visibility = View.GONE
        } else {
            mBinding.mOrderTv.visibility = View.VISIBLE
            mBinding.mConsumptionTv.visibility = View.GONE
        }
        mBinding.mf = this
        mBinding.llLogin.mNameTv.setOnClickListener(this)
        mBinding.llLogin.ivEditInfo.setOnClickListener(this)
        mBinding.llNoLogin.mLoginTv.setOnClickListener(this)
        mBinding.llLogin.llStory.setOnClickListener(this)
        mBinding.llLogin.llCollect.setOnClickListener(this)
        mBinding.llLogin.llInterest.setOnClickListener(this)
        mBinding.llLogin.clPoint.setOnClickListener(this)
        // 故事
        storyAdapter = MineStoryAdapter(activity!!, 2)
        mModel.storyList.observe(this, Observer {
            storyAdapter!!.clear()
            if (!it.isNullOrEmpty() && it.size > 0) {
                var data = it!![0]
                if (data.storyType == Constant.STORY_TYPE_STORY) {
                    storyAdapter!!.addViewType(com.daqsoft.mainmodule.R.layout.item_story_main)
                } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                    storyAdapter!!.addViewType(com.daqsoft.mainmodule.R.layout.item_story_list_strategy)
                }
                storyAdapter!!.addItem(data)
            }

            storyAdapter!!.notifyDataSetChanged()
        })

        val storyLayoutManager = LinearLayoutManager(
            activity!!, LinearLayoutManager.VERTICAL,
            false
        )

        mBinding.rlStory.layoutManager = storyLayoutManager
        mBinding.rlStory.adapter = storyAdapter
        mBinding.mMineShareTv.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }

        mBinding.ivClose.onNoDoubleClick {
            showNotice=false
            animatorUtil.performAnim(false, mBinding.llNotice)
        }
        mBinding.mTimeStoryTv.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
        }
        mBinding.llLogin.ivUserImg.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
                .navigation()
        }
        mBinding.mActivitiesTv.onNoDoubleClick {
            // 我的活动
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_ORDER_LIST)
                    .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.mOrderTv.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_ELECTRONIC_ORDERS)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        // 广告
        mModel.homeAd.observe(this, Observer {
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
                    .setCanLoop(false)
                    .setPointViewVisible(true)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener {
                        var ad: AdInfo = homeAd.get(it)
                        ad?.let {
                            MenuJumpUtils.adJump(ad)
                        }

                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
            }else{
                mBinding.bannerTopAdv.visibility = View.GONE
            }

        })
    }

    override fun injectVm(): Class<MineFragmentVm> = MineFragmentVm::class.java

    /**
     * 跳转到文化场馆
     */
    val gotoVenuesList: View.OnClickListener = View.OnClickListener {
        ARouter.getInstance()
            .build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY)
            .navigation()
    }

    /**
     * 去到个人信息页面
     */
    val gotoPersonalInformation: View.OnClickListener = View.OnClickListener {
        if (AppUtils.isLogin()) {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
                .navigation()
        } else {
            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }

    /**
     * 去到预订列表页面
     */
    val gotoCharge: View.OnClickListener = View.OnClickListener {
        if (AppUtils.isLogin()) {
            if (BaseApplication.appArea == "sc") {
//                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
//                    .withString("html", StringUtil.getWeChatUrl("https://site241962.c.tsichuan.com/#/user-reservation"))
//                    .navigation()
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                    .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                    .navigation()
            } else {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                    .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                    .navigation()
            }
        } else {
            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }

    /**
     * 去到我的消费码页面
     */
    val gotoConsume: View.OnClickListener = View.OnClickListener {
        if (AppUtils.isLogin()) {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_CONSUME_LIST_ACTIVITY)
                .navigation()
        } else {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }


    /**
     * 初始化更多服务中的数据
     */
    fun initServiceData() {
        var serviceTitles = resources.getStringArray(R.array.mine_service_title)
        var serviceIcons = resources.obtainTypedArray(R.array.mine_service_icon)
        var serviceTypes = resources.getStringArray(R.array.mine_service_type)
        serviceList.clear()
        for ((index, title) in serviceTitles.withIndex()) {
            var service = MineServiceBean(
                title, serviceIcons.getResourceId(index, 0),
                serviceTypes.get(index)
            )
            when (serviceTypes[index]) {
                "legacy" -> { // 非遗传承人
                    if (!SPUtils.getInstance().getString(SPKey.HERITAGE_PEOPLEID).isNullOrEmpty()) {
                        serviceList.add(service)
                    }
                }
                "aicommpent" -> {
                    if(BaseApplication.appArea=="ws"){
                        serviceList.add(service)
                    }else{
                        if (SPUtils.getInstance().getBoolean(SPKey.SITE_IT_ROBOT, false)) {
                            serviceList.add(service)
                        }
                    }
                }
                else -> {
                    serviceList.add(service)
                }
            }
        }

        var service = MineServiceBean(
            "消息中心", R.mipmap.mine_center_service_notification,
            "message"
        )
        serviceList.add(service)
        mBinding.recyclerService.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = serviceAdapter
        }
        serviceAdapter.clear()
        serviceAdapter.add(serviceList)
        serviceAdapter.notifyDataSetChanged()
    }

    /**
     * 适配器更多服务
     */
    var serviceAdapter = object : RecyclerViewAdapter<ItemMineServieBinding, MineServiceBean>(
        R.layout.item_mine_servie
    ) {
        override fun setVariable(
            mBinding: ItemMineServieBinding,
            position: Int,
            item: MineServiceBean
        ) {
            mBinding.item = item
            if(item.name=="消息中心"){
                if(TextUtils.isEmpty(readNumber) || readNumber=="0"){
                    mBinding.tvNumber.visibility=View.GONE
                }else{
                    mBinding.tvNumber.visibility=View.VISIBLE
                    if(readNumber.toInt()>99){
                        mBinding.tvNumber.text="99+"
                    }else{
                        mBinding.tvNumber.text=readNumber
                    }
                }

            }else{
                mBinding.tvNumber.visibility=View.GONE
            }
            try {
                mBinding.ivIcon.setImageResource(item.icon)
            }catch (e:Exception){
                ToastUtils.showMessage(item.icon);
            }

            mBinding.root.setOnClickListener {

                when (item.type) {
                    "refund" -> {

                        ARouter.getInstance().build(ARouterPath.UserModule.MINE_ORDER_REFUND).navigation()
                        // 退款
//                        if(AppUtils.isLogin()){
//                            ARouter.getInstance()
//                                .build(ARouterPath.UserModule.USER_ELECTRONIC_REBACK_ACTIVITY)
//                                .withString(IntentConstant.OBJECT, orderDetail?.id.toString())
//                                .withString(IntentConstant.TYPE, orderDetail!!.orderType)
//                                .navigation()
//                        }
//                        toast("敬请期待")
                    }
                    "complaint" -> {
                        // 我的投诉
                        // 2020/5/20 17:30 产品@栾建 要求改成国家的 12301
                        // https://mucomplain.12301.cn/view/complaintmobile#/valid
                        MenuJumpUtils.gotoComplaint()
//                        if (AppUtils.isLogin()) {
//                            ARouter.getInstance()
//                                .build(ARouterPath.UserModule.USER_COMPLAINT_ACTIVITY)
//                                .navigation()
//                        } else {
//                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
//                            ARouter.getInstance()
//                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                                .navigation()
//                        }
                    }


                    //http://site488314.c.tsichuan.com/#/volunteer-service?source=app&appToken=604198ccdae343e28b4ae3bae4e89373
                    //http://site488314.c-ctc.test.daqsoft.com/#/volunteer-service

                    // http://site488314.c-ctc.test.daqsoft.com/#/volunteer-index?appToken=5547cadf54004ae08dc015f75db12661&source=android
                    // http://site488314.c-ctc.test.daqsoft.com/#/volunteers?source=app&appToken=5547cadf54004ae08dc015f75db12661
                    "volunteer" -> {

                        EventBus.getDefault().post(UpdateTokenEvent())
                        // 志愿服务
                        if (!AppUtils.isLogin()) {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }else{
                            val volunteerStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER, "0")
                            var url = ""
                            if (volunteerStatus == "1") {
                                url =  SPUtils.getInstance().getString(SPKey.H5_DOMAIN) + "/#/volunteers"+"?source=app&appToken=" + SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
                            } else {
                                url = SPUtils.getInstance().getString(SPKey.H5_DOMAIN) + "/#/volunteer-service"+ "?source=app&appToken=" + SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
                            }
                            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("html", StringUtil.getHttpsUrl(url))
                                .withString("mTitle", "志愿服务")
                                .navigation()
                        }

                    }
                    "study" -> {
                        // 我的学习
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(MainARouterPath.MINE_LECTURE_LIST).navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    "requetion" ->{
                        // 我的问答
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(MainARouterPath.MINE_LECTURE_REQ).navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    "credit" -> {
                        // 我的信用
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    "message" -> {
                        // 消息中心
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(ARouterPath.UserModule.USER_MEASSAGE_CENTER_ACTIVITY)
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    "aicommpent" -> {
                        if (AppUtils.isLogin()) {
                            MainARouterPath.toItRobotPage()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                    "online" -> {
                        // 在线客服
                        toast("敬请期待")
                    }
                    "developing" -> {
                        // 发展达人
                        toast("敬请期待")
                    }
                    "mall" -> {
                        // 商城二维码
                        toast("敬请期待")
                    }
                    "play" -> {
                        // 玩转达人
                        toast("敬请期待")
                    }
                    "wallet" -> {
                        // 我的钱包
                        toast("敬请期待")
                    }
                    "feedback" -> {
                        // 意见反馈
//                        toast("敬请期待")
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY)
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }

                    }
                    "legacy" -> {
                        // 我的非遗
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(ARouterPath.LegacyModule.LEGACY_MINE)
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateLoginStatusChange(event: LoginStatusEvent) {
        event?.run {
//            initServiceData()
            if (status == LoginStatusEvent.EXIT_LOGIN) {
                // 退出登录
                initServiceData()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        initUserInfo()
        mModel.siteInfo()
        initMineInfor()
        if (AppUtils.isLogin()) {
            EventBus.getDefault().post(UpdateTokenEvent())
            mModel.getHotStoryList()
            mModel.getNoNumber1()
            mModel.getNoNumberList()
        } else {
            storyAdapter!!.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun initMineInfor() {
        when (SPUtils.getInstance().getInt(SPKey.REALNAMESTATUS)) {
            6 -> {
                mBinding.llRoot.tvNoRealName.visibility = View.GONE
                mBinding.llRoot.tvRealName.visibility = View.VISIBLE
                mBinding.llRoot.tvRealName.text = "已认证"
                mBinding.llRoot.tvRealName.setCompoundDrawablesWithIntrinsicBounds(
                    mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_success),
                    null,
                    null,
                    null
                )
            }
            4 -> {
                mBinding.llRoot.tvNoRealName.visibility = View.GONE
                mBinding.llRoot.tvRealName.visibility = View.VISIBLE
                mBinding.llRoot.tvRealName.text = "审核中"
                mBinding.llRoot.tvRealName.setCompoundDrawablesWithIntrinsicBounds(
                    mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_shenhe),
                    null,
                    null,
                    null
                )
            }
            79 -> {
                mBinding.llRoot.tvNoRealName.visibility = View.GONE
                mBinding.llRoot.tvRealName.visibility = View.VISIBLE
                mBinding.llRoot.tvRealName.text = "审核不通过"
                mBinding.llRoot.tvRealName.setCompoundDrawablesWithIntrinsicBounds(
                    mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_failed),
                    null,
                    null,
                    null
                )
            }
            8 -> {
                mBinding.llRoot.tvNoRealName.visibility = View.GONE
                mBinding.llRoot.tvRealName.visibility = View.VISIBLE
                mBinding.llRoot.tvRealName.text = "已撤回"
                mBinding.llRoot.tvRealName.setCompoundDrawablesWithIntrinsicBounds(
                    mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_cancel),
                    null,
                    null,
                    null
                )
            }
            else -> {
                mBinding.llRoot.tvNoRealName.visibility = View.VISIBLE
                mBinding.llRoot.tvRealName.visibility = View.GONE
            }

        }
        mBinding.llRoot.root.onNoDoubleClick {
            if(!AppUtils.isLogin()){
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }else{
                when (SPUtils.getInstance().getInt(SPKey.REALNAMESTATUS)) {
                    6 -> {
                        // 已实名
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMPLETE_ACTIVITY)
                            .navigation()
                    }
                    4 -> {
                        // 待审核
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_REVIEW_ACTIVITY)
                            .navigation()
                    }
                    79 -> {
                        // 审核未通过
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .navigation()
                    }
                    8 -> {
                        // 已经撤回消息
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .withBoolean("isDraw",true)
                            .navigation()
                    }
                    else -> {
                        // 未实名，审核已撤回
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }
    }

    /**
     * 初始化用户信息
     */
    fun initUserInfo() {
        if (!SPUtils.getInstance().getString(SPUtils.Config.TOKEN).isNullOrEmpty()) {
            mBinding.llLogin.clLogin.visibility = View.VISIBLE
            mBinding.llNoLogin.clNoLogin.visibility = View.GONE
            mModel.getMineCenterInfo()
            mModel.getCurrPoint()
            mModel.getPointTaskInfo()
        } else {
            mBinding.llLogin.clLogin.visibility = View.GONE
            mBinding.llNoLogin.clNoLogin.visibility = View.VISIBLE
        }
    }

    override fun notifyData() {
        super.notifyData()
        // 个人信息
        mModel.info.observe(this, Observer {
            mBinding.llLogin.mNameTv.text =
                SPUtils.getInstance().getString(SPKey.NICK_NAME)
            mBinding.llLogin.mPhoneTv.text =
                SPUtils.getInstance().getString(SPKey.MOBILE)
            Glide.with(this@MineFragment)
                .load(SPUtils.getInstance().getString(SPKey.HEAD_URL))
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(mBinding.llLogin.ivUserImg)
            mBinding.llLogin.tvCollectCount.text =
                it.data?.collectionNum.toString()
            mBinding.llLogin.tvStoryCount.text = it.data?.storyNum.toString()
            mBinding.llLogin.tvInterestCount.text = it.data?.focusNum.toString()
            initServiceData()
        })
        // 积分
        mModel.point.observe(this, Observer {
            if (preScore == 0) {
                preScore = it.currPoint
            }
            if (preScore == it.currPoint && count < 5) {
                mModel.getCurrPoint()
                count++
            }
            Glide.with(this)
                .load(it.icon)
                .placeholder(R.mipmap.mine_center_member_bronze)
                .into(mBinding.llLogin.ivPointIcon)
            mBinding.llLogin.tvPointLevel.text = it.level
            mBinding.llLogin.tvPoint.text = "积分：" + it.currPoint
        })
        // 签到任务
        mModel.taskInfo.observe(this, Observer {

            if (it.notReceiveNum == 0) {
                mBinding.llLogin.tvTaskNoAccept.visibility = View.GONE
            } else {
                mBinding.llLogin.tvTaskNoAccept.visibility = View.VISIBLE
            }
            if (it.signStatus == 1) {
                mBinding.llLogin.tvSignScore.visibility = View.VISIBLE
                mBinding.llLogin.tvSignScore.text = "签到+" + it.signPoint
                mBinding.llLogin.tvSignScore.setOnClickListener {
                    if (mBinding.llLogin.tvSignScore.text != "已签到") {
                        showLoadingDialog()
                        mModel.getCheckIn()
                    }
                }
            } else if (it.signStatus == 2) {
                mBinding.llLogin.tvSignScore.visibility = View.VISIBLE
                mBinding.llLogin.tvSignScore.text = "已签到"
            } else {
                mBinding.llLogin.tvSignScore.visibility = View.GONE
            }

        })
        // 签到结果
        mModel.checkInResult.observe(this, Observer {
            dissMissLoadingDialog()
            preScore = 0
            mModel.getCurrPoint()
            if (it.code == 0) {
                mBinding.llLogin.tvSignScore.text = "已签到"
            }
        })
        mModel.siteBean.observe(this, Observer {
            if (it != null) {
                if (it.vipPlayStatus != "1") {
                    mBinding.llLogin.clPoint.visibility = View.GONE
                }
            } else {
            }
        })

        mModel.noReadNumber.observe(this, Observer {
            if(!TextUtils.isEmpty(it)){
                readNumber=it
                serviceAdapter?.notifyDataSetChanged()
            }

        })
        mModel.noReadList.observe(this, Observer {
            if(!showNotice || it==null){
                return@Observer
            }
            showNotice=false
            if(it.size >0)  {
                animatorUtil.performAnim(true, mBinding.llNotice)
                mBinding.tvNotice.list=it
                mBinding.tvNotice.startScroll()
            }else{
                mBinding.llNotice.visibility=View.GONE
            }
        })

    }

    var count: Int = 0
    var preScore: Int = -1
}