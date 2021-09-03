package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.LayoutProviderActivityV2Binding
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   ProviderActivityV2Adapter
 * @Author      luoyi
 * @Time        2020/6/2 15:26
 */
class ProviderActivityV2Adapter :
    RecyclerViewAdapter<LayoutProviderActivityV2Binding, ActivityBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.layout_provider_activity_v2) {
        mContext = context
    }

    override fun setVariable(
        mBinding: LayoutProviderActivityV2Binding,
        position: Int,
        item: ActivityBean
    ) {
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            val images = item.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        GlideModuleUtil.loadDqImageWaterMark(imageUrl, mContext!!, mBinding.imgActivity)
        mBinding.tvActivityName.text = "" + item.name
        // 活动价格
        if (item.money.toDouble() == 0.0 && item.integral == "0") {
            mBinding.tvActivityPrice.text = mContext!!.getString(R.string.provider_order_free)
        } else if (item.money.toDouble() > 0.0) {
            var dMoney = item.money.toDouble()
            if (dMoney % 1 == 0.0) {
                mBinding.tvActivityPrice.text =
                    mContext!!.resources.getString(R.string.yuan) + dMoney.toInt()
            } else {
                mBinding.tvActivityPrice.text =
                    mContext!!.resources.getString(R.string.yuan) + item.money
            }
        } else {
            mBinding.tvActivityPrice.text = "" + item.integral + "积分"
        }
        // 活动时间 & 多少人参加
        var timeStr: String = ""
        if (!item.useStartTime.isNullOrEmpty() && !item.useEndTime.isNullOrEmpty()) {
            var startTime: String? =
                DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd HH:mm", item.useStartTime)
            var endTime: String? =
                DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd HH:mm", item.useEndTime)
            timeStr = "$startTime-$endTime"
        }
        var inputPerson: String = ""
        var status = getActivityStauts(item.activityStatus, item.type)
        if (!item.type.isNullOrEmpty() && item.type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
            // 志愿者
            if (item.alreadySignCount != null && item.alreadySignCount!! > 0) {
                inputPerson = "" + item.alreadySignCount + "参加"
            }
            if (!item.totalStock.isNullOrEmpty() && item.alreadySignCount != null) {
                status += "还剩下${item.totalStock.toInt() - item.alreadySignCount!!}个名额"
            }
        }
        mBinding.tvActivityTime.text =
            DividerTextUtils.convertDotString(StringBuilder(), timeStr, inputPerson)
        // 活动状态 & 标签
        var lbes: MutableList<String> = mutableListOf()

//        if (!status.isNullOrEmpty()) {
//            lbes.add(status)
//        }
        if (!item.tagNames.isNullOrEmpty()) {
            var tags = item.tagNames.split(",")
            if (!tags.isNullOrEmpty()) {
                for (lb in tags) {
                    if (!lb.isNullOrEmpty()) {
                        lbes.add(lb)
                    }
                }
            }
        }
        if (!lbes.isNullOrEmpty()) {
            mBinding.llvHotelSpecialService.setLabels(lbes)
            mBinding.llvHotelSpecialService.visibility = View.VISIBLE
        } else {
            mBinding.llvHotelSpecialService.visibility = View.GONE
        }
        mBinding.root.onNoDoubleClick {
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
    }


    /**
     * @param activityStatus 活动状态
     * @param type 活动类型
     */

    private fun getActivityStauts(activityStatus: String, type: String): String {
        // 活动状态 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
        return when (activityStatus) {
            "0" -> {
                "未开始"
            }
            "1" -> {
                "进行中"
            }
            "2" -> {
                "已结束"
            }
            "3" -> {
                if (type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
                    "招募中"
                } else if (type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                    "预定中"
                } else {
                    "报名中"
                }
            }
            else -> {
                ""
            }
        }
    }
}