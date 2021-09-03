package com.daqsoft.usermodule.ui.order

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.daqsoft.usermodule.ui.order.adapter.MineCanceAppointAdapter
import com.daqsoft.usermodule.uitls.StringUtils
import io.reactivex.functions.Function
import org.jetbrains.anko.image
import org.jetbrains.anko.support.v4.toast


/**
 * 订单的详情
 */
class OrderInformationFragment() :
    BaseFragment<FragmentOrderInformationBinding, BaseViewModel>() {
    /**
     * 订单详情
     */
    var orderDetail: OrderDetail? = null


    override fun getLayout(): Int = R.layout.fragment_order_information

    override fun injectVm(): Class<BaseViewModel> =
        BaseViewModel::class.java

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderInformationFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderInformationFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        mBinding.detail = orderDetail


        // 订单编码复制到剪切板
        mBinding.clip = object : ReplyCommand {
            override fun run() {
                val cm: ClipboardManager =
                    context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = ClipData.newPlainText("Label", orderDetail!!.orderCode)

                cm.primaryClip = clipData
                toast(getString(R.string.order_string_has_clip))
            }

        }
        if (orderDetail != null) {
            if (orderDetail!!.remark.isNullOrEmpty() || orderDetail!!.remark == "暂无") {
                mBinding.bookingRemark.visibility = View.GONE
            } else {
                mBinding.bookingRemark.visibility = View.VISIBLE
            }
            if (!orderDetail!!.cardType.isNullOrEmpty()) {
                mBinding.bookingContactIdcard.content =
                    "(${CertTypes.getCertTypeName(orderDetail!!.cardType)})${orderDetail!!.idCard}"
            } else {
                mBinding.bookingContactIdcard.content = "${orderDetail!!.idCard}"
            }
        }
    }


    override fun initData() {

    }

}

/**
 * 审核信息页面
 */
class OrderValidFragment() : BaseFragment<FragmentOrderValidBinding, BaseViewModel>() {

    /**
     * 订单详情
     */
    var orderDetail: OrderDetail? = null

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderValidFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderValidFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int = R.layout.fragment_order_valid

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        mBinding.detail = orderDetail
        if (orderDetail != null) {

            if (orderDetail!!.recordList.isNullOrEmpty() || orderDetail!!.recordList?.get(0)!!.remark.isNullOrEmpty()) {
                mBinding.bookingVenue.content = "无"
            } else {
                mBinding.bookingVenue.content = orderDetail!!.recordList?.get(0)!!.remark
            }
        }
    }

    override fun initData() {

    }

}

/**
 * 待消费的二维码和消费码
 */
class OrderQrCodeFragment() : BaseFragment<FragmentOrderQrCodeBinding, BaseViewModel>() {
    /**
     * 订单详情
     */
    var orderDetail: OrderDetail? = null


    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderQrCodeFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderQrCodeFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }


    override fun getLayout(): Int = R.layout.fragment_order_qr_code

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        mBinding.detail = orderDetail
        if (orderDetail != null) {
            when (orderDetail!!.orderStatus) {
                // 待消费
                OrderStatusConstant.ORDER_STATUS_WAITE_COST -> {

                }
                // 已完成
                OrderStatusConstant.ORDER_STATUS_FINISHED -> {
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.status.image = activity!!.getDrawable(R.mipmap.order_detail_completed)
                    mBinding.tvConsuCode.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    mBinding.tvConsuCode.paint.isAntiAlias = true
                    mBinding.tvConsuCode.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    mBinding.tvConsuCode.paint.isAntiAlias = true
                }
                OrderStatusConstant.ORDER_STATUS_NO_EFFEFECT -> {
                    //已失效
                    mBinding.tvConsuCode.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    mBinding.tvConsuCode.paint.isAntiAlias = true
                    mBinding.tvConsuCode.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    mBinding.tvConsuCode.paint.isAntiAlias = true
                    mBinding.status.visibility = View.VISIBLE
                    mBinding.status.image = activity!!.getDrawable(R.mipmap.order_detail_expired)
                    mBinding.image.isClickable = false
                }
                else -> {
                    mBinding.tvConsuCode.visibility = View.GONE
                    mBinding.status.image = activity!!.getDrawable(R.mipmap.order_detail_expired)
                }
            }
            mBinding.tvNumbers.text = getString(R.string.order_numbers, orderDetail!!.orderNum)
            if (orderDetail!!.consumeCode.isNullOrEmpty()) {
                mBinding.tvConsuCode.visibility = View.GONE
            } else {
                mBinding.tvConsuCode.text =
                    getString(R.string.order_elect_code_str, orderDetail!!.consumeCode)
            }
            Glide.with(context!!)
                .load(orderDetail!!.orderQrCode)
                .placeholder(R.drawable.placeholder_img_fail_240_180)
                .into(mBinding.image)

//                       mBinding.imgageZoom = object : ReplyCommand {
//                override fun run() {
//                    imageZoom(activity!!, orderDetail!!.orderQrCode)
//                }
//            }

            mBinding.longClick = object : ReplyCommand {
                override fun run() {
                    if (orderDetail?.orderQrCode != null && orderDetail?.orderQrCode!!.contains("http")) {
                        try {
                            showLoadingDialog()
                            DownLoadFileUtil.downNetworkImage(
                                orderDetail!!.orderQrCode,
                                object : DownLoadFileUtil.DownImageListener {
                                    override fun onDownLoadImageSuccess() {
                                        dissMissLoadingDialog()
                                        ToastUtils.showMessage("保存二维码成功~")
                                    }

                                })
                        } catch (e: Exception) {
                            dissMissLoadingDialog()
                            ToastUtils.showMessage("保存二维码失败~")
                        }
                    }

                }
            }
        }

    }

    override fun initData() {

    }

    /**
     * 放大图片
     */
    fun imageZoom(context: Activity, url: String) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogImageBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_image, null, false
        )

        window!!.setContentView(binding.root)
        binding.url = url

        binding.image.setOnClickListener { dialog.dismiss() }

    }

}

/**
 * 取消信息
 */
class OrderCancelInformationFragment() : BaseFragment<FragmentOrderCancelBinding, BaseViewModel>() {
    /**
     * 订单详情
     */
    var orderDetail: OrderDetail? = null

    val adapter by lazy {
        MineCanceAppointAdapter().apply {
            emptyViewShow = false
        }
    }

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: OrderDetail): OrderCancelInformationFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderCancelInformationFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int = R.layout.fragment_order_cancel

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {
        orderDetail = arguments?.getParcelable(ORDER_DETAIL)
        if (orderDetail != null) {
            mBinding.detail = orderDetail

//            if (orderDetail!!.hasAttached != 1) {
//                mBinding.vOrderCanceInfo.visibility = View.VISIBLE
//                mBinding.rvOrderCances.visibility = View.GONE
//                // 展示付费方式
//                if (StringUtils.isZero(orderDetail!!.payMoney) && StringUtils.isZero(orderDetail!!.payIntegral)) {
//                    mBinding.tvBackMoney.visibility = View.GONE
//                } else if (!StringUtils.isZero(orderDetail!!.payMoney) && StringUtils.isZero(
//                        orderDetail!!.payIntegral
//                    )
//                ) {
//                    mBinding.tvBackMoney.content =
//                        getString(R.string.order_yuan) + orderDetail!!.payMoney
//                } else if (StringUtils.isZero(orderDetail!!.payMoney) && StringUtils.isZero(
//                        orderDetail!!.payIntegral
//                    )
//                ) {
//                    mBinding.tvBackMoney.content =
//                        orderDetail!!.payIntegral + getString(R.string.order_integral)
//                } else {
//                    mBinding.tvBackMoney.content =
//                        (orderDetail!!.payMoney?.toInt()!! + orderDetail!!.payIntegral?.toInt()!!).toString() + getString(
//                            R.string.order_integral
//                        )
//                }
//            } else {
            mBinding.vOrderCanceInfo.visibility = View.GONE
            mBinding.rvOrderCances.visibility = View.VISIBLE
            mBinding.rvOrderCances.adapter = adapter
            mBinding.rvOrderCances.layoutManager =
                FullyLinearLayoutManager(context, FullyLinearLayoutManager.VERTICAL, false)
            if (orderDetail!!.recordList.isNullOrEmpty()) {
                mBinding.root.visibility = View.GONE
                mBinding.rvOrderCances.visibility = View.GONE
            } else {
                mBinding.root.visibility = View.VISIBLE
                try {
                    var temp: MutableList<Record> = mutableListOf()
                    for (item in orderDetail!!.recordList!!) {
                        if (!item.type.isNullOrEmpty()) {
                            // 过滤取消操作
                            if (item.type == "CANCEL") {
                                temp.add(item)
                            }
                        } else {
                            temp.add(item)
                        }
                    }
                    adapter.clear()
                    adapter.add(temp)
                    mBinding.rvOrderCances.visibility = View.VISIBLE
                } catch (e: java.lang.Exception) {
                }

//                }
            }
        }


    }

    override fun initData() {

    }


}

class OrderHealthInfomationFragment : BaseFragment<FragOrderHealthInfoBinding, BaseViewModel>() {
    var healthInfo: HelathInfoBean? = null
    var healthSeting: HelathSetingBean? = null

    companion object {
        const val ORDER_DETAIL = "HealthDetail"
        const val HEALTH_SETING = "HealthSeting"
        fun newInstance(dr: HelathInfoBean, set: HelathSetingBean): OrderHealthInfomationFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            bundle.putParcelable(HEALTH_SETING, set)
            val orderDetailFragment = OrderHealthInfomationFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_order_health_info
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        healthInfo = arguments?.getParcelable(ORDER_DETAIL)
        healthSeting = arguments?.getParcelable(HEALTH_SETING)
        healthSeting?.let {
            if (it.enableHealthyCode) {
                mBinding.llvHealthCodeInfo.visibility = View.VISIBLE
            } else {
                mBinding.llvHealthCodeInfo.visibility = View.GONE
            }
            if (it.enableTravelCode) {
                mBinding.llvTravelCodeInfo.visibility = View.VISIBLE
                mBinding.tvTravelCodeName.text = "${it.smartTravelName}状况"
            } else {
                mBinding.llvHealthCodeInfo.visibility = View.GONE
            }
        }
        healthInfo?.let {
            if (it.healthCode.isNullOrEmpty()) {
                mBinding.tvLabelAddress.visibility = View.GONE
                mBinding.registAddres.visibility = View.GONE
                mBinding.healthState.text = "未注册"
                mBinding.healthState.setTextColor(resources.getColor(R.color.color_999))
                var drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_unkn)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                mBinding.healthState.setCompoundDrawables(drawable, null, null, null)
            } else {
                mBinding.registAddres.text = "" + it.regionName
                var drawable: Drawable
                when (it.healthCode) {
                    "01" -> {
                        mBinding.healthState.text = "低风险"
                        mBinding.healthState.setTextColor(resources.getColor(R.color.c_36cd64))
                        drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_normal)
                    }
                    "11" -> {
                        mBinding.healthState.text = "中风险"
                        mBinding.healthState.setTextColor(resources.getColor(R.color.color_ff9e05))
                        drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_warn)
                    }
                    "31" -> {
                        mBinding.healthState.text = "高风险"
                        mBinding.healthState.setTextColor(resources.getColor(R.color.ff4e4e))
                        drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_danger)
                    }
                    else -> {
                        mBinding.healthState.text = "未知"
                        mBinding.healthState.setTextColor(resources.getColor(R.color.color_999))
                        drawable = ResourceUtils.getDrawable(context!!, R.mipmap.icon_health_unkn)
                    }
                }
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                mBinding.healthState.setCompoundDrawables(drawable, null, null, null)
            }
            // 旅游码状态
            var trvalDrawable: Drawable
            if (it.smartTravelRegisterStatus) {
                mBinding.travelCodeState.text = "已注册"
                mBinding.travelCodeState.setTextColor(resources.getColor(R.color.c_36cd64))
                trvalDrawable =
                    ResourceUtils.getDrawable(context!!, R.mipmap.venue_book_condition_icon_low)
            } else {
                mBinding.travelCodeState.setTextColor(resources.getColor(R.color.color_999))
                mBinding.travelCodeState.text = "未注册"
                trvalDrawable =
                    ResourceUtils.getDrawable(context!!, R.mipmap.venue_book_condition_icon_unknown)
            }
            trvalDrawable.setBounds(0, 0, trvalDrawable.minimumWidth, trvalDrawable.minimumHeight)
            mBinding.travelCodeState.setCompoundDrawables(trvalDrawable, null, null, null)
            mBinding.travelCodeState.compoundDrawablePadding =
                resources.getDimension(R.dimen.dp_5).toInt()
        }

    }

    override fun initData() {
    }

}

class OrderHeXiaoFragment : BaseFragment<FragOrderHexiaoBinding, BaseViewModel>() {
    var recorder: Record? = null

    companion object {
        const val ORDER_DETAIL = "OrderDetail"
        fun newInstance(dr: Record): OrderHeXiaoFragment {
            val bundle = Bundle()
            bundle.putParcelable(ORDER_DETAIL, dr)
            val orderDetailFragment = OrderHeXiaoFragment()
            orderDetailFragment.arguments = bundle
            return orderDetailFragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_order_hexiao
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        recorder = arguments?.getParcelable(ORDER_DETAIL)
        if (recorder != null) {
            mBinding.record = recorder
        }
    }

    override fun initData() {
    }

}