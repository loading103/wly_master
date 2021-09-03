package com.daqsoft.usermodule.ui.order.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.AppointMentBean
import com.daqsoft.provider.bean.OrderListDataBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemMineAppointmentScBinding
import com.daqsoft.usermodule.databinding.ItemOrdersLsBinding
import com.jakewharton.rxbinding2.view.RxView
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Description 我的预约列表适配器 四川版本
 * @ClassName   MineAppointmentAdapter
 * @Author      luoyi
 * @Time        2020/5/15 17:33
 *
 * http://ticket.zkjxsoft.com/#/orderinfo?appid=wx896ca2c10f54478f&orderid=525855
 * http://ticket.zkjxsoft.com/index.html?v=0.2222076504248558#/orderinfo?orderid=525855
 */
class MineAppointmentScAdapter : RecyclerViewAdapter<ItemMineAppointmentScBinding, AppointMentBean> {

    var mContext: Context? = null

    var onAppointmentItemListener: OnAppointmentItemListener? = null

    constructor(context: Context) : super(R.layout.item_mine_appointment_sc) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemMineAppointmentScBinding, position: Int, item: AppointMentBean) {
        item?.let {
            mBinding.tvOrderCode.text = "订单编号：" + it.orderCode
            var imageUrl: String = ""
            if (it.resource != null) {
                mBinding.tvOrderName.text = "" + it.resource.resourceName
                imageUrl = it.resource.images.getRealImages()
            }
            Glide.with(mContext!!)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgOrder)

            // 订单数量
            mBinding.tvOrderNum.text = "数量：" + item.orderNum
            // 下单时间
            mBinding.tvOrderTime.text = "预约时间：" + it.orderStartTime + "-" + it.orderEndTime
            // 价格
            var small = BigDecimal("0.0")
            if (item.ticketPrice == null || item.ticketPrice == small) {
                mBinding.tvOrderPrice.text = "免费"
            } else {
                mBinding.tvOrderPrice.text = "" + item.ticketPrice
            }
            if (it.payMoney == null || item.payMoney == BigDecimal("0.0")) {
                mBinding.tvTotalPayValue.text = "免费"
            } else {
                mBinding.tvTotalPayValue.text = "" + item.payMoney
            }
            mBinding.tvOderStatus.text = getStatus(it.orderStatus)
            if (it.cancelStatus == "1") {
                //可以取消
                if (!it.cancelTime.isNullOrEmpty()) {
                    // 是否早于当前时间
                    if (DateUtil.isBeforeNow(it.cancelTime)) {
                        mBinding.tvCancel.visibility = View.GONE
                    } else {
                        mBinding.tvCancel.visibility = View.VISIBLE
                    }
                } else {
                    mBinding.tvCancel.visibility = View.VISIBLE
                }

            } else {
                // 不可以取消
                mBinding.tvCancel.visibility = View.GONE
            }
            mBinding.root.onNoDoubleClick {
                // 跳转订单详情
                onAppointmentItemListener?.toOrderInfo(it.infoUrl)
            }
            mBinding.tvCancel.onNoDoubleClick {
                if (!it.infoUrl.isNullOrEmpty()) {
                    onAppointmentItemListener?.toOrderInfo(it.infoUrl)
                }else{
                    ToastUtils.showMessage("未找到订单详情信息，请稍后再试")
                }
            }
        }

    }

    /**
     * 0待使用，1已使用，2已取消，3已失效，4待支付，5部分消费，6已退款，7部分退款
     */
    private fun getStatus(orderStatus: Int): String {
        return when (orderStatus) {
            0 -> {
                "待使用"
            }
            1 -> {
                "已使用"
            }
            2 -> {
                "已取消"
            }
            3 -> {
                "已失效"
            }
            4 -> {
                "待支付"
            }
            5 -> {
                "部分消费"
            }
            6 -> {
                "已退款"
            }
            7 -> {
                "部分退款"
            }
            else -> {
                ""
            }
        }
    }


    interface OnAppointmentItemListener {
        fun onCancelItem(orderCode: String)

        fun toOrderInfo(linkUrl: String?)
    }
}