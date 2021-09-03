package com.daqsoft.travelCultureModule.resource.view

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
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.PopWindowRouterAppointmentBinding
import com.daqsoft.mainmodule.databinding.PopWindowScenicAppointmentBinding
import com.daqsoft.provider.bean.RouterReservationBean
import com.daqsoft.provider.bean.ScenicReservationBean
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.travelCultureModule.hotel.view.CustomNumberPicker


/**
 * @Description 预约说明弹窗
 * @ClassName   AppointmentPopWindow
 * @Author      luoyi
 * @Time        2020/4/16 16:59
 */
class RouterPopWindow : PopupWindow {


    /**
     * 订票须知
     */
    private var routerReservationBean: RouterReservationBean? = null

    private var numPickView: CustomNumberPicker? = null

    private var mViewAppointment: View? = null

    private var binding: PopWindowRouterAppointmentBinding? = null
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_window_router_appointment,
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
    public fun updateData(routerReservationBean: RouterReservationBean) {
        this.routerReservationBean = routerReservationBean
        routerReservationBean?.let {
            binding?.scrollRouterPop?.smoothScrollTo(0, 0)
            binding?.txtRouterAppointmentName?.text = it.name
            if (!it.included.isNullOrEmpty()) {
                binding?.tvRouterAppointmentCashValue?.visibility = View.VISIBLE
                binding?.txtRouterAppointmentCash?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentCashValue?.text = it.included
            } else {
                binding?.tvRouterAppointmentCashValue?.visibility = View.GONE
                binding?.txtRouterAppointmentCash?.visibility = View.GONE
            }
            if (!it.exclude.isNullOrEmpty()) {
                binding?.tvRouterAppointmentOutValue?.visibility = View.VISIBLE
                binding?.txtRouterAppointmentOutCash?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentOutValue?.text = it.exclude
            } else {
                binding?.tvRouterAppointmentOutValue?.visibility = View.GONE
                binding?.txtRouterAppointmentOutCash?.visibility = View.GONE
            }
            if (!it.reservationNotes.isNullOrEmpty()) {
                binding?.txtRouterAppointmentInfo?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentInfoValue?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentInfoValue?.text = it.reservationNotes
            } else {
                binding?.txtRouterAppointmentInfo?.visibility = View.GONE
                binding?.tvRouterAppointmentInfoValue?.visibility = View.GONE
            }
            if (!it.considerations.isNullOrEmpty()) {
                binding?.txtRouterAppointmentNote?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentNoteValue?.visibility = View.VISIBLE
                binding?.tvRouterAppointmentNoteValue?.text = it.considerations
            } else {
                binding?.txtRouterAppointmentNote?.visibility = View.GONE
                binding?.tvRouterAppointmentNoteValue?.visibility = View.GONE
            }

        }

    }


    private fun initView() {
        binding?.root?.onNoDoubleClick {
            dismiss()
        }
    }

}