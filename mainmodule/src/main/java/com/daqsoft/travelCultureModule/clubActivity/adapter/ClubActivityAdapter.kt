package com.daqsoft.travelCultureModule.clubActivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginStart
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubActivityBean
import java.text.SimpleDateFormat


class ClubActivityAdapter(context: Context) :
    RecyclerViewAdapter<ItemClubActivityBinding, ClubActivityBean>(
        R.layout.item_club_activity
    ) {
    override fun setVariable(
        mBinding: ItemClubActivityBinding,
        position: Int,
        item: ClubActivityBean
    ) {
        mBinding.tvCiaName.text = item.name
        mBinding.tvCiaJifen.text = item.integral
        mBinding.tvCiaTime.text =
            "${setTimeType(item.useStartTime)}-${setTimeType(item.useEndTime)}"
        mBinding.tvCiaAddress.text = item.address

        if (!item.tagNames.isNullOrEmpty()) {
            val tagList = mutableListOf<String>()
            if (item.tagNames.contains(",")) {
                tagList.addAll(item.tagNames.split(","))
            } else {
                tagList.add(item.tagNames)
            }

            tagList.forEach {
                val tv = View.inflate(
                    mBinding.root.context,
                    R.layout.item_ll_club_activity_tag,
                    null
                ) as TextView
                tv.text = it
                mBinding.llTag.addView(tv,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(ScreenUtil.dip2px(mBinding.root.context, 5f), 0, 0, 0)
                    })
            }

            mBinding.llTag.visibility = View.VISIBLE
        } else {
            mBinding.llTag.visibility = View.GONE
        }
        if (!item.money.isNullOrEmpty() && item.money != "0.0") {
            mBinding.llJf.visibility = View.VISIBLE
            mBinding.tvCiaJifen.text = "" + item.money
            mBinding.tvCiaJifenLabel.text = "元"
        } else if (!item.integral.isNullOrEmpty() && item.integral != "0") {
            mBinding.llJf.visibility = View.VISIBLE
            mBinding.tvCiaJifen.text = "" + item.integral
            mBinding.tvCiaJifenLabel.text = "积分"
        } else {
            mBinding.llJf.visibility = View.VISIBLE
            mBinding.tvCiaJifen.text = "免费"
            mBinding.tvCiaJifenLabel.text = ""
        }


        val status = when (item.activityStatus) {
            "0" -> "未开始"
            "1" -> "进行中"
            "2" -> "已结束"
            else -> "报名中/招募中"
        }
        mBinding.tvCiaStatus.text = status
        var imageurl: String? = ""
        if (!item.images.isNullOrEmpty()) {
            imageurl = item.images.getRealImages()
        }
        setImageUrl(
            mBinding.tvCiaImage, imageurl, AppCompatResources.getDrawable(
                BaseApplication.context, R.drawable
                    .placeholder_img_fail_h300
            ), 5
        )

        mBinding.llItemClubActivity.setOnClickListener {
            //            ARouter.getInstance()
//                .build(MainARouterPath.MAIN_HOT_ACITITY)
//                .withString("id", item.id)
//                .withString("classifyId", item.classifyId)
//                .navigation()
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

    fun setTimeType(time: String): String {
//        return SimpleDateFormat("MM-dd").format(SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").parse(time))
        return time.substring(5, 10).replace("-", ".")
    }
}