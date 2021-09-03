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
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.databinding.ItemHomePopularActivityScStyleTwoBinding
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource

/**
 * @package name：com.daqsoft.provider.activitymodule
 * @date 2020/10/9 14:07
 * @author zp
 * @describe 四川热门活动样式 1
 */
class SCPopularActivitiesStyleAdapterTwo : SCPopularActivitiesStyleBaseAdapter<SCPopularActivitiesStyleAdapterTwo.RecyclerViewItemHolder>() {

    inner class RecyclerViewItemHolder(val binding: ItemHomePopularActivityScStyleTwoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ItemHomePopularActivityScStyleTwoBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_popular_activity_sc_style_two,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {
        bindDataToView(holder.binding,position)
    }


    @SuppressLint("CutPasteId")
    private fun bindDataToView(mBinding:ItemHomePopularActivityScStyleTwoBinding, position: Int){

        val item = menus[position]

        mBinding.flActivityLabel.removeAllViews()

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

        // 活动价格
        if (item.type == ActivityType.ACTIVITY_TYPE_PLAIN) {
            mBinding.price.visibility = View.GONE
            mBinding.unit.visibility = View.GONE
        } else {
            mBinding.price.visibility = View.VISIBLE
            mBinding.unit.visibility = View.VISIBLE
            if (item.money.toDouble() == 0.0 && item.integral == "0") {
                mBinding.unit.visibility = View.GONE
                mBinding.price.text = mBinding.price.context.getString(R.string.provider_order_free)
            } else if (item.money.toDouble() > 0.0) {
                var dMoney = item.money.toDouble()
                if (dMoney % 1 == 0.0) {
                    mBinding.price.text = dMoney.toInt().toString()
                } else {
                    mBinding.price.text = item.money
                }
                mBinding.unit.text =
                    mBinding.unit.context.resources.getString(R.string.provider_chinese_yuan)
            } else {
                mBinding.price.text = item.integral
                mBinding.unit.text =
                    mBinding.unit.context.resources.getString(R.string.provider_integral)
            }
        }

        // 活动特色
        mBinding.tvActivityFeatures.run {
            if (item.remark.isNullOrEmpty()) {
                this.visibility = View.GONE
            } else {
                this.text = item.remark
            }
        }
        // 活动名称
        mBinding.tvActivityName.text = item.name

        // 活动宣传图
        Glide
            .with(mBinding.root.context)
            .load(item.images.getRealImages())
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)

        // 活动时间
        var time = ""
        if (!item.useStartTime.isNullOrEmpty() && !item.useEndTime.isNullOrEmpty()) {
            val startTime = DateUtil.formatDateByString(
                "MM月dd日",
                "yyyy-MM-dd HH:mm:ss",
                item.useStartTime
            )
            val endTime =
                DateUtil.formatDateByString(
                    "MM月dd日",
                    "yyyy-MM-dd HH:mm:ss",
                    item.useEndTime
                )
            time = "$startTime-$endTime"
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
                    time = "$startTime-$endTime"
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
                if (!item.stock.isNullOrEmpty() && item.stock.toInt() > 0) {
                    //添加剩余人数 label
//                    activityRunName += "·还剩" + activityBean.stock + "个名额"
                    val view = LayoutInflater.from(mBinding.root.context).inflate(
                        R.layout.home_popular_activity_flowlayout_item,
                        mBinding.flActivityLabel,
                        false
                    )
                    val label = view.findViewById<TextView>(R.id.label)
                    label.text = "还剩${item.stock}个名额"
                    label.textColorResource = R.color.ff9e05
                    label.backgroundResource =
                        R.drawable.shape_home_popular_activity_flowlayout_item_bg_orange
                    mBinding.flActivityLabel.addView(view)

                    tvPeriodText.append("招募人数：${item.recruitedCount}/${item.totalStock}")
                }
                if (!item.signStartTime.isNullOrEmpty() && !item.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        item.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = item.signEndTime.split(" ")[0].replace("-", ".")
                    tvPeriodText.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
                    tvPeriodText.append("$startTime-$endTime")
                }
                time = tvPeriodText.toString()
            }

        }
        when (item.activityStatus) {
            "0" -> {
                activityRunName = "未开始"
            }
            "1" -> {
                activityRunName = "进行中"
            }
            "2" -> {
                activityRunName = "已结束"
            }
        }
        val view = LayoutInflater.from(mBinding.root.context).inflate(
            R.layout.home_popular_activity_flowlayout_item_style_two,
            mBinding.flActivityLabel,
            false
        )
        view.findViewById<TextView>(R.id.label).run {
            this.text = activityRunName
            this.setTextColor(context.resources.getColor(R.color.color_ff9e05))
            this.backgroundResource = R.drawable.shape_home_popular_activity_flowlayout_item_style_two_bg_orange
        }
        mBinding.flActivityLabel.addView(view)


        // 时间和地址
        val timeAndAddress = StringBuilder()
        time.takeIf { it.isNotBlank() }?.apply { timeAndAddress.append(time) }

        // 活动地址
        item.cityRegionNames?.takeIf { it.isNotBlank() }?.apply {
            timeAndAddress.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
            timeAndAddress.append(this.split(",").joinToString(separator = "·") { it })
        }
        item.address?.takeIf { it.isNotBlank() }?.apply { timeAndAddress.append(this) }
        item.resourceNameStr?.takeIf { it.isNotBlank() }?.apply {
            timeAndAddress.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
            timeAndAddress.append(this.split(",")[0])
        }
        item.resourceCount?.takeIf { it.toInt() > 1 }
            ?.apply { timeAndAddress.append("等${this}个场所") }

        mBinding.timeAndAddress.run {
            if (timeAndAddress.isEmpty()) {
                this.visibility = View.GONE
            } else {
                this.visibility = View.VISIBLE
                this.text = timeAndAddress.toString()
            }
        }

        // 诚信状态
        val tags = mutableListOf<String>()
        item.faithAuditStatus?.takeIf { it=="1" }?.apply { tags.add("诚信免审") }
        item.faithUseStatus?.takeIf { it=="1" }?.apply { tags.add("诚信优享") }
        tags.forEach{
            val view = LayoutInflater.from(mBinding.root.context).inflate(
                R.layout.home_popular_activity_flowlayout_item_style_two,
                mBinding.flActivityLabel,
                false
            )
            view.findViewById<TextView>(R.id.label).run {
                this.text = it
                this.setTextColor(context.resources.getColor(R.color.green_36cd64))
                this.backgroundResource = R.drawable.shape_home_popular_activity_flowlayout_item_style_two_bg_green
            }
            mBinding.flActivityLabel.addView(view)
        }

        // 活动标签
        item.tagNames?.takeIf { it.isNotEmpty() }?.let {
            it.split(",").forEachIndexed { index, s ->
                if (index < 3) {
                    val view = LayoutInflater.from(mBinding.root.context).inflate(
                        R.layout.home_popular_activity_flowlayout_item_style_two,
                        mBinding.flActivityLabel,
                        false
                    )
                    view.findViewById<TextView>(R.id.label).run {
                        this.text = s
                        this.setTextColor(context.resources.getColor(R.color.blue_1995ff))
                        this.backgroundResource = R.drawable.shape_home_popular_activity_flowlayout_item_style_two_bg_blue
                    }
                    mBinding.flActivityLabel.addView(view)
                }

            }
        }

        mBinding.ivCollect.onNoDoubleClick {
            onItemCollectionLisener?.let { it(item, position) }
        }
        mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
    }

}