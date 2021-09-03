package com.daqsoft.venuesmodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueActivityBinding
import com.daqsoft.venuesmodule.databinding.ItemVenueActivityTagBinding
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * @Description 文化馆活动适配器
 * @ClassName   VenueActivityAdapter
 * @Author      luoyi
 * @Time        2020/3/27 11:48
 */
class VenueActivityAdapter(context: Context) :
    RecyclerViewAdapter<ItemVenueActivityBinding, ActivityBean>(
        R.layout.item_venue_activity
    ) {
    private val mContext: Context = context


    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemVenueActivityBinding, position: Int, item: ActivityBean) {
        val images = item.images.split(",")
        if (images.isNotEmpty()) {
            mBinding.url = images[0]
        }
        mBinding.name = item.name
        // 当活动结束时
        if (item.activityStatus == "2") {

            mBinding.tvIsOver.visibility = View.VISIBLE
            mBinding.ivCollect.visibility = View.GONE
            mBinding.tvPrice.visibility = View.GONE
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.GONE
        } else {
            mBinding.tvPrice.visibility = View.VISIBLE
            // 显示价格/积分/免费
            if (item.money.toDouble() == 0.0 && item.integral == "0") {
                mBinding.price = mContext.getString(R.string.venue_free)
                mBinding.tvMoneyUnit.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE
            } else if (item.money.toDouble() > 0.0) {
                mBinding.price = item.money
                mBinding.tvMoneyUnit.visibility = View.VISIBLE
                mBinding.tvIntegral.visibility = View.GONE
            } else {
                mBinding.price = item.integral
                mBinding.tvMoneyUnit.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.VISIBLE
            }
        }


        val tags = mutableListOf<String>()
        if (item.faithAuditStatus == "1") {
            tags.add("诚信免审")
        }

        if (item.faithUseStatus == "1") {
            tags.add("诚信优享")
        }

        if (item.tagNames.isNotEmpty()) {
            val tag = item.tagNames.split(",")
            for (el in tag) {
                tags.add(el)
            }
        }
        var startTime = TimeUtils.timeString2MD(item.useStartTime).month.toString() +
                "." + TimeUtils.timeString2MD(item.useStartTime).day.toString()
        var endTime = TimeUtils.timeString2MD(item.useEndTime).month.toString() + "." + TimeUtils.timeString2MD(
            item
                .useEndTime
        ).day.toString()
        mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
        var time = mContext.getString(R.string.venue_activity_room_time_stamp, startTime, endTime)
        when (item.type) {
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                mBinding.tvPrice.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE

                mBinding.totalTime = time
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                mBinding.tvPrice.visibility = View.INVISIBLE
                mBinding.tvIntegral.visibility = View.GONE
                var total =
                    mContext.getString(R.string.venue_activity_total_number, item.recruitedCount, item.totalStock)
                mBinding.totalTime = DividerTextUtils.convertString(StringBuilder(), total, time)
                var left = mContext.getString(R.string.venue_activity_rest_number, item.stock.toString())
                mBinding.left = left
                tags.add(0, left)
            }
            else -> {
                mBinding.tvPrice.visibility = View.VISIBLE
                mBinding.totalTime = time
            }
        }
        val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        /**
         * 标签适配器包括 剩余名额，是否开启诚信面审 诚信优享，tags
         */
        val tagAdapter = VenueTagsAdapter(mContext!!)
        tagAdapter.add(tags)
        mBinding.rvTags.layoutManager = tagLayoutManager
        mBinding.rvTags.adapter = tagAdapter


        var address: String? = ""
        if (!item.cityRegionNames.isNullOrEmpty()) {
            address = item.cityRegionNames + "·"
        }
        if (!item.address.isNullOrEmpty()) {
            address += item.address + " "
        }
        if (!item.resourceNameStr.isNullOrEmpty()) {
            var strs = item.resourceNameStr.split(",")
            var info = ""
            if (!strs.isNullOrEmpty()) {
                info = strs[0]
                if (strs.size > 1) info += "等${strs.size}个场所"
            }
            address += info
        }


        if (address.isNullOrEmpty()) {
            mBinding.tvAddress.visibility = View.GONE
        } else {
            mBinding.address = address
            mBinding.tvAddress.visibility = View.VISIBLE
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

        RxView.clicks(mBinding.ivCollect)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {

                    if (item.userResourceStatus.collectionStatus) {
//                        mViewModel.collectionCancel(item.id,position)
                    } else {
//                        mViewModel.collection(item.id,position)
                    }
                }
            }
    }

    override fun payloadUpdateUi(mBinding: ItemVenueActivityBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0].equals("updateCollect")) {
            val item = getData()[position]
            if (item.userResourceStatus != null) {
                mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
            }
        }
    }

    fun notifyUpdateCollectStatus(position: Int, status: Boolean) {
        try {
            val item = getData()[position]
            if (item.userResourceStatus != null) {
                getData()[position]!!.userResourceStatus!!.collectionStatus = status
                notifyItemChanged(position, "updateCollect")
            }
        } catch (e: Exception) {

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}