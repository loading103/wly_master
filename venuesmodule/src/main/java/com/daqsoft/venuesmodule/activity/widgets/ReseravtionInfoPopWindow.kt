package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.RouterReservationBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.PopReseravionInfoBinding


/**
 * @Description 场馆预约弹窗
 * @ClassName   ReseravtionInfoPopWindow
 * @Author      luoyi
 * @Time        2020/5/15 9:59
 */
class ReseravtionInfoPopWindow : PopupWindow {


    /**
     * 预订须知
     */
    private var mViewAppointment: View? = null

    private var binding: PopReseravionInfoBinding? = null
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_reseravion_info,
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
    public fun updateData(reseravionInfo:String) {
         binding?.tvReseravionInfo?.text =""+reseravionInfo
    }



private fun initView() {
    binding?.root?.onNoDoubleClick {
        dismiss()
    }
}

}