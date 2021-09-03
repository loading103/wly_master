package com.daqsoft.thetravelcloudwithculture.ui.adapter

import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.integralmodule.repository.bean.SiteInfoBean
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.MineUserInfoBean
import com.daqsoft.provider.bean.PersonInfoTemplate
import com.daqsoft.provider.network.home.bean.UserCurrPoint
import com.daqsoft.provider.network.home.bean.UserPointTaskInfoBean
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineMoudleInfoBinding

/**
 * 个人中心顶部信息适配器
 */
class MineInfoAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemMineMoudleInfoBinding>(helper, R.layout.item_mine_moudle_info) {

    // 积分
    var count: Int = 0
    var preScore: Int = -1

    // 个人信息
    var dataUserInfo: MineUserInfoBean? = null

    // 当前积分
    var dataUserCurrPoint: UserCurrPoint? = null

    // 任务信息
    var dataUserPointTask: UserPointTaskInfoBean? = null

    // 签到结果
    var dataCheckIn: Int = -1

    // 站点详情
    var dataSiteInfo: SiteInfoBean? = null

    // 个人资料
    var dataPersonInfoTemplate: PersonInfoTemplate? = null

    override fun bindDataToView(mBinding: ItemMineMoudleInfoBinding, position: Int) {
        if (!AppUtils.isLogin()) {
            mBinding.tvUserName.text = mBinding.root.context.getString(R.string.login_or_register)
            mBinding.tvHeaderPhone.text = mBinding.root.context.getString(R.string.mine_welcome_to_zytf)
            mBinding.imgMineHead.setImageResource(R.mipmap.common_user_headpic_default)
            mBinding.tvMineStoryNum.text = "-"
            mBinding.tvMineCollectNum.text = "-"
            mBinding.tvMineAttentNum.text = "-"
            mBinding.tvMineWriterNum.text = "-"
            mBinding.clPoint.visibility = View.GONE
        }


        // todo 四川通知未打开
//        if(BaseApplication.appArea=="xj" || BaseApplication.appArea=="test" || BaseApplication.appArea=="sc") {
        if(TextUtils.isEmpty(number) || number=="0"){
            mBinding.tvNumber.visibility=View.GONE
            mBinding.tvNumber.text=number
        }else{
            mBinding.tvNumber.visibility=View.VISIBLE
            if(number.toInt()>99){
                mBinding.tvNumber.text="99+"
            }else{
                mBinding.tvNumber.text=number
            }
        }
        mBinding.imgMsgCenter.visibility=View.VISIBLE
//        }

        // 个人信息
        mBinding.imgMineHead.setOnClickListener {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.UserModule.USER_INFORMATION_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        // 故事
        mBinding.rlStoryList.setOnClickListener {
//            if (AppUtils.isLogin()) {
//                ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
//            } else {
//                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
//                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                    .navigation()
//            }
            ARouter.getInstance().build(MainARouterPath.THEME_PROJECT_DETAIL).navigation()
        }
        // 收藏
        mBinding.rlCollectLs.setOnClickListener {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.UserModule.USER_COLLECTION_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        // 收藏
        mBinding.rlAttentLs.setOnClickListener {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.UserModule.USER_FOCUS_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }

        }
        // 积分
        mBinding.clPoint.setOnClickListener {
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
        // 积分
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

        // 个人信息
        dataUserInfo?.let {
            mBinding.tvUserName.text = SPUtils.getInstance().getString(SPKey.NICK_NAME)
            mBinding.tvHeaderPhone.text = SPUtils.getInstance().getString(SPKey.MOBILE)
            Glide.with(mBinding.root.context)
                .load(SPUtils.getInstance().getString(SPKey.HEAD_URL))
                .placeholder(R.mipmap.common_user_headpic_default)
                .into(mBinding.imgMineHead)
            mBinding.tvMineStoryNum.text =
                if (dataUserInfo?.storyNum == null) {
                    "-"
                } else {
                    dataUserInfo?.storyNum.toString()
                }
            mBinding.tvMineCollectNum.text =
                if (dataUserInfo?.collectionNum == null) {
                    "-"
                } else {
                    dataUserInfo?.collectionNum.toString()
                }
            mBinding.tvMineAttentNum.text = if (dataUserInfo?.focusNum == null) {
                "-"
            } else {
                dataUserInfo?.focusNum.toString()
            }
        }


        // 积分
        dataUserCurrPoint?.let {
            if (preScore == 0) {
                preScore = dataUserCurrPoint?.currPoint ?: 0
            }
            if (preScore == dataUserCurrPoint?.currPoint && count < 5) {
                getData?.getData("1")
                count++
            }
            Glide.with(mBinding.root.context)
                .load(dataUserCurrPoint?.icon)
                .placeholder(R.mipmap.mine_center_member_bronze)
                .into(mBinding.ivPointIcon)
            //mBinding.tvPointLevel.text = "智游天府(${it.level})"
            mBinding.tvPointLevel.text = "${dataUserCurrPoint?.level}"
            mBinding.tvPoint.text = "积分：" + dataUserCurrPoint?.currPoint
        }


        // 签到任务
        dataUserPointTask?.let {
            if (dataUserPointTask?.notReceiveNum == 0) {
                mBinding.tvTaskNoAccept.visibility = View.GONE
            } else {
                mBinding.tvTaskNoAccept.visibility = View.VISIBLE
            }
            if (dataUserPointTask?.signStatus == 1) {
                mBinding.tvSignScore.visibility = View.VISIBLE
                mBinding.tvSignScore.text = "签到+" + dataUserPointTask?.signPoint
                mBinding.tvSignScore.setOnClickListener {
                    if (mBinding.tvSignScore.text != "已签到") {
                        getData?.getData("2")
                    }
                }
            } else if (dataUserPointTask?.signStatus == 2) {
                mBinding.tvSignScore.visibility = View.VISIBLE
                mBinding.tvSignScore.text = "已签到"
            } else {
                mBinding.tvSignScore.visibility = View.GONE
            }
        }


        // 签到结果
        if (dataCheckIn != -1) {
            preScore = 0
            getData?.getData("1")
            if (dataCheckIn == 0) {
                mBinding.tvSignScore.text = "已签到"
            }
        }


        // 站点
        dataSiteInfo?.let {
            if (dataSiteInfo?.vipPlayStatus != "1") {
                mBinding.clPoint.visibility = View.GONE
            } else {
                if (AppUtils.isLogin()) {
                    mBinding.clPoint.visibility = View.VISIBLE
                }
            }
        }


        // 个人模板
        dataPersonInfoTemplate?.let {
            if (AppUtils.isLogin()) {
                dataPersonInfoTemplate?.loginBackgroundImage?.let {
                    Glide.with(mBinding.root.context)
                        .load(StringUtil.getImageUrlFill(it, 0, 210))
                        .placeholder(R.mipmap.mine_bg)
                        .into(mBinding.imgMineBg)
                }
            } else {
                dataPersonInfoTemplate?.noLoginBackgroundImage?.let {
                    Glide.with(mBinding.root.context)
                        .load(StringUtil.getImageUrlFill(it, 0, 210))
                        .placeholder(R.mipmap.mine_bg)
                        .into(mBinding.imgMineBg)
                }
                mBinding.tvUserName.text = dataPersonInfoTemplate?.mainTitle
                mBinding.tvHeaderPhone.text = dataPersonInfoTemplate?.subTitle
                mBinding.imgMineHead.setImageResource(R.mipmap.common_user_headpic_default)
                mBinding.tvMineStoryNum.text = "-"
                mBinding.tvMineCollectNum.text = "-"
                mBinding.tvMineAttentNum.text = "-"
                mBinding.tvMineWriterNum.text = "-"
                mBinding.clPoint.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.MINE_STORY
    }

    private var getData: GetData? = null
    private var number: String = ""

    fun setData(getData: GetData) {
        this.getData = getData
    }

    fun setNumber(number: String) {
        this.number=number
    }

    interface GetData {
        fun getData(Str: String)
    }
}