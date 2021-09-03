package com.daqsoft.thetravelcloudwithculture.xj.adpter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.databinding.MainItemHotActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeActivityXjBinding
import com.jakewharton.rxbinding2.view.RxView
import okhttp3.internal.Util
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description 品牌展播的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class XJActivityGalleryAdapter : PagerAdapter() {

    val menus = mutableListOf<ActivityBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemHomeActivityXjBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_home_activity_xj,
            null,
            false
        )

        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    // 如果有跳转链接 直接跳转webactivity
                    if (menus != null && !menus.isNullOrEmpty()) {
                        var item = menus.get(position)
                        if (item.jumpType.equals("2")) {
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

        showActivitySelect(homeBranchBean, mBinding, position)
        container.addView(mBinding.root)
        return mBinding.root
    }


    /**
     * 显示选中的活动信息
     */
    private fun showActivitySelect(
        activityBean: ActivityBean,
        mBinding: ItemHomeActivityXjBinding,
        position: Int
    ) {
        mBinding.tvActivityName.text = activityBean.name
        mBinding.ivCollect.isSelected = activityBean.userResourceStatus.collectionStatus

        if (!activityBean.tagNames.isNullOrEmpty()) {
//            mBinding.tvActivityTag.text = activityBean.tagNames.split(",")[0]
//            mBinding.tvActivityTag.visibility = View.VISIBLE
            val split = activityBean.tagNames.split(",")
            mBinding.lvUse.visibility = View.VISIBLE
            mBinding.lvUse.setLabels( split)
        } else {
//            mBinding.tvActivityTag.visibility = View.GONE
            mBinding.lvUse.visibility = View.GONE
        }





        if (!activityBean.useStartTime.isNullOrEmpty() && !activityBean.useEndTime.isNullOrEmpty()) {
            val startTime: String = activityBean.useStartTime.split(" ")[0].replace("-", ".")
            val endTime: String = activityBean.useEndTime.split(" ")[0].replace("-", ".")
            mBinding.tvActivityTime.text = "${startTime.substring(5,startTime.length)}-${endTime.substring(5,startTime.length)}"
        }
        when (activityBean.type) {
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                if (!activityBean.signStartTime.isNullOrEmpty() && !activityBean.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        activityBean.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = activityBean.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.tvActivityTime.text = "$startTime-$endTime"
                }
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                if (!activityBean.signStartTime.isNullOrEmpty() && !activityBean.signEndTime.isNullOrEmpty()) {
                    val startTime: String =
                        activityBean.signStartTime.split(" ")[0].replace("-", ".")
                    val endTime: String = activityBean.signEndTime.split(" ")[0].replace("-", ".")
                    mBinding.tvActivityTime.text = "$startTime-$endTime"
                }
            }

        }
        if (!activityBean.address.isNullOrEmpty()) {
            mBinding.tvActivityEnjoy.text = activityBean.address
            mBinding.tvActivityEnjoy.visibility = View.VISIBLE
        } else {
            mBinding.tvActivityEnjoy.visibility = View.GONE
        }


        mBinding.ivCollect.isSelected = activityBean.userResourceStatus.collectionStatus

        mBinding.ivCollect.onNoDoubleClick {
            onItemCollectionLisener?.let { it(activityBean, position) }
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
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