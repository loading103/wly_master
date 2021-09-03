package com.daqsoft.provider.uiTemplate.titleBar.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.databinding.ItemHomePopularActivityScStyleFourBinding
import com.daqsoft.provider.databinding.ItemHomePopularActivityScStyleThreeBinding
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource

/**
 * @package name：com.daqsoft.provider.activitymodule
 * @date 2020/10/9 14:07
 * @author zp
 * @describe 四川热门活动样式 4
 */
class SCPopularActivitiesStyleAdapterFour : SCPopularActivitiesStyleBaseAdapter<SCPopularActivitiesStyleAdapterFour.RecyclerViewItemHolder>() {

    inner class RecyclerViewItemHolder(val binding: ItemHomePopularActivityScStyleFourBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ItemHomePopularActivityScStyleFourBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_popular_activity_sc_style_four,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {
        bindDataToView(holder.binding,position)
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    private fun bindDataToView(mBinding:ItemHomePopularActivityScStyleFourBinding, position: Int){

        val item = menus[position]


        // item 点击
        mBinding.root.onModuleNoDoubleClick(
            BaseApplication.context.resources.getString(R.string.provider_hot_activity),
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            // 如果有跳转链接 直接跳转webactivity
            if (menus != null && !menus.isNullOrEmpty()) {
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


        // 活动名称
        mBinding.title.text = item.name

        // 活动宣传图
        Glide
            .with(mBinding.root.context)
            .load(item.images.getRealImages())
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)

        // 活动时间
        if (!item.useStartTime.isNullOrEmpty() && !item.useEndTime.isNullOrEmpty()) {
            val startTime = DateUtil.formatDateByString(
                "yyyy.MM.dd",
                "yyyy-MM-dd HH:mm:ss",
                item.useStartTime
            )
            val endTime =
                DateUtil.formatDateByString(
                    "yyyy.MM.dd",
                    "yyyy-MM-dd HH:mm:ss",
                    item.useEndTime
                )
            mBinding.time.text = "$startTime-$endTime"

        }

        // 活动状态
        var activityRunName = ""
        when (item.type) {
            // 预订
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                activityRunName = "预订中"
            }
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                activityRunName = "报名中"
                if (!item.signStartTime.isNullOrEmpty() && !item.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        item.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = item.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.time.text = "$startTime-$endTime"
                }
            }
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                // 普通
                activityRunName = "进行中"
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                // 志愿
                activityRunName = "招募中"
                // 如果有库存
                var tvPeriodText = StringBuilder()
                if (!item.signStartTime.isNullOrEmpty() && !item.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        item.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = item.signEndTime.split(" ")[0].replace("-", ".")
                    tvPeriodText.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
                    tvPeriodText.append("$startTime-$endTime")
                }
                mBinding.time.text = tvPeriodText.toString()
            }
        }
    }
}