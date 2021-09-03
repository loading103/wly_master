package com.daqsoft.thetravelcloudwithculture.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineMenuStyleBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineTopMenuTemplateBinding
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast

/**
 * 个人中心普通菜单适配器
 */
class MineMenuAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemMineMenuStyleBinding>(helper, R.layout.item_mine_menu_style) {
    var menus: MutableList<CommonTemlate> = mutableListOf()

    fun addAll(datas: MutableList<CommonTemlate>) {
        menus.clear()
        menus.addAll(datas)
    }

    override fun bindDataToView(mBinding: ItemMineMenuStyleBinding, position: Int) {
        if (!menus.isNullOrEmpty()) {
            mBinding?.recycleView?.run {
                layoutManager = GridLayoutManager(context, 4)
                adapter = mineTopMenuAdapter
                mineTopMenuAdapter.clear()
                mineTopMenuAdapter.add(menus)
                mineTopMenuAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.MINE_TOP_MENU
    }

    /**
     * 个人中心 更多服务适配器
     */
    private var mineTopMenuAdapter =
        object : RecyclerViewAdapter<ItemMineTopMenuTemplateBinding, CommonTemlate>(R.layout.item_mine_top_menu_template) {
            override fun setVariable(mBinding: ItemMineTopMenuTemplateBinding, position: Int, item: CommonTemlate) {
                Glide.with(mBinding.root.context)
                    .load(item.selectIcon)
                    .into(mBinding.imgScTab)
                mBinding.tvScTab.text = item.name
                mBinding.root.onNoDoubleClick {
                    if (AppUtils.isLogin()) {
                        if (item.externalLink.isNullOrEmpty()) {
                            when (item.menuCode) {
                                //购物退款
                                "EXTERNAL" -> {
                                    ARouter.getInstance().build(ARouterPath.UserModule.MINE_ORDER_REFUND).navigation()
                                }
                                // 我的诉讼
                                "COMPLAINT" -> {
                                    ARouter.getInstance().build(ARouterPath.UserModule.USER_COMPLAINT_ACTIVITY).navigation()
                                }
                                // 机器人
                                "ROBOT" -> {
                                    ARouter.getInstance().build(MainARouterPath.IT_ROBOT_PAGE).navigation()
                                }
                                // 意见反馈
                                "FEEDBACK" -> {
                                    ARouter.getInstance().build(ARouterPath.UserModule.USER_FEED_BACK_ACTIVITY).navigation()
                                }
                                // 我的积分
                                "INTEGRAL" -> {
                                    ARouter.getInstance().build(ARouterPath.IntegralModule.MEMBER_HOME_ACTIVITY).navigation()
                                }
                                // 志愿服务111
                                "VOLUNTEER_SERVICE" -> {
                                    //ARouter.getInstance().build(ARouterPath.VolunteerModule.VOLUNTEER_INDEX).navigation()
//                                    mBinding.root.context.toast("敬请期待")
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
                                // 我的学习
                                "MY_STUDY" -> {
                                    ARouter.getInstance().build(MainARouterPath.MINE_LECTURE_LIST).navigation()
                                }
                                // 我的问答
                                "EQ" -> {
                                    ARouter.getInstance().build(MainARouterPath.MINE_LECTURE_REQ).navigation()
                                }
                                // 非遗传承
                                "INHERITOR" -> {
                                    ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE).navigation()
                                }
                                // 消费码
                                "CONSUMPTION_CODE" -> {
                                    ARouter.getInstance().build(ARouterPath.UserModule.USER_CONSUME_LIST_ACTIVITY).navigation()
                                }
                                // 我的信用
                                "HONESTY" -> {
                                    ARouter.getInstance().build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY).navigation()
                                }
                                // 在线客服
                                "EXTERNAL" -> {
                                    mBinding.root.context.toast("敬请期待")
                                }
                            }
                        } else {
                            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY).withString("mTitle", item.name).withString("html", item.externalLink).navigation()
                        }
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