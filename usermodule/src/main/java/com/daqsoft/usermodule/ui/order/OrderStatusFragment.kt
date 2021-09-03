package com.daqsoft.usermodule.ui.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ContractInfo
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentOrderStatusBinding
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.bean.OrderStatusConstant
import com.daqsoft.provider.businessview.event.UpdateOrderCanceStatus
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.usermodule.ui.order.widget.SingleCanceAppointPopWindow
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.backgroundDrawable
import java.util.concurrent.TimeUnit

/**
 * 订单详情的状态展示
 */
class OrderStatusFragment() :
    BaseFragment<FragmentOrderStatusBinding, OrderStatusFragmentViewModel>() {
    /**
     * 订单详情
     */
    var orderDetail: OrderDetail? = null

    /**
     * 单人健康码取消
     */
    var singleCanceAppointPopWIndow: SingleCanceAppointPopWindow? = null

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderStatusFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderStatusFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int = R.layout.fragment_order_status

    override fun injectVm(): Class<OrderStatusFragmentViewModel> =
        OrderStatusFragmentViewModel::class.java


    @SuppressLint("StringFormatInvalid")
    override fun initView() {
        EventBus.getDefault().register(this)
        orderDetail = arguments?.getParcelable(OrderDetailFragment.ORDER_DETAIL)
        mBinding.detail = orderDetail
        mModel.orderCode = orderDetail?.orderCode ?: ""
        mModel.orderDetail = orderDetail
        mBinding.vm = mModel


        if (orderDetail != null) {
            // 根据配置的是否可以取消状态和取消时间展示不同的类型
            var cancelTime = ""
            if (orderDetail!!.resourceType != null) {
                if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_SCENERY || orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_VENUE) {
                    if (orderDetail!!.venueInfo != null) {
                        cancelTime = getCanceTimes(orderDetail!!.venueInfo!!.cancelStatus)
                        mBinding.tvTimeNotice.text = cancelTime
                        mBinding.tvSubmit.visibility =
                            getCanceBtnVisible(orderDetail!!.venueInfo!!.cancelStatus)
                    }
                } else {
                    cancelTime = getCanceTimes(orderDetail!!.cancelStatus)
                    mBinding.tvTimeNotice.text = cancelTime
                    mBinding.tvSubmit.visibility = getCanceBtnVisible(orderDetail!!.cancelStatus)
                }
            }

            when (orderDetail!!.orderStatus) {
                OrderStatusConstant.ORDER_STATUS_WAITE_VALIDE -> {
                    // 待审核
                    mBinding.status = getString(R.string.order_waite_valid)
                    if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
                        mBinding.submitText = "取消订单"
                    } else {
                        mBinding.submitText = getString(R.string.order_cancel_apply)
                    }

                    mBinding.tvTimeNotice.text = cancelTime
                    mBinding.tvTimeNotice.visibility = View.VISIBLE
                    //取消订单
                    RxView.clicks(mBinding.tvSubmit)
                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                            canceOrder()
                        }
                }
                OrderStatusConstant.ORDER_STATUS_WAITE_COST -> {
                    // 待消费
                    if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_VENUE ||
                        orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_SCENERY
                    ) {
                        mBinding.status = getString(R.string.order_waite_use)
                    } else {
                        mBinding.status = getString(R.string.order_waite_cost)
                    }
                    if (mBinding.tvTimeNotice.text.isNullOrEmpty()) {
                        mBinding.tvTimeNotice.visibility = View.GONE
                    } else {
                        mBinding.tvTimeNotice.visibility = View.VISIBLE
                    }
                    mBinding.tvSubmit.visibility = if (orderDetail!!.cancelStatus != "0") {
                        mBinding.submitText = getString(R.string.order_cancel_order)
                        mBinding.tvSubmit.onNoDoubleClick {
                            canceOrder()
                        }
                        getCanceBtnVisible(orderDetail!!.cancelStatus)
                    } else {
                        View.GONE
                    }
                    // 讲解员预约按钮显示
                    mBinding.tvYuyueCommtator.visibility = if (orderDetail!!.venueInfo != null) {
                        if (orderDetail!!.isGuideOrder != 1 && orderDetail!!.venueInfo!!.hasRelationResource != null && orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_VENUE) {
                            if (orderDetail!!.venueInfo!!.hasRelationResource.exist) {
                                View.GONE
                            } else {
                                // 场馆预约时间 是否可以预约讲解员
                                if (orderDetail!!.venueInfo!!.hasRelationResource.date.isNullOrEmpty() || DateUtil.isBeforeNowV2(
                                        orderDetail!!.venueInfo!!.hasRelationResource.date!!
                                    )
                                ) {
                                    RxView.clicks(mBinding.tvYuyueCommtator)
                                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                                            ARouter.getInstance()
                                                .build(ARouterPath.VenuesModule.VENUES_RESERVATION_COM_ACTIVITY)
                                                .withInt("resourceType", 1)
                                                .withString(
                                                    "venueId",
                                                    orderDetail!!.venueInfo!!.venueId
                                                )
                                                .withString("orderId", orderDetail!!.id)
                                                .withString("orderCode", orderDetail!!.orderCode)
                                                .navigation()
                                        }
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
                            }
                        } else {
                            View.GONE
                        }
                    } else {
                        View.GONE
                    }
                }
                OrderStatusConstant.ORDER_STATUS_FINISHED -> {
                    // 已完成
                    mBinding.status = getString(R.string.order_finish)
                    // 我要评价
                    if (orderDetail!!.comment == 0) {
                        mBinding.tvSubmit.visibility = View.VISIBLE
                        mBinding.submitText = getString(R.string.order_comment)
                        mBinding.tvSubmit.setTextColor(context!!.resources.getColor(R.color.color_ff9e05))
                        mBinding.tvSubmit.background =
                            context!!.resources.getDrawable(R.drawable.user_back_wite_strok_d4_round_5)
                    } else {
                        mBinding.tvSubmit.visibility = View.GONE
                    }
                    if (!orderDetail!!.updateTime.isNullOrEmpty()) {
                        mBinding.tvTimeNotice.text = orderDetail!!.updateTime
                        mBinding.tvTimeNotice.visibility = View.VISIBLE
                    } else {
                        mBinding.tvTimeNotice.visibility = View.GONE
                    }
                    //评价晒单
                    RxView.clicks(mBinding.tvSubmit)
                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                            if (orderDetail != null) {
                                var resourceId: String? = ""
                                if (orderDetail!!.venueInfo != null) {
                                    resourceId = orderDetail!!.venueInfo!!.resourceId
                                } else if (!orderDetail!!.activityId.isNullOrEmpty()) {
                                    resourceId = orderDetail!!.activityId.toString()
                                }
                                if (!resourceId.isNullOrEmpty()) {
                                    MenuJumpUtils.gotoCommentPage(
                                        orderDetail!!.resourceType,
                                        resourceId,
                                        orderDetail!!.orderName,
                                        orderDetail!!.id
                                    )
                                }

                            }
                        }
                }
                OrderStatusConstant.ORDER_STATUS_NO_EFFEFECT -> {
                    // 已失效
                    mBinding.status = getString(R.string.order_no_effect)

                    mBinding.tvTimeNotice.text = orderDetail!!.updateTime
                    mBinding.tvTimeNotice.visibility = View.VISIBLE
                    mBinding.tvSubmit.visibility = View.GONE
                }
                OrderStatusConstant.ORDER_STATUS_NO_PASS -> {
                    // 未通过
                    mBinding.status = getString(R.string.order_no_pass)

                    mBinding.tvTimeNotice.text = orderDetail!!.updateTime
                    mBinding.tvTimeNotice.visibility = View.VISIBLE
                    mBinding.tvSubmit.visibility = View.GONE
                }
                OrderStatusConstant.ORDER_STATUS_CANCELED -> {
                    // 已取消
                    mBinding.status = getString(R.string.order_canceled)
                    mBinding.tvTimeNotice.text = orderDetail!!.updateTime
                    mBinding.tvTimeNotice.visibility = View.VISIBLE
                    mBinding.tvSubmit.visibility = View.GONE
                }
                else -> ""
            }

            mModel.cancelSuccess.observe(this, Observer
            {
                if (it) {
                    EventBus.getDefault().post(UpdateOrderCanceStatus())
//                    mBinding.status = getString(R.string.order_canceled)
//                    mBinding.tvTimeNotice.text = orderDetail!!.updateTime
//                    mBinding.tvTimeNotice.visibility = View.VISIBLE
//                    mBinding.tvSubmit.visibility = View.GONE
                }
            })
        }
        initViewModel()
    }

    private fun getCanceBtnVisible(canceStatus: String): Int {
        return if (canceStatus != "0") {
            if (canceStatus == "2") {
                //判断时间是否再24小时之前
                getResourceTypeCanceVisible()
            } else {
                View.VISIBLE
            }
        } else {
            View.GONE
        }
    }

    private fun getResourceTypeCanceVisible(): Int {
        return if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
            if (!orderDetail!!.roomStartTime.isNullOrEmpty() && DateUtil.isBeforeOneDay(orderDetail!!.roomStartTime!!)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            if (!orderDetail!!.useStartTime.isNullOrEmpty() && DateUtil.isBeforeOneDay(orderDetail!!.useStartTime)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun getCanceTimes(canceStatus: String): String {
        if (canceStatus.isNullOrEmpty()) {
            return ""
        }
        return when (canceStatus) {
            "0" -> {
                // 不可取消
                getString(R.string.order_can_cancel_not)
            }
            "1" -> {
                // 随时
                getString(R.string.order_can_cancel_any)
            }
            "2" -> {
                // 24小时
                getString(R.string.order_can_use_cancel_24h, getCanceOrderTime())
            }
            "3" -> {
                // 自定义
                getString(
                    R.string.order_can_cancel_time,
                    getCanceOrderCustomTime()
                )
            }
            else -> {
                ""
            }
        }
    }

    private fun getCanceOrderTime(): String {
        return if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
            if (!orderDetail!!.roomStartTime.isNullOrEmpty()) {
                DateUtil.getBeforDay(orderDetail!!.roomStartTime)
            } else {
                "使用前24小时可以退"
            }
        } else {
            if (!orderDetail!!.useStartTime.isNullOrEmpty()) {
                DateUtil.getBeforDay(orderDetail!!.useStartTime)
            } else {
                "使用前24小时可以退"
            }
        }
    }

    private fun getCanceOrderCustomTime(): String {
        return if (orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
            if (!orderDetail!!.roomStartTime.isNullOrEmpty()) {
                DateUtil.getBefoHour(orderDetail!!.roomStartTime, orderDetail!!.cancelTime)
            } else {
                "使用前24小时可以退"
            }
        } else {
            if (!orderDetail!!.useStartTime.isNullOrEmpty()) {
                DateUtil.getBefoHour(orderDetail!!.useStartTime, orderDetail!!.cancelTime)
            } else {
                "使用前24小时可以退"
            }
        }
    }

    private fun initViewModel() {
        mModel.canceActivityOrderSuccess.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("取消成功~")
                EventBus.getDefault().post(UpdateOrderCanceStatus())
            } else {

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
                mModel.cancelOrder()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateCommentStatus(event: UpdateOrderCommentStatus) {
        if (event != null && orderDetail != null && orderDetail!!.id == event.updateOrderId &&
            mBinding != null && mBinding.tvSubmit != null
        ) {
            // 我要评价
            orderDetail?.comment = 1
            mBinding.tvSubmit.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        singleCanceAppointPopWIndow = null
    }


    fun canceOrder() {
        if (orderDetail != null && orderDetail!!.resourceType != null) {
            if ((orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_SCENERY || orderDetail!!.resourceType == ResourceType.CONTENT_TYPE_VENUE)
                && orderDetail!!.isGuideOrder !== 1
            ) {
                if (orderDetail!!.hasAttached == 1) {
                    // 多人预约部分取消
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_APPOINT_CANCE_ORDER_ACITIVTY)
                        .withString("orderId", orderDetail!!.id)
                        .withString("orderCode", orderDetail!!.orderCode)
                        .navigation()
                } else {
                    // 单人健康信息预约取消
                    showSingleCancePop()
                }
            } else {
                noticeConfirm(this@OrderStatusFragment.activity!!)
            }
        }
    }

    fun showSingleCancePop() {
        if (singleCanceAppointPopWIndow == null) {
            singleCanceAppointPopWIndow = SingleCanceAppointPopWindow(context!!)
            singleCanceAppointPopWIndow?.onSingleCanceAppointListener =
                object : SingleCanceAppointPopWindow.OnSingleCanceAppointListener {
                    override fun onSingleCanceOrder(
                        orderNum: Int,
                        canceReason: String,
                        remark: String?
                    ) {
                        showLoadingDialog()
                        mModel.canceActivityOrderById(
                            orderDetail!!.orderCode,
                            orderNum,
                            "",
                            canceReason,
                            remark
                        )
                    }

                }
            singleCanceAppointPopWIndow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            singleCanceAppointPopWIndow?.softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        }
        if (!orderDetail!!.orderNum.isNullOrEmpty()) {
            singleCanceAppointPopWIndow?.orderUserNum =
                orderDetail!!.orderNum.toInt() - orderDetail!!.backNum
        } else {
            singleCanceAppointPopWIndow?.orderUserNum = 1
        }
        singleCanceAppointPopWIndow?.inSelectReason()
        if (!singleCanceAppointPopWIndow!!.isShowing) {
            if (activity != null) {
                singleCanceAppointPopWIndow?.showAtLocation(
                    (activity as OrderDetailActivity).getRootView(),
                    Gravity.BOTTOM,
                    0,
                    0
                )
            }

        }
    }
}

class OrderStatusFragmentViewModel : BaseViewModel() {
    var orderDetail: OrderDetail? = null

    /**
     *  取消预订
     */
    var cancel: ReplyCommand? = null


    /**
     * 取消预订
     */
    var cancelSuccess = MutableLiveData<Boolean>(false)

    var canceActivityOrderSuccess = MutableLiveData<Boolean>()
    var orderCode = ""
    fun cancelOrder() {
        mPresenter.value?.loading = true
        UserRepository().userService.postCancelOrder(orderCode)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        ToastUtils.showMessage("取消成功")
                        cancelSuccess.postValue(true)
                    } else {
                        ToastUtils.showMessage(response.message)
                    }
                }
            })
    }

    var orderId = ""

    /**
     *常规取消
     */
    fun cancelOrderById() {
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.cancelOrder(orderId)
            .excut(object : ElectronicObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 1) {
                        ToastUtils.showMessage("取消成功")
                        cancelSuccess.postValue(true)
                    } else {
                        ToastUtils.showMessage(response.message)
                    }
                }
            })
    }

    /**
     *酒店取消
     */
    fun cancelHotelOrderById() {
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.cancelHotelOrder(orderId)
            .excut(object : ElectronicObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 1) {
                        ToastUtils.showMessage("取消成功")
                        cancelSuccess.postValue(true)
                    } else {
                        ToastUtils.showMessage(response.message)
                    }
                }
            })
    }


    /**
     *线路取消
     */
    fun cancelLineOrderById() {
        mPresenter.value?.loading = true
        ElectronicRepository.electronicService.cancelLineOrder(orderId)
            .excut(object : ElectronicObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 1) {
                        ToastUtils.showMessage("取消成功")
                        cancelSuccess.postValue(true)
                    } else {
                        ToastUtils.showMessage(response.message)


                    }
                }
            })
    }

    /**
     * 取消订单
     * @param orderCode 订单编号
     * @param canceNum 取消数量
     * @param operateIds 随行人数据id
     * @param operateExcuse 随行人取消方式
     * @param cancelNum 取消数量
     * @param operateExcuse 取消原因
     * @param remark 取消备注
     */
    fun canceActivityOrderById(
        orderCode: String,
        cancelNum: Int = 0,
        operateIds: String? = "",
        operateExcuse: String? = "",
        remark: String? = ""
    ) {
        mPresenter?.value?.loading = false
        var params: HashMap<String, String> = hashMapOf()
        params["orderCode"] = orderCode
        if (cancelNum > 0) {
            params["cancelNum"] = cancelNum.toString()
        }
        if (!operateExcuse.isNullOrEmpty()) {
            params["operateExcuse"] = operateExcuse
        }
        if (!operateIds.isNullOrEmpty()) {
            params["operateIds"] = operateIds
        }
        if (!operateExcuse.isNullOrEmpty()) {
            params["operateExcuse"] = operateExcuse
        }
        if (!remark.isNullOrEmpty()) {
            params["remark"] = remark
        }
        UserRepository().userService.canceActivityOrder(params)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    canceActivityOrderSuccess?.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("" + response.message)
                    canceActivityOrderSuccess?.postValue(false)
                }

            })
    }
}