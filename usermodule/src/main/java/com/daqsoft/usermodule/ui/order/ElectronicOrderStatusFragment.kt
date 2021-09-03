package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentOrderStatusBinding
import com.daqsoft.usermodule.databinding.ShopOrderTopBinding
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.OrderDetailBean
import com.daqsoft.provider.bean.OrderStatusConstant
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * 小电商顶部订单状态视图
 */
class ElectronicOrderStatusFragment() :
    BaseFragment<ShopOrderTopBinding, OrderStatusFragmentViewModel>() {
    /**
     * 订单详情
     */
    var orderDetail: OrderDetailBean? = null

    constructor(dr: OrderDetailBean) : this() {
        this.orderDetail = dr
    }

    override fun getLayout(): Int = R.layout.shop_order_top

    override fun injectVm(): Class<OrderStatusFragmentViewModel> =
        OrderStatusFragmentViewModel::class.java


    @SuppressLint("StringFormatInvalid", "CheckResult")
    override fun initView() {
        mModel.orderId = orderDetail?.id.toString() ?: ""
        RxView.clicks(mBinding.mReviewsTv)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_ELECTRONIC_COMMENT_ACTIVITY)
                    .navigation()
            }
        //取消订单
        RxView.clicks(mBinding.mCancelTv)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                noticeConfirm(activity as ElectronicOrderDetailActivity)
            }
        // 立即支付
        RxView.clicks(mBinding.mPayNowTv)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                // 支付 调用网页得支付
//                var payUrl = BaseApplication.electronicPayUrl + "/html/paymentIn.html?orderId=${orderDetail!!.id}&payOperate=ORDER_PAY&" +
//                        "unid=${uuid}&token=${token}&url=http://sub8470972.c.xds.daqsoft.com/order/result"
//                ARouter.getInstance()
//                    .build(ARouterPath.Provider.WEB_ACTIVITY)
//                    .withString("mTitle", "支付")
//                    .withString("html", payUrl)
//                    .navigation()
                var shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                var payUrl = "${shopUrl}/order/detail?id=${orderDetail?.id}&unid=${uuid}&token=${token}&encryption=${encry}"
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "支付")
                    .withString("html", payUrl)
                    .navigation()
            }
        // 退款
        if (orderDetail!!.allowRefund!!) {
            RxView.clicks(mBinding.mRefundTv)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_ELECTRONIC_REBACK_ACTIVITY)
                        .withString(IntentConstant.OBJECT, orderDetail?.id.toString())
                        .withString(IntentConstant.TYPE, orderDetail!!.orderType)
                        .navigation()
                }
        }



        when (orderDetail!!.orderStatus) {
            "0" -> {
                // 待支付状态
                mBinding.mStatusTv.text = getString(R.string.order_waite_pay)
                mBinding.mCancelTv.visibility = View.VISIBLE
                mBinding.mPayNowTv.visibility = View.VISIBLE
            }
            "1" -> {
                // 已支付状态
                when (orderDetail!!.orderType) {
                    // 酒店
                    "5" -> {
                        mBinding.mStatusTv.text = getString(R.string.order_consume_waite_confirm)
                    }
                    else -> {
                        mBinding.mStatusTv.text = getString(R.string.order_waite_payed)
                    }
                }
                if (orderDetail!!.allowRefund == true) {
                    mBinding.mCancelTv.visibility = View.VISIBLE
                }
            }
            "2", "4" -> {
                when (orderDetail!!.orderType) {
                    "6" -> {
                        // 线路订单
                        mBinding.mStatusTv.text = orderDetail!!.orderStatusName
                        if (orderDetail!!.allowRefund!!) {
                            // 判断是否允许退货
                            mBinding.mRefundTv.visibility = View.VISIBLE

                        } else {
                            mBinding.mRefundTv.visibility = View.GONE
                        }
                    }
                    "5" -> {
                        // 酒店订单
                        mBinding.mStatusTv.text = orderDetail!!.orderStatusName
                        // 酒店订单 已完成应取消退款功能
//                        if (orderDetail!!.allowRefund!!) {
//                            // 判断是否允许退货
//                            mBinding.mRefundTv.visibility = View.VISIBLE
//
//                        } else {
//                            mBinding.mRefundTv.visibility = View.GONE
//                        }
                        // 酒店订单无立即消费选项
//                        mBinding.mBooking.visibility = View.VISIBLE
//                        mBinding.mBooking.text = getString(R.string.order_cost_imitation)
                    }
                    else -> {
                        // 已完成状态
                        mBinding.mStatusTv.text = getString(R.string.order_finish)
                        //                mBinding.mLogisticsTv.visibility = View.VISIBLE
                        // 点评功能未做
//                        mBinding.mReviewsTv.visibility = View.VISIBLE
                    }
                }
                RxView.clicks(mBinding.mBooking)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING_WEB)
                            .withString(IntentConstant.OBJECT, orderDetail!!.id.toString())
                            .navigation()
                    }
            }
            "3"->{
                mBinding.mStatusTv.text =orderDetail!!.orderStatusName
            }
               "9" -> {
                // 已关闭状态
                mBinding.mStatusTv.text = getString(R.string.order_waite_close)
                mBinding.vOrderTopClose.visibility = View.VISIBLE
                mBinding.vOrderTopOpration.visibility = View.GONE
                mBinding.mStatusTv.visibility = View.GONE
            }
            "20" -> {
                // 待发货状态
                mBinding.mStatusTv.text = getString(R.string.order_to_be_delivered)
                mBinding.mLogisticsTv.visibility = View.GONE
                if (orderDetail!!.allowRefund!!) {
                    // 判断是否允许退货
                    mBinding.mRefundTv.visibility = View.VISIBLE

                } else {
                    mBinding.mRefundTv.visibility = View.GONE
                }

            }
            "21" -> {
                // 已发货
                mBinding.mStatusTv.text = getString(R.string.order_shipped)
            }
            "40" -> {
                // 待预约
                mBinding.mStatusTv.text = getString(R.string.order_waite_book)
                mBinding.mBooking.visibility = View.VISIBLE
                if (orderDetail!!.allowRefund!!) {
                    // 判断是否允许退货
                    mBinding.mRefundTv.visibility = View.VISIBLE
                } else {
                    mBinding.mRefundTv.visibility = View.GONE
                }
                RxView.clicks(mBinding.mBooking)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING_WEB)
                            .withString(IntentConstant.OBJECT, orderDetail!!.id.toString())
                            .navigation()
                    }

            }

            "12" -> {
                // 已失效
                mBinding.mStatusTv.text = getString(R.string.order_no_effect)
            }
            "10" -> {
                // 待消费
                mBinding.mStatusTv.text = getString(R.string.order_waite_cost)
                mBinding.mBooking.visibility = View.VISIBLE
                mBinding.mBooking.text = getString(R.string.order_cost_imitation)
                if (orderDetail!!.allowRefund!!) {
                    // 判断是否允许退货
                    mBinding.mRefundTv.visibility = View.VISIBLE

                } else {
                    mBinding.mRefundTv.visibility = View.GONE
                }
                RxView.clicks(mBinding.mBooking)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL)
                            .withString(IntentConstant.OBJECT, orderDetail!!.id.toString() ?: "")
                            .withString(IntentConstant.TYPE, orderDetail!!.type ?:"1")
                            .navigation()
//                        ARouter.getInstance()
//                            .build(ARouterPath.UserModule.USER_CONSUME_ELECTRONIC_BOOKING_WEB)
//                            .withString(IntentConstant.OBJECT, orderDetail!!.id)
//                            .navigation()
                    }
            }
        }



        if (orderDetail!!.orderStatus == "0") {

        }

        // 待发货状态 不知道为什么这么写，已屏蔽
//        if (orderDetail!!.orderStatus == "20") {
//            mBinding.mRefundTv.visibility = View.VISIBLE
//        }
        mModel.cancelSuccess.observe(this, Observer {
            if (it) {
                mBinding.mStatusTv.text = getString(R.string.order_waite_close)
                mBinding.vOrderTopClose.visibility = View.VISIBLE
                mBinding.vOrderTopOpration.visibility = View.GONE
                mBinding.mStatusTv.visibility = View.GONE
            }
        })
    }

    override fun initData() {

    }

    /**
     * 弹出确认框
     * @param sureCommand 动作
     */
    fun noticeConfirm(context: Activity) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogNoticeBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_notice, null, false
        )

        window!!.setContentView(binding.root)
        binding.label = context.getString(R.string.order_cancel_notice_label)
        binding.notice = context.getString(R.string.order_cancel_notice)
        binding.cancel = context.getString(R.string.order_cancel)
        binding.sure = context.getString(R.string.user_str_conform)

        binding.cancelSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
        binding.sureSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()

                if(orderDetail!!.orderStatus == "0"){
                    mModel.cancelOrderById()
                    return
                }

                when (orderDetail!!.orderType) {
                    "1", "2", "3" -> {
                        mModel.cancelOrderById()
                    }
                    "5" -> {
                        mModel.cancelHotelOrderById()
                    }
                    "6" -> {
                        mModel.cancelLineOrderById()
                    }
                }

            }
        }
    }


}

class ElectronicOrderStatusFragmentViewModel : BaseViewModel() {
//    var orderDetail: OrderDetail? = null
//    /**
//     *  取消预订
//     */
//    var cancel: ReplyCommand? = null
//
//
//    /**
//     * 取消预订
//     */
//    fun cancelOrder() {
//        mPresenter.value?.loading = true
//        UserRepository().userService.postCancelOrder(orderDetail!!.orderCode)
//            .excute(object : BaseObserver<Any>(mPresenter) {
//                override fun onSuccess(response: BaseResponse<Any>) {
//                    toast.postValue(response.message)
//                }
//            })
//    }
}