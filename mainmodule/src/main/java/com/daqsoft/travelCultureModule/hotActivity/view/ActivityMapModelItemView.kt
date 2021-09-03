package com.daqsoft.travelCultureModule.hotActivity.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityMapBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.TimeUtils
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description 活动-地图模式 浏览布局
 * @ClassName   ActivityMapModelItemView
 * @Author      罗毅
 * @Time        2020/3/18 11:08
 */
class ActivityMapModelItemView : RelativeLayout {

    var mBinding: MainItemHotActivityMapBinding? = null

    var mContext: Context? = null

    /**
     * 当前经纬度
     */
    var latLng: LatLng? = null
    /**
     * 当前地址
     */
    var currAdress: String? = ""

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    /**
     * 初始化布局
     */
    private fun initView(context: Context?) {
        mContext = context
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.main_item_hot_activity_map,
            this,
            false
        )
        addView(mBinding!!.root)
    }

    /**
     * 修改UI 当前活动实体
     * @param bean
     */
    fun updateUi(bean: ActivityBean) {
        if (bean == null) {
            return
        }
        mBinding?.let {
            val images = bean.images.split(",")
            if (images.isNotEmpty()) {
                it.url = images[0]
            }
            it.name = bean.name
            // 显示价格/积分/免费
            if (bean.money?.toDouble() == 0.0 && bean.integral == "0") {
                it.price = mContext!!.getString(R.string.order_free)
                it.tvMoneyUnit!!.visibility = View.GONE
                it.tvIntegral!!.visibility = View.INVISIBLE
            } else if (bean.money?.toDouble() > 0.0) {
                it.price = bean.money.toString()
                it.tvMoneyUnit.visibility = View.VISIBLE
                it.tvIntegral.visibility = View.INVISIBLE
            } else {
                it.price = bean.integral
                it.tvMoneyUnit.visibility = View.GONE
                it.tvIntegral.visibility = View.VISIBLE
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

            it.ivCollect.isSelected = bean.userResourceStatus.collectionStatus

            when (bean.type) {
                ActivityType.ACTIVITY_TYPE_PLAIN -> {
                    it.tvPrice.visibility = View.GONE
                    it.tvIntegral.visibility = View.GONE
                }
                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                    it.tvPrice.visibility = View.INVISIBLE
                    it.tvIntegral.visibility = View.GONE
                    var total =
                        mContext!!.getString(
                            R.string.home_activity_total_number,
                            bean.recruitedCount,
                            bean.totalStock
                        )
                    it.totalNumber = total
                    var left =
                        mContext!!.getString(R.string.home_activity_rest_number, bean.stock.toString())
                    it.left = left
//                mBinding.tvLeft.visibility = View.VISIBLE
                    tags.add(0, left)
                }
                else -> {
                    it.tvPrice.visibility = View.VISIBLE
                }
            }
            dealTags(tags)
            bindDisatance(bean)
            RxView.clicks(it.tvCheck)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_HOT_ACITITY)
                            .withString("id", bean.id)
                            .withString("classifyId", bean.classifyId)
                            .navigation()
                    }
                }
            it.tvNavigation.onNoDoubleClick {
                if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty()) {
                    var startLat = 0.0
                    var startLng = 0.0
                    if (latLng != null) {
                        startLat = latLng!!.latitude
                        startLng = latLng!!.longitude
                    }
                    var address = ""
                    if (!currAdress.isNullOrEmpty()) {
                        address = currAdress!!
                    }
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            context, startLat, startLng, address,
                            bean.latitude.toDouble(), bean.longitude.toDouble(),
                            bean.name
                        )
                    } else {
                        ToastUtils.showMessage("非常抱歉，系统未安装高德地图")
                    }
                } else {
                    ToastUtils.showMessage("非常抱歉，暂无位置信息")
                }
            }
        }

    }

    /**
     * 绑定 距离相关
     * @param bean
     */
    private fun bindDisatance(bean: ActivityBean) {
        var startTime = TimeUtils.timeString2MD(bean.useStartTime).month.toString() +
                "." + TimeUtils.timeString2MD(bean.useStartTime).day.toString()
        var endTime =
            TimeUtils.timeString2MD(bean.useStartTime).month.toString() + "." + TimeUtils.timeString2MD(
                bean
                    .useEndTime
            ).day.toString()

        mBinding!!.time =
            mContext!!.getString(
                R.string.order_activity_room_time_stamp_,
                bean.useStartTime,
                bean.useStartTime
            )
        var end: LatLng? = null
        var address = ""
        if (!bean.latitude.isNullOrEmpty() && !bean.longitude.isNullOrEmpty()) {
            // 换算距离
            end = LatLng(bean.latitude.toDouble(), bean.longitude.toDouble())
            var dis = AMapUtils.calculateLineDistance(latLng, end)
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

    /**
     * 处理标签
     */
    private fun dealTags(tags: MutableList<String>) {
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
        mBinding!!.rvTags.layoutManager = tagLayoutManager
        mBinding!!.rvTags.adapter = tagAdapter
    }
}