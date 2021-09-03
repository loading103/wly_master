package com.daqsoft.thetravelcloudwithculture.home.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeBranchBinding
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeActivityBinding
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description 品牌展播的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class XActivityGalleryAdapter : PagerAdapter() {

    val menus = mutableListOf<ActivityBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemHomeActivityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_activity,
            null,
            false
        )
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
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
            }
        val homeBranchBean = menus[position]
        mBinding.url = homeBranchBean.images
        try {
            Glide.with(mBinding.root.context).load(homeBranchBean.images)
                .fitCenter()
                .into(mBinding.image)
        } catch (e: Exception) {
        }

        showActivitySelect(homeBranchBean, mBinding)
        container.addView(mBinding.root)
        return mBinding.root
    }


    /**
     * 显示选中的活动信息
     */
    private fun showActivitySelect(activityBean: ActivityBean, mBinding: ItemHomeActivityBinding) {
        mBinding.tvActivityName.text = activityBean.name
        if (!activityBean.tagNames.isNullOrEmpty()) {
            mBinding.tvActivityTag.text = activityBean.tagNames.split(",")[0]
            mBinding.tvActivityTag.visibility = View.VISIBLE
        } else {
            mBinding.tvActivityTag.visibility = View.GONE
        }
        var activityRunName = ""
        if (!activityBean.useStartTime.isNullOrEmpty() && !activityBean.useEndTime.isNullOrEmpty()) {
            val startTime: String = activityBean.useStartTime.split(" ")[0].replace("-", ".")
            val endTime: String = activityBean.useEndTime.split(" ")[0].replace("-", ".")
            mBinding.tvActivityTime.text = "$startTime-$endTime"
        }
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
                    mBinding.tvActivityTime.text = "$startTime-$endTime"
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
                if (!activityBean.stock.isNullOrEmpty() && activityBean.stock.toInt() > 0) {
                    activityRunName += "·还剩" + activityBean.stock + "个名额"
                }
                if (!activityBean.signStartTime.isNullOrEmpty() && !activityBean.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        activityBean.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = activityBean.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.tvActivityTime.text = "$startTime-$endTime"
                }
            }

        }
        if (!activityBean.recruitedCount.isNullOrEmpty() && activityBean.recruitedCount.toInt() > 0) {
            mBinding.tvActivityEnjoy.text = "|" + activityBean.recruitedCount + "人参加"
            mBinding.tvActivityEnjoy.visibility = View.VISIBLE
            mBinding.tvActivityLine.visibility = View.VISIBLE
        } else {
            mBinding.tvActivityEnjoy.visibility = View.GONE
            mBinding.tvActivityLine.visibility = View.GONE
        }
        when (activityBean.activityStatus) {
            "0" -> {
                mBinding.tvActivityStatus.text = "未开始"
            }
            "1" -> {
                mBinding.tvActivityStatus.text = "进行中"
            }
            "2" -> {
                mBinding.tvActivityStatus.text = "已结束"
            }
            "3" -> {
                mBinding.tvActivityStatus.text = activityRunName
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }

}