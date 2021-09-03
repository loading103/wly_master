package com.daqsoft.thetravelcloudwithculture.sc.adapter

import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.MineServiceBean
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineServieBinding
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   ServiceAdapter
 * @Author      luoyi
 * @Time        2020/9/7 11:51
 */
class ServiceAdapter : RecyclerViewAdapter<ItemMineServieBinding, MineServiceBean>(
    R.layout.item_mine_servie
) {
    override fun setVariable(
        mBinding: ItemMineServieBinding,
        position: Int,
        item: MineServiceBean
    ) {
        mBinding.item = item
        mBinding.ivIcon.setImageResource(item.icon)
        mBinding.root.setOnClickListener {
            when (item.type) {
                "refund" -> {
                    // 退款
                    ToastUtils.showMessage("敬请期待")
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
//                            ToastUtils.showMessageUtils.showMessage("非常抱歉，登录后才能访问~")
//                            ARouter.getInstance()
//                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                                .navigation()
//                        }
                }
                "volunteer" -> {
                    // 志愿服务
                    if (!AppUtils.isLogin()) {
                        ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }else{
                        val volunteerStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER, "0")
                        var url = ""
                        EventBus.getDefault().post(UpdateTokenEvent())
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
                "online" -> {
                    // 在线客服
                    ToastUtils.showMessage("敬请期待")
                }
                "developing" -> {
                    // 发展达人
                    ToastUtils.showMessage("敬请期待")
                }
                "mall" -> {
                    // 商城二维码
                    ToastUtils.showMessage("敬请期待")
                }
                "play" -> {
                    // 玩转达人
                    ToastUtils.showMessage("敬请期待")
                }
                "wallet" -> {
                    // 我的钱包
                    ToastUtils.showMessage("敬请期待")
                }
                "feedback" -> {
                    // 意见反馈
//                        ToastUtils.showMessage("敬请期待")
                    if (AppUtils.isLogin()) {
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY)
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

                "aicommpent" -> {
                    if (SPUtils.getInstance().getBoolean(SPKey.SITE_IT_ROBOT, false)) {
                        if (AppUtils.isLogin()) {
                            MainARouterPath.toItRobotPage()
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

}