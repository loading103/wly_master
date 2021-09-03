package com.daqsoft.thetravelcloudwithculture.sc.fragment

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.AnimatorUtil
import com.daqsoft.baselib.widgets.PandaRefreshHeader
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.MineServiceBean
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentScMineBinding
import com.daqsoft.thetravelcloudwithculture.sc.adapter.ScMineTabAdapter
import com.daqsoft.thetravelcloudwithculture.sc.adapter.ServiceAdapter
import com.daqsoft.thetravelcloudwithculture.ui.vm.MineFragmentVm
import com.daqsoft.thetravelcloudwithculture.ui.vm.ScHomeModel
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

/**
 * @Description 四川新版个人中心
 * @ClassName   ScMineFragment
 * @Author      luoyi
 * @Time        2020/9/4 16:16
 */
class ScMineFragment : BaseFragment<FragmentScMineBinding, MineFragmentVm>(), View.OnClickListener {


    var count: Int = 0
    var preScore: Int = -1
    lateinit var  animatorUtil: AnimatorUtil
    private var showNotice=true
    /**
     * 更多服务数据
     */
    var serviceList = ArrayList<MineServiceBean>()

    val serviceAdapter: ServiceAdapter by lazy {
        ServiceAdapter().apply {
            emptyViewShow = false
        }
    }

    val mScOrderTabAdapter by lazy {
        ScMineTabAdapter().apply {
            emptyViewShow = false
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sc_mine
    }

    override fun injectVm(): Class<MineFragmentVm> {
        return MineFragmentVm::class.java

    }

    override fun onResume() {
        super.onResume()
        mModel.siteInfo()
        initMineInfor()
        setUserInfoView()
    }

    private fun setUserInfoView() {
        if (AppUtils.isLogin()) {
            mModel.getMineCenterInfo()
            mModel.getCurrPoint()
            mModel.getHotStoryList()
            mModel.getPointTaskInfo()
            // todo 四川通知未打开
//            if(BaseApplication.appArea=="xj" || BaseApplication.appArea=="test" || BaseApplication.appArea=="sc" ){
            mModel.getNoNumber1()
            mModel.getNoNumberList()
//            }
            EventBus.getDefault().post(UpdateTokenEvent())
        } else {
            mBinding.tvUserName.text = getString(R.string.login_or_register)
            mBinding.tvHeaderPhone.text = getString(R.string.mine_welcome_to_zytf)
            mBinding.imgMineHead.setImageResource(R.mipmap.common_user_headpic_default)
            mBinding.tvMineStoryNum.text = "-"
            mBinding.tvMineWriterNum.text = "-"
            mBinding.tvMineCollectNum.text = "-"
            mBinding.tvMineAttentNum.text = "-"
            mBinding.clPoint.visibility = View.GONE
            mBinding.vEmptyTime.visibility = View.VISIBLE
            mBinding.vMineStory.visibility = View.GONE
        }

    }

    override fun initView() {
        animatorUtil= AnimatorUtil(activity)
        initViewModel()
        mBinding.rvGridOrderMenu.layoutManager =
            FullyGridLayoutManager(context!!, 4, FullyGridLayoutManager.VERTICAL, false)
        mBinding.rvGridOrderMenu.adapter = mScOrderTabAdapter
        mScOrderTabAdapter.clear()
        mScOrderTabAdapter.add(ScHomeModel.getMineOrderTab())
        mBinding.clMineLoginInfo.setOnClickListener(this)
        mBinding.rlStoryList.setOnClickListener(this)
        mBinding.rlCollectLs.setOnClickListener(this)
        mBinding.rlAttentLs.setOnClickListener(this)
        mBinding.tvMineAllStory.setOnClickListener(this)
        mBinding.tvEmptyShareTime.setOnClickListener(this)
        mBinding.tvTimeShareStory.setOnClickListener(this)
        mBinding.clPoint.setOnClickListener(this)
        // todo 四川通知未打开
//        if(BaseApplication.appArea=="xj" || BaseApplication.appArea=="test" || BaseApplication.appArea=="sc"){
        mBinding.imgMsgCenter.visibility=View.VISIBLE
//        }
        mBinding.smartRefreshLayout.run {
            setOnRefreshListener {
                mModel.siteInfo()
                setUserInfoView()
                initMineInfor()
            }
            setRefreshHeader(PandaRefreshHeader(context!!))
        }

    }

    private fun initViewModel() {
        mBinding.imgMsgCenter.setOnClickListener {
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
        mModel.noReadNumber.observe(this, Observer {
            if(!TextUtils.isEmpty(it)){
                if(it=="0"){
                    mBinding.tvNumber.visibility=View.GONE
                }else{
                    mBinding.tvNumber.visibility=View.VISIBLE
                    if(it.toInt()>99){
                        mBinding.tvNumber.text="99+"
                    }else{
                        mBinding.tvNumber.text=it
                    }
                }
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

        mBinding.ivClose.onNoDoubleClick {
            showNotice=false
            animatorUtil.performAnim(false, mBinding.llNotice)
        }
        // 个人信息
        mModel.info.observe(this, Observer {
            mBinding.tvUserName.text =
                SPUtils.getInstance().getString(SPKey.NICK_NAME)
            mBinding.tvHeaderPhone.text =
                SPUtils.getInstance().getString(SPKey.MOBILE)
            Glide.with(context!!)
                .load(SPUtils.getInstance().getString(SPKey.HEAD_URL))
                .placeholder(R.mipmap.common_user_headpic_default)
                .into(mBinding.imgMineHead)
            mBinding.tvMineCollectNum.text =
                it.data?.collectionNum.toString()
            mBinding.tvMineStoryNum.text = it.data?.storyNum.toString()
            mBinding.tvMineAttentNum.text = it.data?.focusNum.toString()
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
                .into(mBinding.ivPointIcon)
//            mBinding.tvPointLevel.text = "智游天府(${it.level})"
            mBinding.tvPointLevel.text="${it.level}"
            mBinding.tvPoint.text = "积分：" + it.currPoint
        })
        // 签到任务
        mModel.taskInfo.observe(this, Observer {

            if (it.notReceiveNum == 0) {
                mBinding.tvTaskNoAccept.visibility = View.GONE
            } else {
                mBinding.tvTaskNoAccept.visibility = View.VISIBLE
            }
            if (it.signStatus == 1) {
                mBinding.tvSignScore.visibility = View.VISIBLE
                mBinding.tvSignScore.text = "签到+" + it.signPoint
                mBinding.tvSignScore.setOnClickListener {
                    if (mBinding.tvSignScore.text != "已签到") {
                        showLoadingDialog()
                        mModel.getCheckIn()
                    }
                }
            } else if (it.signStatus == 2) {
                mBinding.tvSignScore.visibility = View.VISIBLE
                mBinding.tvSignScore.text = "已签到"
            } else {
                mBinding.tvSignScore.visibility = View.GONE
            }

        })
        // 签到结果
        mModel.checkInResult.observe(this, Observer {
            dissMissLoadingDialog()
            preScore = 0
            mModel.getCurrPoint()
            if (it.code == 0) {
                mBinding.tvSignScore.text = "已签到"
            }
        })
        mModel.siteBean.observe(this, Observer {
            if (it != null) {
                if (it.vipPlayStatus != "1") {
                    mBinding.clPoint.visibility = View.GONE
                } else {
                    if (AppUtils.isLogin()) {
                        mBinding.clPoint.visibility = View.VISIBLE
                    }
                }
            }
            mBinding.smartRefreshLayout.finishRefresh()

        })
        mModel.storyList.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                mBinding.vEmptyTime.visibility = View.VISIBLE
                mBinding.vMineStory.visibility = View.GONE
            } else {
                mBinding.vEmptyTime.visibility = View.GONE
                mBinding.vMineStory.visibility = View.VISIBLE
                bindStoryUi(it[0])
            }
        })

    }

    private fun bindStoryUi(homeStoryBean: HomeStoryBean?) {
        if (homeStoryBean == null) {
            mBinding.vEmptyTime.visibility = View.VISIBLE
            mBinding.vMineStory.visibility = View.GONE
        } else {
            var imageUrl: String = ""
            if (!homeStoryBean.cover.isNullOrEmpty()) {
                imageUrl = homeStoryBean.cover
            } else if (!homeStoryBean.images.isNullOrEmpty()) {
                imageUrl = homeStoryBean.images[0]
            } else if (!homeStoryBean.videoCover.isNullOrEmpty()) {
                imageUrl = homeStoryBean.videoCover
            }
            if (imageUrl.isNullOrEmpty()) {
                mBinding.imgTimeCover.visibility = View.GONE
            } else {
                mBinding.imgTimeCover.visibility = View.VISIBLE
                Glide.with(context!!).load(imageUrl)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(mBinding.imgTimeCover)
            }
            if (!homeStoryBean.createDate.isNullOrEmpty()) {
                mBinding.tvTime.text = homeStoryBean.createDate
                mBinding.tvTime.visibility = View.VISIBLE
            } else {
                mBinding.tvTime.visibility = View.GONE
            }
            // 判断是否需要添加 标签
            var ssb = SpannableStringBuilder()

            if (!homeStoryBean.tagName.isNullOrEmpty()) {
                ssb.append("#" + homeStoryBean.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(context!!.resources.getColor(com.daqsoft.mainmodule.R.color.colorPrimary)),
                    0,
                    ssb.length,
                    Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(homeStoryBean.content)
            mBinding.tvStoryContent.text = ssb

            if (!ssb.isNullOrEmpty()) {
                mBinding.tvStoryContent.visibility = View.VISIBLE
            } else {
                mBinding.tvStoryContent.visibility = View.GONE
            }

            // 地址
            if (homeStoryBean.resourceRegionName.isNullOrEmpty()) {
                // 判断是否关联地址和类型
                mBinding.tvAddressTag.visibility = View.GONE
            } else {
                mBinding.tvAddressTag.visibility = View.VISIBLE
            }
            mBinding.tvAddressTag.text = "" + DividerTextUtils.convertDotString(
                StringBuilder(), homeStoryBean.resourceRegionName,
                homeStoryBean.resourceName
            )
            mBinding.tvLike.text = "${homeStoryBean.likeNum}"
            mBinding.tvComment.text = "${homeStoryBean.commentNum}"
            mBinding.vMineStory.onNoDoubleClick {
                if (homeStoryBean.storyType == Constant.STORY_TYPE_STORY) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STORY_DETAIL)
                        .withString("id", homeStoryBean.id)
                        .withInt("type", 2)
                        .navigation()
                } else if (homeStoryBean.storyType == Constant.STORY_TYPE_STRATEGY) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                        .withString("id", homeStoryBean.id)
                        .withInt("type", 2)
                        .navigation()
                }

            }

        }
    }

    override fun initData() {
        initServiceData()
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
            if (serviceTypes[index] != "legacy"
                || serviceTypes[index] == "legacy" &&
                !SPUtils.getInstance().getString(SPKey.HERITAGE_PEOPLEID).isNullOrEmpty()
            )
                serviceList.add(service)
        }
        mBinding.rvGridService.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = serviceAdapter
        }
        serviceAdapter.add(serviceList)
        serviceAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cl_mine_login_info -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            R.id.rl_story_list -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            R.id.rl_collect_ls -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_COLLECTION_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            R.id.rl_attent_ls -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_FOCUS_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            R.id.tv_mine_all_story -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            R.id.tv_time_share_story -> {
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
            R.id.tv_empty_share_time -> {
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

}