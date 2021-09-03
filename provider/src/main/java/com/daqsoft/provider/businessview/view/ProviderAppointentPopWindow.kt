package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ScenicReservationBean
import com.daqsoft.provider.databinding.PopProviderScenicAppointmentBinding
import com.daqsoft.provider.utils.HtmlUtils

/**
 * @Description
 * @ClassName   ProviderAppointentPopWindow
 * @Author      luoyi
 * @Time        2020/11/5 17:06
 */
class ProviderAppointentPopWindow : PopupWindow {


    /**
     * 订票须知
     */
    private var scenicReservation: ScenicReservationBean? = null


    private var mViewAppointment: View? = null

    private var binding: PopProviderScenicAppointmentBinding? = null
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_provider_scenic_appointment,
            null,
            false
        )
        mViewAppointment = binding!!.root
        initView()
        initPopWindow()
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);

        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /**
     * 更新数据
     */
    public fun updateData(scenicReservationBean: ScenicReservationBean) {
        this.scenicReservation = scenicReservationBean
        scenicReservationBean?.let {
            binding?.scrollScenicPop?.smoothScrollTo(0, 0)
            binding?.txtScenicAppointmentName?.text = scenicReservation?.productName
            // 处理标签
            var tagbuilder = getTags(it)
            if (tagbuilder.isNullOrEmpty()) {
                binding?.txtScenicAppointmentTags?.visibility = View.GONE
            } else {
                binding?.txtScenicAppointmentTags?.text = tagbuilder
                binding?.txtScenicAppointmentTags?.visibility = View.VISIBLE
            }
            // 预订说明
            // 退款设置
            if (!it.refundLimit.isNullOrEmpty()) {
                binding?.vScenicAppointmentSeting?.visibility = View.VISIBLE
                binding?.tvScenicAppointmentSetingValue?.text = it.refundLimit
            } else {
                binding?.vScenicAppointmentSeting?.visibility = View.GONE
            }
            // 订单取消
            if (!it.autoCancelTime.isNullOrEmpty()) {
                binding?.vTicketAppointmentCanceOder?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentCanceOderValue?.text = it.autoCancelTime
            } else {
                binding?.vTicketAppointmentCanceOder?.visibility = View.GONE
            }
            // 入园凭证
            if (!it.exchangeVoucherType.isNullOrEmpty()) {
                binding?.vTicketAppointmentRuyuanPz?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentRuyuanPzValue?.text = it.exchangeVoucherType
            }
            // 入园方式
            if (!it.passType.isNullOrEmpty()) {
                binding?.vTicketAppointmentRuyuanAction?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentRuyuanActionValue?.text = it.passType
            } else {
                binding?.vTicketAppointmentRuyuanAction?.visibility = View.GONE
            }
            // 入园人数
            if (!it.adultTicket.isNullOrEmpty()) {
                binding?.vTicketAppointmentRunyuanNum?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentRunyuanNumValue?.text = it.adultTicket
            } else {
                binding?.vTicketAppointmentRuyuanAction?.visibility = View.GONE
            }
            // 入园次数
            if (!it.checkTimes.isNullOrEmpty()) {
                binding?.vTicketAppointmentRuyuanCishu?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentRuyuanCishuValue?.text = it.checkTimes
            } else {
                binding?.vTicketAppointmentRuyuanCishu?.visibility = View.GONE
            }
            // 入园限制
            if (!it.enterSightDelayBookCheck.isNullOrEmpty()) {
                binding?.vTicketAppointmentRuyuanXz?.visibility = View.VISIBLE
                binding?.tvTicketAppointmentRuyuanXzValue?.text = it.enterSightDelayBookCheck
            } else {
                binding?.vTicketAppointmentRuyuanXz?.visibility = View.GONE
            }
            // 费用包含
            if (!it.costIncludes.isNullOrEmpty()) {
                binding?.txtTiketAppointmentCashValue?.visibility = View.VISIBLE
                binding?.txtTiketAppointmentCash?.visibility = View.VISIBLE
                binding?.txtTiketAppointmentCashValue?.text = HtmlUtils.html2Str(it.costIncludes)
            } else {
                binding?.txtTiketAppointmentCashValue?.visibility = View.GONE
                binding?.txtTiketAppointmentCash?.visibility = View.GONE
            }


        }

    }

    private fun getTags(it: ScenicReservationBean): StringBuilder {
        var tagBuilder = StringBuilder("")
        if (it.needFaceRecognition) {
            val needFaceRecog = "人脸识别入园"
            val foregroundColorSpan = ForegroundColorSpan(context!!.resources.getColor(R.color.c_16b2fa))
            val spannableString = SpannableString(needFaceRecog)
            spannableString.setSpan(foregroundColorSpan, 0, needFaceRecog.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tagBuilder.append(spannableString)
        }
        if (!it.purchaseLimit.isNullOrEmpty()) {
            if (!tagBuilder.isNullOrEmpty()) {
                tagBuilder.append(" | ")
            }
            val foregroundColorSpan = ForegroundColorSpan(context!!.resources.getColor(R.color.colorPrimary))
            val spannableString = SpannableString(it.purchaseLimit)
            spannableString.setSpan(foregroundColorSpan, 0, it.purchaseLimit.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tagBuilder.append(spannableString)
        }
        if (!it.allowRefund.isNullOrEmpty()) {
            if (!tagBuilder.isNullOrEmpty()) {
                tagBuilder.append(" | ")
            }
            val foregroundColorSpan = ForegroundColorSpan(context!!.resources.getColor(R.color.colorPrimary))
            val spannableString = SpannableString(it.allowRefund)
            spannableString.setSpan(foregroundColorSpan, 0, it.allowRefund.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tagBuilder.append(spannableString)
        }
        if (!it.needTicket.isNullOrEmpty()) {
            if (!tagBuilder.isNullOrEmpty()) {
                tagBuilder.append(" | ")
            }
            val foregroundColorSpan = ForegroundColorSpan(context!!.resources.getColor(R.color.colorPrimary))
            val spannableString = SpannableString(it.needTicket)
            spannableString.setSpan(foregroundColorSpan, 0, it.needTicket.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tagBuilder.append(spannableString)
        }
        return tagBuilder
    }

    private fun initView() {
        binding?.root?.onNoDoubleClick {
            dismiss()
        }
    }

}