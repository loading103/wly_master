package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.LayoutProviderActivityV3Binding
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   HotActivityPagerAdapter
 * @Author      luoyi
 * @Time        2020/6/9 16:05
 */
class HotActivityPagerAdapter : PagerAdapter {
    val menus = mutableListOf<ActivityBean>()
    var mContext: Context? = null
    var onHotActPagerClickListener: OnHotActPagerClickListener? = null

    constructor(context: Context) {
        mContext = context
    }

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: MainItemHotActivityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.main_item_hot_activity,
            null,
            false
        )
        val homeBranchBean = menus[position]

        showActivitySelect(homeBranchBean, mBinding, position)
        container.addView(mBinding.root)
        return mBinding.root
    }


    /**
     * 显示选中的活动信息
     */
    private fun showActivitySelect(
        item: ActivityBean,
        mBinding: MainItemHotActivityBinding,
        position: Int
    ) {
        val images = item.images.split(",")
        var url = ""
        if (images.isNotEmpty()) {
            url = images[0]
        }
        mBinding.imageUrl = url

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
            mBinding.ivCollect.visibility = View.VISIBLE
            mBinding.tvIsOver.visibility = View.GONE
            mBinding.tvPrice.visibility = View.VISIBLE
            // 显示价格/积分/免费
            if (item.money.toDouble() == 0.0 && item.integral == "0") {
                mBinding.price = mContext!!.getString(R.string.order_free)
                mBinding.tvMoneyUnit.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE
            } else if (item.money.toDouble() > 0.0) {
                var dMoney = item.money.toDouble()
                if (dMoney % 1 == 0.0) {
                    mBinding.price = "" + dMoney.toInt()
                } else {
                    mBinding.price = item.money
                }
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
        if (item.userResourceStatus != null) {
            mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus
        }
        var time =
            mContext!!.getString(R.string.order_activity_room_time_stamp_, startTime, endTime)
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
                    mContext!!.getString(
                        R.string.home_activity_total_number,
                        item.recruitedCount,
                        item.totalStock
                    )
                mBinding.totalTime = DividerTextUtils.convertString(StringBuilder(), total, time)
                var left =
                    mContext!!.getString(R.string.home_activity_rest_number, item.stock.toString())
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
            val tagLayoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            /**
             * 标签适配器包括 剩余名额，是否开启诚信面审 诚信优享，tags
             */
            val tagAdapter =
                object :
                    RecyclerViewAdapter<MainItemHotActivityTagBinding, String>(R.layout.main_item_hot_activity_tag) {
                    @SuppressLint("CheckResult")
                    override fun setVariable(
                        mBinding: MainItemHotActivityTagBinding,
                        position: Int,
                        item: String
                    ) {

                        if (item == "诚信免审" || item == "诚信优享") {
                            //诚信
                            mBinding.tvVolunteer.background =
                                mContext!!.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                            mBinding.tvVolunteer.textColor =
                                mContext!!.resources.getColor(R.color.white)

                            if (item == "诚信优享") {
                                var d = mContext!!.resources.getDrawable(R.mipmap.activity_enjoy)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(
                                    d,
                                    null,
                                    null,
                                    null
                                )
                            } else {
                                var d = mContext!!.resources.getDrawable(R.mipmap.activity_exempt)
                                mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(
                                    d,
                                    null,
                                    null,
                                    null
                                )
                            }
                        } else if (item.startsWith("还剩")) {
                            // 剩余
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext!!.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
                            mBinding.tvVolunteer.textColor =
                                mContext!!.resources.getColor(R.color.white)
                        } else {
                            // tag
                            mBinding.name = item
                            mBinding.tvVolunteer.background =
                                mContext!!.getDrawable(R.drawable.shape_label_primary_color_bg_2)
                            mBinding.tvVolunteer.textColor =
                                mContext!!.resources.getColor(R.color.colorPrimary)
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

        var address: String? = DividerTextUtils.convertDotString(
            StringBuilder(),
            if (!item.cityRegionNames.isNullOrEmpty()) {
                item.cityRegionNames
            } else {
                ""
            },
            if (!item.address.isNullOrEmpty()) {
                item.address
            } else {
                ""
            }
            , if (!item.resourceNameStr.isNullOrEmpty()) {
                item.resourceNameStr
            } else {
                ""
            }
        )

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
                } else if (type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                    "预定中"
                } else {
                    "报名中"
                }
            }
            else -> {
                ""
            }
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

    interface OnHotActPagerClickListener {
        fun onCollect(activityId: String, position: Int)
        fun onCanceCollect(activityId: String, position: Int)
    }
}