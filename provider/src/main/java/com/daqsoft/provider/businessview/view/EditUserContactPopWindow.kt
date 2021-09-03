package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.PopWindowEditUserContactBinding

/**
 * @Description
 * @ClassName   EditUserContactPopWindow
 * @Author      luoyi
 * @Time        2020/8/4 10:27
 */
class EditUserContactPopWindow : PopupWindow {
    private var context: Context? = null
    private var mBind: PopWindowEditUserContactBinding? = null

    constructor(context: Context) : super(context) {
        this.context = context
        mBind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_window_edit_user_contact,
            null,
            false
        )
        initPopWindow()
        initView()
    }

    private fun initView() {

    }

    private fun initPopWindow() {
        mBind?.let {
            contentView = it.root
        }
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            isClippingEnabled = false
        }
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}