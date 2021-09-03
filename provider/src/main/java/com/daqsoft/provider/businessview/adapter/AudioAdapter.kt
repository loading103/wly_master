package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.ItemHomePopularActivityOtherScBinding
import com.daqsoft.provider.databinding.ItemHomePopularActivityScBinding
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource
import java.lang.Exception

/**
 * @date 2020/9/3 14:32
 * @author zp
 * @describe 四川 热门活动适配器
 */
class AudioAdapter : PagerAdapter() {


    var isShowCollect:Boolean=true

    val menus = mutableListOf<ActivityBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var mBinding: ViewDataBinding? = null

//        if(menus[position].type == ActivityType.ACTIVITY_TYPE_PLAIN){
//            mBinding = DataBindingUtil.inflate(
//                LayoutInflater.from(container.context),
//                R.layout.item_home_popular_activity_other_sc,
//                null,
//                false
//            ) as ItemHomePopularActivityOtherScBinding
//            showActivitySelect(menus[position], mBinding)
//        }else{
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_popular_activity_sc,
            null,
            false
        ) as ItemHomePopularActivityScBinding
        showActivitySelect(menus[position], mBinding)
//        }


        mBinding.root.onModuleNoDoubleClick(
            BaseApplication.context.resources.getString(R.string.provider_hot_activity),
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            // 如果有跳转链接 直接跳转webactivity
            if (menus != null && !menus.isNullOrEmpty()) {
                var item = menus.get(position)
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

        mBinding.ivCollect.isSelected = menus[position].userResourceStatus.collectionStatus

        mBinding.ivCollect.onNoDoubleClick {
            onItemCollectionLisener?.let { it(menus[position], position) }
        }

        container.addView(mBinding.root)
        return mBinding.root
    }


    /**
     * 显示选中的活动信息   其他活动
     */
    @SuppressLint("SetTextI18n")
    private fun showActivitySelect(
        activityBean: ActivityBean,
        mBinding: ItemHomePopularActivityOtherScBinding
    ) {
        // 活动名称
        mBinding.tvActivityName.text = activityBean.name

        // 活动宣传图
        Glide
            .with(mBinding.root.context)
            .load(activityBean.images.getRealImages())
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
            .into(mBinding.image)

        // 活动时间
        if (!activityBean.useStartTime.isNullOrEmpty() && !activityBean.useEndTime.isNullOrEmpty()) {
            val startTime = DateUtil.formatDateByString(
                "yyyy.MM.dd",
                "yyyy-MM-dd HH:mm:ss",
                activityBean.useStartTime
            )
            val endTime =
                DateUtil.formatDateByString(
                    "yyyy.MM.dd",
                    "yyyy-MM-dd HH:mm:ss",
                    activityBean.useEndTime
                )
            mBinding.tvPeriod.text = "$startTime-$endTime"
        }

        // 活动标签
        activityBean.tagNames?.takeIf { it.isNotEmpty() }?.let {
            it.split(",").forEach {
                val view = LayoutInflater.from(mBinding.root.context).inflate(
                    R.layout.home_popular_activity_other_flowlayout_item,
                    mBinding.flActivityLabel,
                    false
                )
                view.findViewById<TextView>(R.id.label).text = it
                mBinding.flActivityLabel.addView(view)
            }
        }
        when (activityBean.activityStatus) {
            "0" -> "未开始"
            "1" -> "进行中"
            "2" -> "已结束"
            else -> ""
        }.takeIf { it.isNotBlank() }?.apply {
            val view = LayoutInflater.from(mBinding.root.context).inflate(
                R.layout.home_popular_activity_other_flowlayout_item,
                mBinding.flActivityLabel,
                false
            )
            view.findViewById<TextView>(R.id.label).text = this
            mBinding.flActivityLabel.addView(view)
        }
    }

    /**
     * 显示选中的活动信息
     */
    @SuppressLint("SetTextI18n")
    private fun showActivitySelect(
        activityBean: ActivityBean,
        mBinding: ItemHomePopularActivityScBinding
    ) {

        // 活动价格
        if (activityBean.type == ActivityType.ACTIVITY_TYPE_PLAIN) {
            mBinding.price.visibility = View.GONE
            mBinding.unit.visibility = View.GONE
        } else {
            mBinding.price.visibility = View.VISIBLE
            mBinding.unit.visibility = View.VISIBLE
            if (activityBean.money.toDouble() == 0.0 && activityBean.integral == "0") {
                mBinding.unit.visibility = View.GONE
                mBinding.price.text = mBinding.price.context.getString(R.string.provider_order_free)
            } else if (activityBean.money.toDouble() > 0.0) {
                var dMoney = activityBean.money.toDouble()
                if (dMoney % 1 == 0.0) {
                    mBinding.price.text = dMoney.toInt().toString()
                } else {
                    mBinding.price.text = activityBean.money
                }
                mBinding.unit.text =
                    mBinding.unit.context.resources.getString(R.string.provider_chinese_yuan)
            } else {
                mBinding.price.text = activityBean.integral
                mBinding.unit.text =
                    mBinding.unit.context.resources.getString(R.string.provider_integral)
            }
        }

        // 活动特色
        mBinding.tvActivityFeatures.run {
            if (activityBean.remark.isNullOrEmpty()) {
                this.visibility = View.GONE
            } else {
                this.text = activityBean.remark
            }
        }
        // 活动名称
        mBinding.tvActivityName.text = activityBean.name

        // 活动宣传图
        GlideModuleUtil.loadDqImageWaterMark(
            activityBean.images.getRealImages(),
            mBinding.root.context,
            mBinding.image,
            R.mipmap.placeholder_img_fail_240_180,
            R.mipmap.placeholder_img_fail_240_180,
            RequestOptions().transform(CenterCrop(), RoundedCorners(5))
        )
//        Glide
//            .with(mBinding.root.context)
//            .load(activityBean.images.getRealImages())
//            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
//            .into(mBinding.image)

        // 活动时间
        if (!activityBean.useStartTime.isNullOrEmpty() && !activityBean.useEndTime.isNullOrEmpty()) {
            val startTime = DateUtil.formatDateByString(
                "MM月dd日",
                "yyyy-MM-dd HH:mm:ss",
                activityBean.useStartTime
            )
            val endTime =
                DateUtil.formatDateByString(
                    "MM月dd日",
                    "yyyy-MM-dd HH:mm:ss",
                    activityBean.useEndTime
                )
            mBinding.tvPeriod.text = "$startTime-$endTime"
        }

        // 活动状态
        var activityRunName = ""
        var activeStatusBg = R.drawable.shape_home_popular_activity_tag_bg_green
        when (activityBean.type) {
            // 预订
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                activityRunName = "预订中"
            }
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                activityRunName = "报名中"
                if (!activityBean.signStartTime.isNullOrEmpty() && !activityBean.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        activityBean.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = activityBean.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.tvPeriod.text = "$startTime-$endTime"
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
                if (!activityBean.stock.isNullOrEmpty() && activityBean.stock.toInt() > 0) {
                    //添加剩余人数 label
//                    activityRunName += "·还剩" + activityBean.stock + "个名额"
                    val view = LayoutInflater.from(mBinding.root.context).inflate(
                        R.layout.home_popular_activity_flowlayout_item,
                        mBinding.flActivityLabel,
                        false
                    )
                    val label = view.findViewById<TextView>(R.id.label)
                    label.text = "还剩${activityBean.stock}个名额"
                    label.textColorResource = R.color.ff9e05
                    label.backgroundResource =
                        R.drawable.shape_home_popular_activity_flowlayout_item_bg_orange
                    mBinding.flActivityLabel.addView(view)

                    tvPeriodText.append("招募人数：${activityBean.recruitedCount}/${activityBean.totalStock}")
                }
                if (!activityBean.signStartTime.isNullOrEmpty() && !activityBean.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        activityBean.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = activityBean.signEndTime.split(" ")[0].replace("-", ".")
                    tvPeriodText.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
                    tvPeriodText.append("$startTime-$endTime")
                }
                mBinding.tvPeriod.text = tvPeriodText.toString()
            }

        }
        when (activityBean.activityStatus) {
            "0" -> {
                activityRunName = "未开始"
                activeStatusBg = R.drawable.shape_home_popular_activity_tag_bg_black
            }
            "1" -> {
                activityRunName = "进行中"
            }
            "2" -> {
                activityRunName = "已结束"
                activeStatusBg = R.drawable.shape_home_popular_activity_tag_bg_black
            }
        }
        mBinding.tvActiveStatus.text = activityRunName
        mBinding.tvActiveStatus.setBackgroundResource(activeStatusBg)

        // 活动地址
        val location = StringBuilder()
        activityBean.cityRegionNames?.takeIf { it.isNotBlank() }?.apply {
            location.append(this.split(",").joinToString(separator = "·") { it })
        }
        activityBean.address?.takeIf { it.isNotBlank() }?.apply { location.append(this) }
        activityBean.resourceNameStr?.takeIf { it.isNotBlank() }?.apply {
            location.takeIf { it.isNotEmpty() }?.apply { this.append(" | ") }
            location.append(this.split(",")[0])
        }
        activityBean.resourceCount?.takeIf { it.toInt() > 1 }
            ?.apply { location.append("等${this}个场所") }
        mBinding.tvLocation.run {
            if (location.isNullOrEmpty()) {
                mBinding.vLocation.visibility = View.GONE
                this.visibility = View.GONE
            } else {
                this.visibility = View.VISIBLE
                mBinding.vLocation.visibility = View.VISIBLE
                this.text = location.toString()
            }
        }

        // 活动标签
        activityBean.tagNames?.takeIf { it.isNotEmpty() }?.let {
            it.split(",").forEachIndexed { index, s ->
                if (index < 3) {
                    val view = LayoutInflater.from(mBinding.root.context).inflate(
                        R.layout.home_popular_activity_flowlayout_item,
                        mBinding.flActivityLabel,
                        false
                    )
                    view.findViewById<TextView>(R.id.label).text = s
                    mBinding.flActivityLabel.addView(view)
                }

            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }


    var onItemCollectionLisener: ((item: ActivityBean, position: Int) -> Unit)? = null

    fun setOnCollectionLisener(onItemCollectionLisener: (item: ActivityBean, position: Int) -> Unit) {
        this.onItemCollectionLisener = onItemCollectionLisener
    }


    fun notifyUpdateCollectStatus(position: Int, status: Boolean) {
        try {
            val item = menus[position]
            if (item.userResourceStatus != null) {
                menus[position]!!.userResourceStatus!!.collectionStatus = status
            }
        } catch (e: Exception) {

        }
    }
}