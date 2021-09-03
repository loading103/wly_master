package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityMap1Binding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.utils.TimeUtils
import org.jetbrains.anko.textColor
import java.text.DecimalFormat

/**
 * @Author：      邓益千
 * @Create by：   2020/6/29 10:09
 * @Description： 地图模式，活动导航adapter
 */
class ActivityNaviAdapter : RecyclerViewAdapter<MainItemHotActivityMap1Binding, ActivityBean> {

    constructor(): super(R.layout.main_item_hot_activity_map_1){
        emptyViewShow = false
    }

    private var startLatLng: LatLng? = null

    fun setStartLatLng(startLatLng: LatLng){
        this.startLatLng = startLatLng
    }

    override fun setVariable(mBinding: MainItemHotActivityMap1Binding, position: Int, bean: ActivityBean) {
        val images = bean.images.split(",")
        if (images.isNotEmpty()) {
            mBinding.url = images[0]
        }
        mBinding.name = bean.name
        // 显示价格/积分/免费
        if (bean.money.toDouble() == 0.0 && bean.integral == "0") {
            mBinding.price = mBinding.root.context.getString(R.string.order_free)
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.INVISIBLE
        } else if (bean.money.toDouble() > 0.0) {
            mBinding.price = bean.money.toString()
            mBinding.tvMoneyUnit.visibility = View.VISIBLE
            mBinding.tvIntegral.visibility = View.INVISIBLE
        } else {
            mBinding.price = bean.integral
            mBinding.tvMoneyUnit.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.VISIBLE
        }
        val tags = mutableListOf<String>()
        if (bean.faithAuditStatus == "1") {
            tags.add("诚信免审")
        }

        if (bean.faithUseStatus == "1") {
            tags.add("诚信优享")
        }

        if (bean.tagNames.isNotEmpty()) {
            val tag = bean.tagNames.split(",")
            for (el in tag) {
                tags.add(el)
            }
        }

        mBinding.ivCollect.isSelected = bean.userResourceStatus.collectionStatus

        when (bean.type) {
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                mBinding.tvPrice.visibility = View.GONE
                mBinding.tvIntegral.visibility = View.GONE
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                mBinding.tvPrice.visibility = View.INVISIBLE
                mBinding.tvIntegral.visibility = View.GONE
                var total = mBinding.root.context.getString(
                    R.string.home_activity_total_number,
                    bean.recruitedCount,
                    bean.totalStock
                )
                mBinding.totalNumber = total
                var left = mBinding.root.context.getString(R.string.home_activity_rest_number, bean.stock)
                mBinding.left = left
                tags.add(0, left)
            }
            else -> {
                mBinding.tvPrice.visibility = View.VISIBLE
            }
        }
        dealTags(mBinding.root.context,mBinding,tags)
        bindDisatance(mBinding.root.context,mBinding,bean)
        mBinding.tvCheck.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_HOT_ACITITY)
                .withString("id", bean.id)
                .withString("classifyId", bean.classifyId)
                .navigation()
        }
        mBinding.tvNavigation.onNoDoubleClick {
            if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty()){
                val startPoi = Poi("我的位置", startLatLng, "")
                val endPoi = Poi(bean.classifyName, LatLng(bean.latitude.toDouble(), bean.longitude.toDouble()), "")

                AmapNaviPage.getInstance().showRouteActivity(
                    mBinding.root.context,
                    AmapNaviParams(startPoi, null, endPoi, AmapNaviType.DRIVER, AmapPageType.NAVI),
                    null
                )
            }
        }
    }

    /**
     * 处理标签
     */
    private fun dealTags(mContext: Context, mBinding: MainItemHotActivityMap1Binding,tags: MutableList<String>) {
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
                            mContext!!.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
                        mBinding.tvVolunteer.textColor = mContext!!.resources.getColor(R.color.white)

                        if (item == "诚信优享") {
                            var d = mContext!!.resources.getDrawable(R.mipmap.provider_activity_enjoy)
                            mBinding.tvVolunteer.setCompoundDrawablesWithIntrinsicBounds(
                                d,
                                null,
                                null,
                                null
                            )
                        } else {
                            var d = mContext!!.resources.getDrawable(R.mipmap.provider_activity_exempt)
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
                        mBinding.tvVolunteer.textColor = mContext!!.resources.getColor(R.color.white)
                    } else {
                        // tag
                        mBinding.name = item
                        mBinding.tvVolunteer.background =
                            mContext!!.getDrawable(R.drawable.home_b_white_stroke_36cd64_round_2)
                        mBinding.tvVolunteer.textColor =
                            mContext!!.resources.getColor(R.color.colorPrimary)
                    }
                }
            }
        tagAdapter.add(tags)
        tagAdapter.emptyViewShow = false
//        mBinding!!.rvTags.layoutManager = tagLayoutManager
//        mBinding!!.rvTags.adapter = tagAdapter

        mBinding!!.rvTags.setLabels(tags)
    }

    /**
     * 绑定 距离相关
     * @param bean
     */
    private fun bindDisatance(mContext: Context, mBinding: MainItemHotActivityMap1Binding, bean: ActivityBean) {
        var startTime = TimeUtils.timeString2MD(bean.useStartTime).month.toString() +
                "." + TimeUtils.timeString2MD(bean.useStartTime).day.toString()
        var endTime =
            TimeUtils.timeString2MD(bean.useStartTime).month.toString() + "." + TimeUtils.timeString2MD(
                bean
                    .useEndTime
            ).day.toString()

        mBinding.time = mContext!!.getString(
            R.string.order_activity_room_time_stamp_,
            bean.useStartTime.substring(0, bean.useStartTime.length-3),
            bean.useEndTime.substring(0, bean.useStartTime.length-3)
        )
        var end: LatLng? = null
        var address = ""
        if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty()) {
            // 换算距离
            end = LatLng(bean.latitude.toDouble(), bean.longitude.toDouble())
            var dis = AMapUtils.calculateLineDistance(startLatLng, end)
            var disDistance = if (dis > 1000) {
                val df = DecimalFormat("0.00")
                df.format(dis / 1000) + "KM，"
            } else {
                dis.toInt().toString() + "M，"
            }
            address = disDistance + bean.address
        } else {
            address = bean.address
        }
        if (address.isNullOrEmpty()) {
            mBinding!!.tvAddress.visibility = View.GONE
        } else {
            mBinding!!.address = address
            mBinding!!.tvAddress.visibility = View.VISIBLE
        }
    }

}