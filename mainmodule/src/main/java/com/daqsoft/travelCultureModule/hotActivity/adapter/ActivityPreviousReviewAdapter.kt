package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPreviousActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.travelCultureModule.clubActivity.getRealImages
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ActivityPreviousReview
 * @Author      luoyi
 * @Time        2020/6/11 17:40
 */
class ActivityPreviousReviewAdapter : RecyclerViewAdapter<ItemPreviousActivityBinding, ActivityBean> {
    var mContext: Context? = null
    var isShowllAll: Boolean = false

    constructor(context: Context) : super(R.layout.item_previous_activity) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemPreviousActivityBinding, position: Int, item: ActivityBean) {
        item?.let {
            Glide.with(mContext!!).load(item.images.getRealImages())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgPreviousActivity)
            mBinding.tvPreviousActName.text = "" + item.name
            if (!it.classifyName.isNullOrEmpty()) {
                mBinding.tvActivityType.visibility = View.VISIBLE
                mBinding.tvActivityType.text = "" + it.classifyName
            } else {
                mBinding.tvActivityType.visibility = View.GONE
            }
            if (item.money.toDouble() == 0.0 && item.integral == "0") {
                mBinding.tvPreviousActPrice.text = mContext!!.getString(R.string.order_free)
            } else if (item.money.toDouble() > 0.0) {
                var dMoney = item.money.toDouble()
                if (dMoney % 1 == 0.0) {
                    mBinding.tvPreviousActPrice.text = mContext!!.getString(R.string.yuan) + dMoney.toInt()
                } else {
                    mBinding.tvPreviousActPrice.text = mContext!!.getString(R.string.yuan) + item.money
                }
            } else {
                mBinding.tvPreviousActPrice.text = item.integral
            }
        }
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    // 如果有跳转链接 直接跳转webactivity
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
    }

    override fun getItemCount(): Int {
        if (!isShowllAll) {
            if (getData().size >= 4) {
                return 4
            }
        }
        return super.getItemCount()
    }
}