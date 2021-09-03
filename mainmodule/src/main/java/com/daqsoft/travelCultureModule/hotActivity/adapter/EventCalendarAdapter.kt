package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainEventCalendarItemBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.FlowLayout
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textColorResource
import org.w3c.dom.Text
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.travelCultureModule.hotActivity.adapter
 * @date 2020/9/7 17:29
 * @author zp
 * @describe 活动日历 适配器
 */
class EventCalendarAdapter :
    RecyclerViewAdapter<MainEventCalendarItemBinding, ActivityBean>(R.layout.main_event_calendar_item) {

    //    val menus = mutableListOf<ActivityBean>()
    var onHotActPagerClickListener: OnHotActPagerClickListener? = null

    override fun setVariable(
        mBinding: MainEventCalendarItemBinding,
        position: Int,
        item: ActivityBean
    ) {
        showActivitySelect(item, mBinding, position)
    }


    /**
     * 显示选中的活动信息
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun showActivitySelect(
        item: ActivityBean,
        mBinding: MainEventCalendarItemBinding,
        position: Int
    ) {
        // 宣传图
        GlideModuleUtil.loadDqImageWaterMark(
            item.images.getRealImages(),
            mBinding.image.context,
            mBinding.image,
            R.mipmap.placeholder_img_fail_240_180,
            R.mipmap.placeholder_img_fail_240_180,
            RequestOptions().transform(CenterCrop(), RoundedCorners(5))
        )

        // 活动名称
        mBinding.title.text = item.name

        // 活动标签
        val tags = mutableListOf<String>()
        item.faithAuditStatus?.takeIf { it == "1" }?.apply { tags.add("诚信免审") }
        item.faithUseStatus?.takeIf { it == "1" }?.apply { tags.add("诚信优享") }
        item.tagNames?.takeIf { it.isNotEmpty() }
            ?.apply { this.split(",").forEach { tags.add(it) } }
        mBinding.label.removeAllViews()
        tags.forEachIndexed { index, s ->
            createTagTextView(s, mBinding.label, index != tags.size - 1)
        }


        // 收藏状态
        item.userResourceStatus?.let {
            mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
        }

//        // 活动价格
//        if (item.money.toDouble() > 0.0) {
//            mBinding.free.visibility = View.GONE
//            mBinding.groupPrice.visibility = View.VISIBLE
//            var dMoney = item.money.toDouble()
//            if (dMoney % 1 == 0.0) {
//                mBinding.price.text =  dMoney.toInt().toString()
//            } else {
//                mBinding.price.text = item.money
//            }
//            mBinding.unit.text = mBinding.unit.context.resources.getString(com.daqsoft.provider.R.string.provider_chinese_yuan)
//        } else {
//            mBinding.price.text = item.integral
//            mBinding.unit.text = mBinding.unit.context.resources.getString(com.daqsoft.provider.R.string.provider_integral)
//        }

        // 活动价格
        if (item.type == ActivityType.ACTIVITY_TYPE_VOLUNT
            || item.type == ActivityType.ACTIVITY_TYPE_SERVICE
            || item.type == ActivityType.ACTIVITY_TYPE_PLAIN
            || item.activityStatus == "2"
            || (item.stock == "0" && item.totalStock != "0")
        ) {
            // 志愿活动或者活动结束 不显示
            mBinding.free.visibility = View.GONE
            mBinding.groupPrice.visibility = View.GONE
        } else if (item.integral.toInt() > 0) {
            mBinding.free.visibility = View.GONE
            mBinding.groupPrice.visibility = View.VISIBLE
            mBinding.price.text = item.integral
            mBinding.unit.text =
                mBinding.unit.context.resources.getString(com.daqsoft.provider.R.string.provider_integral)
        } else if (item.money.toDouble() > 0.0) {
            mBinding.free.visibility = View.GONE
            mBinding.groupPrice.visibility = View.VISIBLE
            var dMoney = item.money.toDouble()
            if (dMoney % 1 == 0.0) {
                mBinding.price.text = dMoney.toInt().toString()
            } else {
                mBinding.price.text = item.money
            }
            mBinding.unit.text =
                mBinding.unit.context.resources.getString(com.daqsoft.provider.R.string.provider_chinese_yuan)
        } else {
            mBinding.free.visibility = View.VISIBLE
            mBinding.groupPrice.visibility = View.GONE
        }


        // 活动时间
        if (!item.useStartTime.isNullOrEmpty() && !item.useEndTime.isNullOrEmpty()) {
            val startTime = DateUtil.formatDateByString(
                "MM.dd",
                "yyyy-MM-dd HH:mm:ss",
                item.useStartTime
            )
            val endTime =
                DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd HH:mm:ss", item.useEndTime)
            mBinding.period.text = "$startTime-$endTime"
        }

        // 活动状态
        var activityRunName = ""
        var activeStatusBg =
            com.daqsoft.provider.R.drawable.shape_home_popular_activity_tag_bg_green
        when (item.type) {
            // 预订
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                activityRunName = "预订中"
            }
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                activityRunName = "报名中"
                if (!item.signStartTime.isNullOrEmpty() && !item.signEndTime.isNullOrEmpty()) {
                    val startTime: String = item.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = item.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.period.text = "$startTime-$endTime"
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
                if (!item.stock.isNullOrEmpty() && item.stock.toInt() > 0) {
                    //添加剩余人数
                    mBinding.groupRecruit.visibility = View.VISIBLE
                    mBinding.alreadyAmount.text = item.recruitedCount
                    mBinding.totalRecruitment.text = "/${item.totalStock}"
                }
                if (!item.signStartTime.isNullOrEmpty() && !item.signEndTime.isNullOrEmpty()) {
                    val startTime: String = item.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = item.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.period.text = "$startTime-$endTime"
                }
            }

        }
        when (item.activityStatus) {
            "0" -> {
                activityRunName = "未开始"
                activeStatusBg =
                    com.daqsoft.provider.R.drawable.shape_home_popular_activity_tag_bg_black
            }
            "1" -> {
                activityRunName = "进行中"
            }
            "2" -> {
                activityRunName = "已结束"
                activeStatusBg =
                    com.daqsoft.provider.R.drawable.shape_home_popular_activity_tag_bg_black
            }
        }
        mBinding.tvActiveStatus.text = activityRunName
        mBinding.tvActiveStatus.setBackgroundResource(activeStatusBg)


        // 点击事件处理
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


        // 收藏点击事件处理
        RxView.clicks(mBinding.ivCollect)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    if (AppUtils.isLogin()) {
                        if (item.userResourceStatus.collectionStatus) {
                            onHotActPagerClickListener?.onCanceCollect(item.id, position)
                        } else {
                            onHotActPagerClickListener?.onCollect(item.id, position)
                        }
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
    }

    private fun createTagTextView(text: String, parent: FlowLayout, needDrawable: Boolean = true) {
        val tv = TextView(parent.context)
        tv.textSize = 11f
        tv.text = text
        tv.textColorResource = R.color.gray_999999
        if (needDrawable) {
            val drawable = parent.context.resources.getDrawable(R.drawable.shape_round_gray_d2)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tv.setCompoundDrawables(null, null, drawable, null)
            tv.compoundDrawablePadding = 6
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0, 0, 6, 0)
        }
        tv.layoutParams = layoutParams
        parent.addView(tv)
    }

    interface OnHotActPagerClickListener {
        fun onCollect(activityId: String, position: Int)
        fun onCanceCollect(activityId: String, position: Int)
    }
}