package com.daqsoft.usermodule.ui.order.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.OrderListDataBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemOrdersLsBinding
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.append
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Description 我的预约列表适配器
 * @ClassName   MineAppointmentAdapter
 * @Author      luoyi
 * @Time        2020/5/15 17:33
 */
class MineAppointmentAdapter : RecyclerViewAdapter<ItemOrdersLsBinding, OrderListDataBean> {

    var mContext: Context? = null

    var onAppointmentItemListener: OnAppointmentItemListener? = null

    constructor(context: Context) : super(R.layout.item_orders_ls) {
        this.mContext = context
    }

    override fun setVariable(
        mBinding: ItemOrdersLsBinding,
        position: Int,
        item: OrderListDataBean
    ) {
        mBinding.tvCancel.visibility = View.GONE
        mBinding.tvVenueCommtatorTip.visibility = View.GONE
        mBinding.mReviewsTv.visibility = View.GONE
        mBinding.tvGoToComment.visibility = View.GONE
        mBinding.item = item
        item?.let {
            mBinding.status = when (item.status) {
                // 判断当前订单的状态
                4 -> {
                    // 待审核
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.colorPrimary)
                    mContext!!.resources.getString(R.string.order_waite_valid)
                }
                79 -> {
                    // 未通过
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.red)
                    mBinding.tvCancel.visibility = View.GONE
                    mContext!!.resources.getString(R.string.order_no_pass)
                }
                11 -> {
                    // 待消费
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.colorPrimary)
                    if (item!!.orderType == ResourceType.CONTENT_TYPE_VENUE || item!!.orderType == ResourceType.CONTENT_TYPE_SCENERY) {
                        // 景区和场馆，默认部分可退 需要展示待使用数量
                        mContext!!.resources.getString(R.string.order_waite_use) + "(${item.surplusNum})"
                    } else {
                        mContext!!.resources.getString(R.string.order_waite_use)
                    }
                }
                12 -> {
                    // 已完成
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.colorPrimary)
                    mBinding.mReviewsTv.visibility = View.GONE
                    if (item.commentStatus == 0) {
                        mBinding.tvGoToComment.visibility = View.VISIBLE
                    } else {
                        mBinding.tvGoToComment.visibility = View.GONE
                    }
                    mBinding.tvGoToComment.onNoDoubleClick {
                        if (item != null) {
                            var resourceId: String? = ""
                            var resourceType: String? = ""
                            var resourceName: String? = ""
                            if (item!!.orderType != null) {
                                if (item!!.orderType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
                                    // 活动室
                                    if (item.activityRoom != null) {
                                        resourceId = item.activityRoom.resourceId
                                        resourceType = item.activityRoom.resourceType
                                        resourceName = item.activityRoom.venueName
                                    }
                                } else if (item!!.orderType == ResourceType.CONTENT_TYPE_VENUE || item!!.orderType == ResourceType.CONTENT_TYPE_SCENERY) {
                                    // 场馆和景区
                                    if (item.venueInfo != null) {
                                        resourceId = item.venueInfo.resourceId.toString()
                                        resourceType = item.venueInfo.resourceType
                                        resourceName = item.venueInfo.venueName
                                    }
                                }
                            }
                            //活动暂时未处理
                            if (!resourceId.isNullOrEmpty() && !resourceType.isNullOrEmpty()) {
                                MenuJumpUtils.gotoCommentPage(
                                    resourceType,
                                    resourceId,
                                    resourceName,
                                    item.id.toString()
                                )
                            }

                        }
                    }
                    mContext!!.resources.getString(R.string.order_finish)
                }
                13 -> {
                    // 已失效
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.red)
                    mContext!!.resources.getString(R.string.order_no_effect)
                }
                14 -> {
                    // 已取消
                    mBinding.statusColor = mContext!!.resources.getColor(R.color.txt_gray)
                    mContext!!.resources.getString(R.string.order_canceled)
                }

                else -> mContext!!.resources.getString(R.string.order_waite_valid)
            }
        }
        var image = ""
        var name: SpannableStringBuilder = SpannableStringBuilder("")
        var money = 0.0
        var integral = 0
        var timeBegin =
            DateUtil.formatDateByString("yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm:ss", item.startTime)
        var timeEnd = DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm:ss", item.endTime)
        when (item.orderType) {
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                // 预约的活动室
                mBinding.tvActivityRoomNum.visibility = View.VISIBLE
                mBinding.theirView.visibility = View.VISIBLE
                var activityRoom = item.activityRoom
                activityRoom?.let {
                    mBinding.theirView.text = "所属场馆: ${it.venueName}"
                    mBinding.tvPeople.visibility = View.GONE
                    image = it.image.getRealImages()

                    name.append(it.name)
                    if (it.money != null) {
                        money = it.money
                    }
                    if (it.integral != null) {
                        integral = it.integral
                    }
                    timeEnd = DateUtil.formatDateByString(
                        "yyyy.MM.dd HH:mm",
                        "yyyy-MM-dd HH:mm:ss", item.endTime
                    )
                    if (!it.faithAuditStatus.isNullOrEmpty() && it.faithAuditStatus.toInt() == 1) {
                        mBinding.ivCxyx.visibility = View.VISIBLE
                    } else {
                        mBinding.ivCxyx.visibility = View.GONE
                    }
                    // 待审核或者待消费 判断是否可以取消
                    var cance =
                        getCanceStatus(item.status, it.cancelStatus, it.cancelTime, it.useStartTime)
                    if (cance) {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    }
                }
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                // 场馆预约
                mBinding.tvPeople.visibility = View.VISIBLE
                mBinding.tvActivityRoomNum.visibility = View.GONE
                mBinding.theirView.visibility = View.GONE
                var venueInfo = item.venueInfo
                venueInfo?.let {
                    image = it.image.getRealImages()
                    if (!it.money.isNullOrEmpty()) {
                        money = it.money.toDouble()
                    }
                    integral = it.integral.toInt()
                    mBinding.tvPeople.text = "到场人数：${it.useNum}人"
                    // 待审核或者待消费 判断是否可以取消
                    var cance =
                        getCanceStatus(item.status, it.cancelStatus, it.cancelTime, it.useStartTime)
                    if (cance) {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    }
                    if (!it.useStartTime.isNullOrEmpty()) {
                        timeBegin = DateUtil.formatDateByString(
                            "yyyy.MM.dd HH:mm",
                            "yyyy-MM-dd HH:mm:ss",
                            it.useStartTime
                        )
                    }
                    if (!it.useEndTime.isNullOrEmpty()) {
                        timeEnd = DateUtil.formatDateByString(
                            "HH:mm",
                            "yyyy-MM-dd HH:mm:ss",
                            it.useEndTime
                        )
                    }

                    if (item.isGuideOrder == 1) {
                        // 讲解员订单
                        mBinding.mReviewsTv.visibility = View.GONE
                        var yuyueStr: String = "(讲解)"
                        mBinding.tvOrderType.text = yuyueStr
                        name.append(it.venueName)
                    } else {
                        // 场馆预约订单
                        var yuyueStr: String = "(预约)"
                        mBinding.tvOrderType.text = yuyueStr
                        name.append(it.venueName)
                        if (it.hasRelationResource != null) {
                            if (it.hasRelationResource!!.exist) {
                                mBinding.mReviewsTv.visibility = View.GONE
                            } else {
                                mBinding.mReviewsTv.visibility = View.VISIBLE
                                if (it.hasRelationResource!!.date.isNullOrEmpty() || DateUtil.isBeforeNowV2(
                                        it.hasRelationResource!!.date!!
                                    )
                                ) {
                                    if (!it.hasRelationResource!!.date.isNullOrEmpty()) {
                                        mBinding.tvVenueCommtatorTip.visibility = View.VISIBLE
                                        mBinding.tvVenueCommtatorTip.text = mContext!!.getString(
                                            R.string.commentator_tip_date,
                                            it.hasRelationResource!!.date
                                        )
                                    }
                                    mBinding.mReviewsTv.backgroundDrawable =
                                        mContext!!.getDrawable(R.drawable.user_back_wite_strok_ff9e05_round_5)
                                    mBinding.mReviewsTv.isClickable = true
                                } else {
                                    mBinding.mReviewsTv.isClickable = false
                                    mBinding.tvVenueCommtatorTip.visibility = View.GONE
                                    mBinding.mReviewsTv.backgroundDrawable =
                                        mContext!!.getDrawable(R.drawable.user_back_wite_strok_f5_round_5)
                                }
                            }
                        } else {

                        }
                    }
                }
            }
            ResourceType.CONTENT_TYPE_SCENERY -> {
                // 景区预约
                mBinding.mReviewsTv.visibility = View.GONE
                mBinding.tvPeople.visibility = View.VISIBLE
                mBinding.theirView.visibility = View.GONE
                var venueInfo = item.venueInfo
                venueInfo?.let {
                    image = it.image.getRealImages()
                    var yuyueStr: String = "(预约)"
                    name.append(it.venueName)
                    mBinding.tvOrderType.text = yuyueStr
                    if (!it.money.isNullOrEmpty()) {
                        money = it.money.toDouble()
                    }
                    integral = it.integral
                    if (!it.useStartTime.isNullOrEmpty()) {
                        timeBegin = DateUtil.formatDateByString(
                            "yyyy.MM.dd HH:mm",
                            "yyyy-MM-dd HH:mm:ss",
                            it.useStartTime
                        )
                    }
                    if (!it.useEndTime.isNullOrEmpty()) {
                        timeEnd = DateUtil.formatDateByString(
                            "HH:mm",
                            "yyyy-MM-dd HH:mm:ss",
                            it.useEndTime
                        )
                    }

                    mBinding.tvPeople.text = "到场人数：${it.useNum}人"
                    // 待审核或者待消费 判断是否可以取消
                    var cance =
                        getCanceStatus(item.status, it.cancelStatus, it.cancelTime, it.useStartTime)
                    if (cance) {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    }
                }
            }
            ResourceType.CONTENT_TYPE_TRIPARTITE -> {
                // 第三方订单
                var tripartiteOrderInfo = item.tripartiteOrderInfo
                tripartiteOrderInfo?.let {
                    name.append(it.name)
                    mBinding.tvCancel.visibility = View.GONE
                    mBinding.tvPeople.visibility = View.GONE
                    var yuyueStr: String = "(预约)"
                    mBinding.tvOrderType.text = yuyueStr
                    timeBegin =
                        DateUtil.formatDateByString(
                            "yyyy.MM.dd HH:mm",
                            "yyyy-MM-dd HH:mm:ss",
                            it.orderStartTime
                        )
                    timeEnd =
                        DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm:ss", it.orderEndTime)
                    image = it.coverImage ?: ""
                }
            }
            else -> {

            }
        }
        if (item.orderNum >= 0)
            mBinding.tvActivityRoomNum.text = "X${item.orderNum}"


        var timeStart = "$timeBegin-$timeEnd"
        mBinding.time = "时间：${timeStart!!}"
        Glide.with(mContext!!).load(image)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)
        if (!name.isNullOrEmpty()) {
            mBinding.name.text = name
        }


        // 判断当前订单是已积分还是人民币计价的，和各种展示方式
        if (money == 0.0 && integral == 0) {
            mBinding.price = mContext!!.getString(R.string.order_free)
        } else if (money != 0.0 && integral == 0) {
            mBinding.price = mContext!!.getString(R.string.order_yuan) + money.toString()
        } else if (money == 0.0 && integral != 0) {
            mBinding.price = integral.toString() + mContext!!.getString(R.string.order_integral)
        } else {
            mBinding.price =
                mContext!!.getString(R.string.order_yuan) + money.toString() + integral.toString() + mContext!!.getString(
                    R.string.order_integral
                )
        }
        // 判断当前待支付的总价展示方式
        if (item.payMoney == 0.0 && item.payIntegral == "0") {
            mBinding.total = mContext!!.getString(R.string.order_free)
        } else if (item.payMoney != 0.0 && item.payIntegral == "0") {
            mBinding.total = mContext!!.getString(R.string.order_yuan) + item.payMoney.toString()
        } else if (item.payMoney == 0.0 && item.payIntegral != "0") {
            mBinding.total =
                item.payIntegral.toString() + mContext!!.getString(R.string.order_integral)
        } else {
            mBinding.total =
                mContext!!.getString(R.string.order_yuan) + item.payMoney.toString() + item.payIntegral.toString() + mContext!!.getString(
                    R.string.order_integral
                )
        }
        if (item.isGuideOrder == 1) {
            mBinding.tvPrice.visibility = View.GONE
            mBinding.total = "线下支付"
        } else {
            mBinding.tvPrice.visibility = View.VISIBLE
        }

        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    if (!item.orderType.isNullOrEmpty()) {
                        if (item.orderType == ResourceType.CONTENT_TYPE_TRIPARTITE) {
//                            if (item.tripartiteOrderInfo != null) {
//                                ARouter.getInstance()
//                                    .build(ARouterPath.Provider.WEB_ACTIVITY)
//                                    .withString("html", item.tripartiteOrderInfo!!.infoUrl)
//                                    .navigation()
//                            }
                            onAppointmentItemListener?.toOrderInfo(item.tripartiteOrderInfo!!.infoUrl)

                        } else {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                                .withString("orderId", item.id.toString())
                                .withString("type", item.orderType)
                                .navigation()
                        }
                    }
                }
            }
        mBinding.mReviewsTv.onNoDoubleClick {
            // 讲解预约
            if (item.venueInfo != null) {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_RESERVATION_COM_ACTIVITY)
                    .withInt("resourceType", 1)
                    .withString("venueId", item.venueInfo.venueId.toString())
                    .withString("orderId", item.id.toString())
                    .withString("orderCode", item.orderCode)
                    .navigation()
            }
        }
        mBinding.tvCancel.onNoDoubleClick {
            onAppointmentItemListener?.onCancelItem(item.orderCode)
        }
    }

    /**
     * 是否可以取消预约
     * @param status 订单状态
     * @param canceStatus 取消订单状态
     * @param startTime 订单生效时间 用于判断是否再取消订单有效期内
     */
    private fun getCanceStatus(
        status: Int,
        canceStatus: Int,
        startTime: String?,
        userStartTime: String
    ): Boolean {
        if (status == 11 || status == 4) {
            when (canceStatus) {
                0 -> {
                    // 不可取消
                    return false
                }
                1 -> {
                    // 随时取消
                    return true
                }
                2 -> {
                    // 24小时内
                    if (userStartTime.isNullOrEmpty()) {
                        return false
                    }
                    return DateUtil.isBeforeOneDay(userStartTime)
                }
                3 -> {
                    // 自定义取消时间
                    return true
                }
            }
        }
        return false

    }

    interface OnAppointmentItemListener {
        fun onCancelItem(orderCode: String)
        fun toOrderInfo(linkUrl: String?)
    }
}