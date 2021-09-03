package com.daqsoft.usermodule.ui.order

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentOrderDetailBinding
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.uitls.StringUtils
import kotlinx.android.synthetic.main.fragment_order_detail.*
import org.jetbrains.anko.append
import org.jetbrains.anko.support.v4.toast

/**
 * 订单的详情
 * @author PuHua
 * @data
 */
class OrderDetailFragment() :
    BaseFragment<FragmentOrderDetailBinding, OrderDetailFragmentViewModel>() {

    var orderDetail: OrderDetail? = null
    var type: String = ""

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        const val TYPE = "type"
        fun newInstance(dr: OrderDetail, type: String): OrderDetailFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            bundle.putString(TYPE, type)
            val orderDetailFragment = OrderDetailFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int = R.layout.fragment_order_detail

    override fun injectVm(): Class<OrderDetailFragmentViewModel> =
        OrderDetailFragmentViewModel::class.java


    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        type = arguments?.getString(TYPE, "").toString()

        orderDetail?.let {
            mBinding.image.setOnClickListener {
                toResourceDetail()
            }
            mBinding.name.setOnClickListener {
                toResourceDetail()
            }
            if (!it.venueList.isNullOrEmpty()) {
                mBinding.recyclerView.adapter = VenueAdapter(it.venueList!!)
            }



            mBinding.detail = orderDetail
            mBinding.name.text = "" + orderDetail?.orderName
            var numUnit: String = " "
            if (!it.resourceType.isNullOrEmpty() && orderDetail != null) {
                if (it.resourceType == ResourceType.CONTENT_TYPE_VENUE || it.resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                    mBinding.clActivityShiInfo.visibility = View.GONE
                    mBinding.llVenueYuyue.visibility = View.VISIBLE
                    mBinding.time.visibility = View.GONE
                    mBinding.ivSeat.visibility = View.GONE
                    if (it.backNum > 0) {
                        mBinding.tvTotalCanceNum.visibility = View.VISIBLE
                        mBinding.tvTotalCanceNum.text = "已取消 x${it.backNum}"
                    }
                    if (!orderDetail!!.orderNum.isNullOrEmpty() && orderDetail!!.orderNum != "0") {
                        mBinding.tvTotalNum.text = "x${orderDetail?.orderNum}"
                    }
                    if (it.venueInfo != null) {
                        if (it.venueInfo!!.reservationType == ResourceType.PERSON) {
                            mBinding.appointType.text = "个人预约"
                            mBinding.llCompanyName.visibility = View.GONE
                        } else {
                            mBinding.appointType.text = "团体预约"
                            mBinding.llCompanyName.visibility = View.VISIBLE
                        }
                        mBinding.tvOrderTimeValue.text = DateUtil.getTwoDateStrs(
                            it.venueInfo!!.useStartTime,
                            it.venueInfo!!.useEndTime
                        )
                        var forregroundCSpan = ForegroundColorSpan(Color.parseColor("#999999"))
                        var name = SpannableStringBuilder("")
                        var yuyueStr: String = "(预约)"
                        if (it.resourceType == ResourceType.CONTENT_TYPE_SCENERY) {
                            tv_label_address.text = "景区地址"
                            tv_label_contact.text = "景区电话"
                            if (it.venueInfo!!.phone.isNullOrEmpty()) {
                                tv_label_contact.visibility = View.GONE
                                tv_value_contact.visibility = View.GONE
                                img_arrow_contact.visibility = View.GONE
                            }
                            tv_label_order_num.text = "预订数量"
                        } else {
                            tv_label_address.text = "联系地址"
                            tv_label_contact.text = "联系电话"
                            tv_label_order_num.text = "预约人数"
                            numUnit = "人"
                            // 讲解员预约
                            if (it.isGuideOrder == 1) {
                                mBinding.ivTotalPay.content = "到馆支付"
                                mBinding.tvOrderTimeLabel.text = "讲解时间"
                                bindCommtatorData(it.venueInfo!!)
                                yuyueStr = "(讲解)"
                            }
                        }
                        name.append(yuyueStr, forregroundCSpan, 0, yuyueStr.length)
                        name.append("" + it.orderName)
                        mBinding.name.text = name
                        mBinding.rvAddress.onNoDoubleClick {
                            if (!it.venueInfo!!.lat.isNullOrEmpty() && !it.venueInfo!!.lon.isNullOrEmpty()) {
                                if (MapNaviUtils.isGdMapInstalled()) {
                                    MapNaviUtils.openGaoDeNavi(
                                        BaseApplication.context,
                                        0.0,
                                        0.0,
                                        null,
                                        it.venueInfo!!.lat.toDouble(),
                                        it.venueInfo!!.lon.toDouble(),
                                        it.venueInfo!!.address
                                    )
                                } else {
                                    toast("非常抱歉，系统未安装地图软件")
                                }
                            } else {
                                toast("非常抱歉，暂无位置信息")
                            }
                        }
                        mBinding.rvContact.onNoDoubleClick {
                            if (!it.venueInfo!!.phone.isNullOrEmpty())
                                SystemHelper.callPhone(context!!,it.venueInfo!!.phone)
                        }

                    }
                } else {
                    // 活动室订单
                    mBinding.clActivityShiInfo.visibility = View.VISIBLE
                    mBinding.llVenueYuyue.visibility = View.GONE
                    mBinding.validityView.content =
                        DateUtil.getTwoDateStrs(it.roomStartTime, it.roomEndTime)
                    // 开启了免审的才显示免审
                    if (!orderDetail!!.faithAuditStatus.isNullOrEmpty() && orderDetail!!.faithAuditStatus.toInt() == 1) {
                        mBinding.labelNotice.visibility = View.VISIBLE
                        mBinding.labelNotice.text =
                            getString(R.string.order_label_notice, orderDetail!!.faithAuditValue)
                    }
                    // 诚信优享
                    if (!orderDetail!!.faithUseStatus.isNullOrEmpty() && orderDetail!!.faithUseStatus.toInt() == 1) {
                        mBinding.ivCxyx.visibility = View.VISIBLE
                        mBinding.labelNotice.visibility = View.VISIBLE
                        mBinding.labelNotice.setCompoundDrawablesWithIntrinsicBounds(
                            resources.getDrawable(
                                R.mipmap.mine_book_logo_cxyx
                            ), null, null, null
                        )
                        mBinding.labelNotice.text =
                            getString(R.string.order_label_cxyx_notice, orderDetail!!.faithUseValue)

                    }

                    if (!orderDetail!!.seatList.isNullOrEmpty()) {
                        mBinding.ivSeat.content = parseSeat(orderDetail!!.seatList)
                    } else {
                        mBinding.ivSeat.visibility = View.GONE
                    }
                }
            }

            if (type == ResourceType.CONTENT_TYPE_ACTIVITY) {
                mBinding.bookingPeople.visibility = View.GONE
                mBinding.label.text = getString(R.string.order_activity_information)
                mBinding.bookingTime.visibility = View.VISIBLE
                mBinding.rlBookPhone.visibility = View.GONE
                mBinding.validityView.setLabel("有效期")
                mBinding.validityView.content =
                    "${DateUtil.getTwoDateStrs(it.orderIndateStart, it.orderIndateEnd)}"
                mBinding.bookingTime.content =
                    "${DateUtil.getTwoDateStrs(it.useStartTime, it.useEndTime)}"
            }
            mBinding.numUnit = numUnit
            // 待审核
            mBinding.time.setLabel(getString(R.string.order_number))
            mBinding.time.content = orderDetail!!.orderNum
            mBinding.ivTotalPay.setLabel(getString(R.string.order_pay))
            mBinding.ivTotalPay.content = orderDetail!!.orderNum
            // 展示付费方式
            if (StringUtils.isZero(orderDetail!!.payMoney) && StringUtils.isZero(orderDetail!!.payIntegral)) {
                mBinding.ivTotalPay.content = getString(R.string.order_free)
            } else if (!StringUtils.isZero(orderDetail!!.payMoney) && StringUtils.isZero(orderDetail!!.payIntegral)) {
                mBinding.ivTotalPay.content =
                    getString(R.string.order_yuan) + orderDetail!!.payMoney
            } else if (StringUtils.isZero(orderDetail!!.payMoney) && !StringUtils.isZero(orderDetail!!.payIntegral)) {
                mBinding.ivTotalPay.content =
                    orderDetail!!.payIntegral + getString(R.string.order_integral)
            } else {
                if (!orderDetail!!.payMoney.isNullOrEmpty()) {
                    mBinding.ivTotalPay.content =
                        getString(R.string.order_yuan) + orderDetail!!.payMoney.toDouble()
                } else if (!orderDetail!!.payIntegral.isNullOrEmpty()) {
                    mBinding.ivTotalPay.content = "" +
                            orderDetail!!.payIntegral.toInt() + getString(
                        R.string.order_integral
                    )
                }

            }

            if (orderDetail!!.isGuideOrder == 1) {
                if (orderDetail!!.venueInfo != null) {
                    mBinding.ivTotalPay.content =
                        "${orderDetail!!.venueInfo!!.guideOrderPayMoney}元(到馆支付)"
                }
            }

            mBinding.bookingAddress.onNoDoubleClick {
                if (orderDetail != null && !orderDetail?.latitude.isNullOrEmpty() && !orderDetail?.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            BaseApplication.context, 0.0, 0.0, null,
                            orderDetail!!.latitude.toDouble(), orderDetail!!.longitude.toDouble(),
                            orderDetail!!.address
                        )
                    } else {
                        toast("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    toast("非常抱歉，暂无位置信息")
                }
            }
            mBinding.bookingPhone.onNoDoubleClick {
                if (!it.servicePhone.isNullOrEmpty())
                    SystemHelper.callPhone(context!!,it.servicePhone!!)
            }
        }
    }

    private fun toResourceDetail() {
        if (orderDetail != null) {
            if (orderDetail!!.resourceType != null && orderDetail!!.activityId != null) {
                MenuJumpUtils.gotoResourceDetail(
                    orderDetail!!.resourceType,
                    orderDetail!!.activityId!!,
                    orderDetail!!.activityType
                )
            }
        }
    }

    private fun parseSeat(seats: List<Seat>): String {
        var result = ""
        for (item in seats) {
            result += "${item.row}排${item.col}座,"
        }
        return result.substring(0, result.lastIndexOf(","))
    }


    override fun initData() {
        val timeBegin = DateUtil.formatDateByString(
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            orderDetail!!.orderIndateStart
        )

        var timeFormat = "HH:mm"
        if (type == ResourceType.CONTENT_TYPE_ACTIVITY) {
            timeFormat = "yyyy-MM-dd HH:mm"
        } else {
            val timeEnd = DateUtil.formatDateByString(
                timeFormat,
                "yyyy-MM-dd HH:mm:ss",
                orderDetail!!.orderIndateEnd
            )
            mBinding.bookingTime.content = "${timeBegin}-${timeEnd}"
        }

    }

    private fun bindCommtatorData(venueInfo: VenueInfo) {
        ll_order_commentator_info.visibility = View.VISIBLE
        if (!venueInfo.guideOrderLanguage.isNullOrEmpty()) {
            mBinding.llOrderCommentatorInfo.tvComtatorLgValue.text =
                if (venueInfo.guideOrderLanguage == "CH") {
                    "中文"
                } else {
                    "英文"
                }
            if (!venueInfo.guideExhibitions.isNullOrEmpty()) {
                for (item in venueInfo.guideExhibitions!!) {
                    var exhibitionView = LayoutInflater.from(context!!)
                        .inflate(R.layout.item_order_haved_exhall, null)
                    var tvExhibitionName: TextView =
                        exhibitionView!!.findViewById(R.id.tv_exhall_name)
                    var tvExhibitionIsRecommed: TextView =
                        exhibitionView!!.findViewById(R.id.tv_exhall_recommend)
                    if (item.recommend == 1) {
                        tvExhibitionIsRecommed.visibility = View.VISIBLE
                    } else {
                        tvExhibitionIsRecommed.visibility = View.GONE
                    }
                    tvExhibitionName.text = "${item.name}"
                    mBinding.llOrderCommentatorInfo.lvComtatorExhallValue.addView(exhibitionView)

                }

            }
            // 到馆时间
            if (!venueInfo.expectedTime.isNullOrEmpty()) {
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.text =
                    "" + venueInfo.expectedTime
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeLabel.visibility = View.VISIBLE
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.visibility = View.VISIBLE
            } else {
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeLabel.visibility = View.GONE
                mBinding.llOrderCommentatorInfo.tvComtatorIntimeValue.visibility = View.GONE
            }
            // 讲解员
            if (!venueInfo.guideName.isNullOrEmpty()) {
                try {
                    if (venueInfo.guideName!!.endsWith(",")) {
                        venueInfo.guideName = venueInfo.guideName!!.removeRange(
                            venueInfo.guideName!!.length - 1,
                            venueInfo.guideName!!.length
                        )
                    }
                } catch (e: Exception) {
                }
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.text =
                    "" + venueInfo.guideName
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.visibility = View.VISIBLE
                mBinding.llOrderCommentatorInfo.tvComtatorPersonLabel.visibility = View.VISIBLE
            } else {
                mBinding.llOrderCommentatorInfo.tvComtatorPersonValue.visibility = View.GONE
                mBinding.llOrderCommentatorInfo.tvComtatorPersonLabel.visibility = View.GONE
            }
        }
        mBinding.llOrderCommentatorInfo.tvComtatorCostValue.text =
            "${venueInfo.guideOrderPayMoney}元(到馆支付)"
    }

    inner class VenueAdapter : BaseQuickAdapter<Venue, BaseViewHolder> {

        constructor(data: List<Venue>) : super(R.layout.item_venue_layout, data)

        override fun convert(helper: BaseViewHolder?, item: Venue?) {
            helper?.setText(R.id.booking_venue, item?.name)
            helper?.itemView?.onNoDoubleClick {
                var path = ""
                when (item?.resourceType) {
                    // 场馆
                    ResourceType.CONTENT_TYPE_VENUE -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    // 农家乐
                    ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                        path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                    }
                    // 	活动室
                    ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                        path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
                    }
                    // 酒店
                    ResourceType.CONTENT_TYPE_HOTEL -> {
                        path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                    }
                    // 景区
                    ResourceType.CONTENT_TYPE_SCENERY -> {
                        path = MainARouterPath.MAIN_SCENIC_DETAIL
                    }
                    // 景点
                    ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                        path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
                    }
                    // 餐饮
                    ResourceType.CONTENT_TYPE_RESTAURANT -> {
                        path = MainARouterPath.MAIN_FOOD_DETAIL
                    }
                    // 活动
                    ResourceType.CONTENT_TYPE_ACTIVITY -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    else -> {
                        toast("功能开发中!")
                        return@onNoDoubleClick
                    }
                }
                ARouter.getInstance().build(path)
                    .withString("id", item.id)
                    .navigation()
            }
        }
    }

}

class OrderDetailFragmentViewModel : BaseViewModel() {

}

