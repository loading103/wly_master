package com.daqsoft.thetravelcloudwithculture.sc.adapter

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ValueResBean
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineScTabBinding

/**
 * @Description
 * @ClassName   ScMineTabAdapter
 * @Author      luoyi
 * @Time        2020/9/7 11:22
 */
class ScMineTabAdapter : RecyclerViewAdapter<ItemMineScTabBinding, ValueResBean>(R.layout.item_mine_sc_tab)  {



    override fun setVariable(mBinding: ItemMineScTabBinding, position: Int, item: ValueResBean) {

        mBinding.imgScTab.setImageResource(item.res)
        mBinding.tvScTab.text = item.name
        mBinding.root.onNoDoubleClick {
            when(item.value){
                "appointMent"->{
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
                "activity"->{
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
                "counsum"->{
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
            }
        }

    }
}