package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.LayoutProviderActivityV2Binding
import com.daqsoft.provider.databinding.LayoutProviderActivityV3Binding
import com.daqsoft.provider.utils.DividerTextUtils
import com.google.zxing.client.result.VINParsedResult
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ProviderAcitivtyPageV1Adapter
 * @Author      luoyi
 * @Time        2020/6/9 14:12
 */
class ProviderAcitivtyPageV1Adapter : PagerAdapter {
    val menus = mutableListOf<ActivityBean>()
    var mContext: Context? = null

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
        val mBinding: LayoutProviderActivityV3Binding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.layout_provider_activity_v3,
            null,
            false
        )

        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    // ????????????????????? ????????????webactivity
                    if (menus != null && menus.isNotEmpty()) {
                        var item = menus.get(position)
                        if (item.jumpType.equals("2")) {
                            ARouter.getInstance()
                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("mTitle", item.jumpName)
                                .withString("html", item.jumpUrl)
                                .navigation()
                        } else {
                            when (item.type) {
                                // ????????????
                                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                        .withString("id", item.id)
                                        .withString("classifyId", item.classifyId)
                                        .navigation()
                                }
                                // ????????????
                                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                                    // ??????
                                    when (item.method) {
                                        // ??????????????????
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

        showActivitySelect(homeBranchBean, mBinding, position)
        container.addView(mBinding.root)
        return mBinding.root
    }


    /**
     * ???????????????????????????
     */
    private fun showActivitySelect(
        item: ActivityBean,
        mBinding: LayoutProviderActivityV3Binding,
        position: Int
    ) {
        var imageUrl = ""
        if (!item.images.isNullOrEmpty()) {
            val images = item.images.split(",")
            if (!images.isNullOrEmpty()) {
                imageUrl = images[0]
            }
        }
        Glide.with(mContext!!)
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgActivity)
        mBinding.tvActivityName.text = "" + item.name
        // ????????????
        if (item.money.toDouble() == 0.0 && item.integral == "0") {
            mBinding.tvActivityPrice.text = mContext!!.getString(R.string.provider_order_free)
        } else if (item.money.toDouble() > 0.0) {
            var dMoney = item.money.toDouble()
            if (dMoney % 1 == 0.0) {
                mBinding.tvActivityPrice.text =
                    mContext!!.resources.getString(R.string.yuan) + dMoney.toInt()
            } else {
                mBinding.tvActivityPrice.text = item.money
            }
        } else {
            mBinding.tvActivityPrice.text =
                mContext!!.resources.getString(R.string.yuan) + item.integral + "??????"
        }
        if (item.type == ActivityType.ACTIVITY_TYPE_PLAIN) {
            mBinding.tvActivityPrice.visibility = View.GONE
        } else {
            mBinding.tvActivityPrice.visibility = View.VISIBLE
        }
        // ???????????? & ???????????????
        var timeStr: String = ""
        if (!item.useStartTime.isNullOrEmpty() && !item.useEndTime.isNullOrEmpty()) {
            var startTime: String? =
                DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd HH:mm", item.useStartTime)
            var endTime: String? =
                DateUtil.formatDateByString("MM.dd", "yyyy-MM-dd HH:mm", item.useEndTime)
            timeStr = "$startTime-$endTime"
        }
        var inputPerson: String = ""
        var status = getActivityStauts(item.activityStatus, item.type)
        if (!item.type.isNullOrEmpty() && item.type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
            // ?????????
            if (item.alreadySignCount != null && item.alreadySignCount!! > 0) {
                inputPerson = "" + item.alreadySignCount + "??????"
            }
            if (!item.totalStock.isNullOrEmpty() && item.alreadySignCount != null) {
                status += "?????????${item.totalStock.toInt() - item.alreadySignCount!!}?????????"
            }
        }
        mBinding.tvActivityTime.text =
            DividerTextUtils.convertDotString(StringBuilder(), timeStr, inputPerson)
        // ???????????? & ??????
        var lbes: MutableList<String> = mutableListOf()

//        if (!status.isNullOrEmpty()) {
//            lbes.add(status)
//        }
        if (!item.tagNames.isNullOrEmpty()) {
            var tags = item.tagNames.split(",")
            if (!tags.isNullOrEmpty()) {
                for (lb in tags) {
                    if (!lb.isNullOrEmpty()) {
                        lbes.add(lb)
                    }
                }
            }
        }
        if (!lbes.isNullOrEmpty()) {
            mBinding.llvHotelSpecialService.setLabels(lbes)
            mBinding.llvHotelSpecialService.visibility = View.VISIBLE
        } else {
            mBinding.llvHotelSpecialService.visibility = View.GONE
        }
        mBinding.root.onNoDoubleClick {
            if (!item.jumpUrl.isNullOrEmpty()) {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", item.jumpName)
                    .withString("html", item.jumpUrl)
                    .navigation()
            } else {
                when (item.type) {
                    // ????????????
                    ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                            .withString("id", item.id)
                            .withString("classifyId", item.classifyId)
                            .navigation()
                    }
                    // ????????????
                    ActivityType.ACTIVITY_TYPE_RESERVE -> {
                        // ??????
                        when (item.method) {
                            // ??????????????????
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


    /**
     * @param activityStatus ????????????
     * @param type ????????????
     */

    private fun getActivityStauts(activityStatus: String, type: String): String {
        // ???????????? 0(?????????) 1(?????????) 2(?????????) 3 (?????????/?????????)
        return when (activityStatus) {
            "0" -> {
                "?????????"
            }
            "1" -> {
                "?????????"
            }
            "2" -> {
                "?????????"
            }
            "3" -> {
                if (type == ActivityType.ACTIVITY_TYPE_VOLUNT) {
                    "?????????"
                } else if (type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                    "?????????"
                } else {
                    "?????????"
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
}