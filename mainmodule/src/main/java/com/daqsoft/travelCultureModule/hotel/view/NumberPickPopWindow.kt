package com.daqsoft.travelCultureModule.hotel.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.PopupWindow
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R

/**
 * @Description
 * @ClassName   NumberPickPopWindow
 * @Author      luoyi
 * @Time        2020/4/9 17:22
 */
class NumberPickPopWindow : PopupWindow {


    private var numPickView: CustomNumberPicker? = null

    private var mViewNumPick: View? = null
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
        mViewNumPick = LayoutInflater.from(context).inflate(R.layout.popwindow_number_picker, null)
        numPickView = mViewNumPick!!.findViewById(R.id.numpicker_pop)
        initView()
        initPopWindow()
    }

    private fun initView() {
        numPickView?.maxValue = 5
        numPickView?.minValue = 1
        numPickView?.value = 3
        var pickerFields = NumberPicker::javaClass.javaClass.declaredFields
        numPickView?.wrapSelectorWheel = false
        numPickView?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        numPickView?.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
                ToastUtils.showMessage("" + newVal)
            }
        })
        numPickView?.setNumberPicker(numPickView)
    }

    private fun initPopWindow() {
        contentView = mViewNumPick
        this.width = ViewGroup.LayoutParams.MATCH_PARENT;
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

}