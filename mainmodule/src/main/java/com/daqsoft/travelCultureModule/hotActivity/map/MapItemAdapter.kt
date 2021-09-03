package com.daqsoft.travelCultureModule.hotActivity.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityMapBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.utils.TimeUtils
import com.daqsoft.provider.bean.ActivityBean
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit
import java.text.DecimalFormat


/**
 * @Description 地图模式里面的活动item的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class MapItemAdapter(context: Context) : PagerAdapter() {
    val mContext = context
    /**
     * 自己的位置
     */
    var latLng: LatLng? = null


    val menus = mutableListOf<ActivityBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: MainItemHotActivityMapBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.main_item_hot_activity_map,
            null,
            false
        )
        val item = menus[position]
        val images = item.images.split(",")
        if (images.isNotEmpty()) {
            mBinding.url = images[0]
        }
        mBinding.name = item.name
        // 显示价格/积分/免费
        if (item.money.toDouble() == 0.0 && item.integral == "0") {
            mBinding.price = mContext.getString(R.string.order_free)
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.INVISIBLE
        } else if (item.money.toDouble() > 0.0) {
            mBinding.price = item.money.toString()
            mBinding.tvMoneyUnit.visibility = View.VISIBLE
            mBinding.tvIntegral.visibility = View.INVISIBLE
        } else {
            mBinding.price = item.integral
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.VISIBLE
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

        mBinding.ivCollect.isSelected = item.userResourceStatus.collectionStatus

        when (item.type) {
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                mBinding.tvPrice.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                mBinding.tvPrice.visibility = View.INVISIBLE
                mBinding.tvIntegral.visibility = View.GONE
                var total =
                    mContext.getString(
                        R.string.home_activity_total_number,
                        item.recruitedCount,
                        item.totalStock
                    )
                mBinding.totalNumber = total
                var left =
                    mContext.getString(R.string.home_activity_rest_number, item.stock.toString())
                mBinding.left = left
//                mBinding.tvLeft.visibility = View.VISIBLE
                tags.add(0, left)
            }
            else -> {
                mBinding.tvPrice.visibility = View.VISIBLE
            }
        }
        val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
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
                            mContext.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                        mBinding.tvVolunteer.textColor = mContext.resources.getColor(R.color.white)

                        if (item == "诚信优享") {
                            var d = mContext.resources.getDrawable(R.mipmap.provider_activity_enjoy)
                            mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(
                                d,
                                null,
                                null,
                                null
                            )
                        } else {
                            var d = mContext.resources.getDrawable(R.mipmap.provider_activity_exempt)
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
                            mContext.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
                        mBinding.tvVolunteer.textColor = mContext.resources.getColor(R.color.white)
                    } else {
                        // tag
                        mBinding.name = item
                        mBinding.tvVolunteer.background =
                            mContext.getDrawable(R.drawable.home_b_white_stroke_36cd64_round_2)
                        mBinding.tvVolunteer.textColor =
                            mContext.resources.getColor(R.color.colorPrimary)
                    }
                }
            }
        tagAdapter.add(tags)
        mBinding.rvTags.layoutManager = tagLayoutManager
        mBinding.rvTags.adapter = tagAdapter

        var startTime = TimeUtils.timeString2MD(item.useStartTime).month.toString() +
                "." + TimeUtils.timeString2MD(item.useStartTime).day.toString()
        var endTime =
            TimeUtils.timeString2MD(item.useStartTime).month.toString() + "." + TimeUtils.timeString2MD(
                item
                    .useEndTime
            ).day.toString()

        mBinding.time =
            mContext.getString(
                R.string.order_activity_room_time_stamp_,
                item.useStartTime,
                item.useStartTime
            )
        var end: LatLng? = null
        if (!item.latitude.isNullOrEmpty() && !item.longitude.isNullOrEmpty()) {
            // 换算距离
            end = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
            var dis = AMapUtils.calculateLineDistance(latLng, end)
            var disDistance = if (dis > 1000) {
                val df = DecimalFormat("0.00")
                df.format(dis / 1000) + "KM，"
            } else {
                dis.toInt().toString() + "M，"
            }
            mBinding.address = disDistance + item.address
        } else {
            mBinding.address = item.address
        }



        if (item.address.isBlank()) {
            mBinding.tvAddress.visibility = View.GONE
        }
        RxView.clicks(mBinding.tvCheck)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
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

        container.addView(mBinding.root)
        return mBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }

}