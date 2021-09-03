package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.ItemProviderActivityBinding
import com.daqsoft.provider.databinding.ItemProviderActivityTagBinding
import com.daqsoft.provider.databinding.ItemProviderTagsBinding
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.TimeUtils
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 活动适配器
 * @ClassName   ProviderActivityAdapter
 * @Author      luoyi
 * @Time        2020/4/7 16:52
 */
class ProviderActivityAdapter(context: Context) : RecyclerViewAdapter<ItemProviderActivityBinding, ActivityBean>(R.layout.item_provider_activity) {
    private val mContext: Context = context
    override fun setVariable(mBinding: ItemProviderActivityBinding, position: Int, item: ActivityBean) {
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            val images = item.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        mBinding.url = imageUrl
        mBinding.name = item.name
        // 当活动结束时
        if (item.activityStatus == "2") {
            mBinding.tvIsOver.visibility = View.VISIBLE
            mBinding.ivCollect.visibility = View.GONE
            mBinding.tvPrice.visibility = View.GONE
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.GONE
            mBinding.tvPrice.visibility = View.GONE
        } else {
            mBinding.ivCollect.visibility = View.GONE
            mBinding.tvIsOver.visibility = View.GONE
            mBinding.tvPrice.visibility = View.VISIBLE
            // 显示价格/积分/免费
            if (item.money.toDouble() == 0.0 && item.integral == "0") {
                mBinding.price = mContext.getString(R.string.provider_order_free)
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
        /**
         * 活动特色
         */
        if (!item.remark.isNullOrEmpty()) {
            mBinding.tvRemark.visibility = View.VISIBLE
            mBinding.tvRemark.text = item.remark
        } else {
            mBinding.tvRemark.visibility = View.GONE
        }

        val tags = mutableListOf<String>()
        if (item.faithAuditStatus == "1") {
            tags.add("诚信免审")
        }

        if (item.faithUseStatus == "1") {
            tags.add("诚信优享")
        }

        if (!item.tagNames.isNullOrEmpty() && item.tagNames != null) {
            val tag = item.tagNames.split(",")
            for (el in tag) {
                tags.add(el)
            }
        }

        var startTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useStartTime)
        var endTime = DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd", item.useEndTime)
        mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
        var time = mContext.getString(R.string.provider_order_activity_room_time_stamp_, startTime, endTime)
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
                    mContext.getString(R.string.provider_activity_total_number, item.recruitedCount, item.totalStock)
                mBinding.totalTime = DividerTextUtils.convertString(StringBuilder(), total, time)
                var left = mContext.getString(R.string.provider_activity_rest_number, item.stock.toString())
                mBinding.left = left
                tags.add(0, left)
            }
            else -> {
                mBinding.tvPrice.visibility = if (mBinding.tvPrice.isVisible) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                mBinding.totalTime = time
            }
        }
        tags.add(getActivityStauts(item.activityStatus, item.type))
        if (!tags.isNullOrEmpty()) {
            val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            /**
             * 标签适配器包括 剩余名额，是否开启诚信面审 诚信优享，tags
             */
            val tagAdapter =
                object : RecyclerViewAdapter<ItemProviderActivityTagBinding, String>(R.layout.item_provider_activity_tag) {
                    @SuppressLint("CheckResult")
                    override fun setVariable(mBinding: ItemProviderActivityTagBinding, position: Int, item: String) {

                        if (item == "诚信免审" || item == "诚信优享") {
                            //诚信
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                            mBinding.tvVolunteer.setTextColor( mContext.resources.getColor(R.color.white))

                            if (item == "诚信优享") {
                                var d = mContext.resources.getDrawable(R.mipmap.provider_activity_enjoy)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                            } else {
                                var d = mContext.resources.getDrawable(R.mipmap.provider_activity_exempt)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                            }
                        } else if (item.startsWith("还剩")) {
                            // 剩余
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
                            mBinding.tvVolunteer.setTextColor( mContext.resources.getColor(R.color.white))
                        } else {
                            // tag
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext.getDrawable(R.drawable.shape_label_primary_color_bg_2)
                            mBinding.tvVolunteer.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
                        }
                    }
                }
            tagAdapter.add(tags)
            tagAdapter.emptyViewShow = false
            mBinding.rvTags.layoutManager = tagLayoutManager
            mBinding.rvTags.adapter = tagAdapter
            mBinding.rvTags.visibility = View.VISIBLE
        } else {
            mBinding.rvTags.visibility = View.GONE
        }


//        var address: String? = ""
//        if (!item.cityRegionNames.isNullOrEmpty()) {
//            address = item.cityRegionNames + "·"
//        }
//        if (!item.address.isNullOrEmpty()) {
//            address += item.address + " "
//        }
//        if (!item.resourceNameStr.isNullOrEmpty()) {
//            address += item.resourceNameStr
//        }

        // 活动地址
        val location = StringBuilder()
        item.cityRegionNames?.takeIf { it.isNotBlank() }?.apply {
            location.append(this.split(",").joinToString(separator = "·") { it })
        }
        item.address?.takeIf { it.isNotBlank() }?.apply { location.append(this) }
        item.resourceNameStr?.takeIf { it.isNotBlank() }?.apply {
            location.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
            location.append(this.split(",")[0])
        }
        item.resourceCount?.takeIf { it.toInt() > 1 }
            ?.apply { location.append("等${this}个场所") }
        var address = location.toString()


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
                    //                    if (AppUtils.isLogin()) {
//                        if (item.userResourceStatus.collectionStatus) {
//                            mViewModel.collectionCancel(item.id, position)
//                        } else {
//                            mViewModel.collection(item.id, position)
//                        }
//                    } else {
//                        ToastUtils.showUnLoginMsg()
//                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                            .navigation()
//                    }
                }
            }
    }

    override fun payloadUpdateUi(mBinding: ItemProviderActivityBinding, position: Int, payloads: MutableList<Any>) {
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