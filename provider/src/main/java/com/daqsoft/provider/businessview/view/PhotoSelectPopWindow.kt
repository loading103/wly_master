package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.PopPhotoSelectBinding
import java.lang.reflect.Field


/**
 * @Description
 * @ClassName   PhotoSelectPopWindow
 * @Author      luoyi
 * @Time        2020/8/6 14:40
 */
class PhotoSelectPopWindow : PopupWindow {
    private var context: Context? = null
    private var mBind: PopPhotoSelectBinding? = null
    var onPhotoSelectListener: OnPhotoSelectListener? = null

    constructor(context: Context) : super(context) {
        this.context = context
        mBind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_photo_select,
            null,
            false
        )
        initPopWindow()
        initView()
    }

    private fun initView() {
        mBind?.tvSelectPhoto?.onNoDoubleClick {
            // 跳转相册选择
            dismiss()
            onPhotoSelectListener?.onSelectPhoto()
        }
        mBind?.tvTakePhoto?.onNoDoubleClick {
            dismiss()
            // 拍摄照片
            onPhotoSelectListener?.onTakePhoto()
        }
        mBind?.tvCancePopWindow?.onNoDoubleClick {
            dismiss()
        }
        mBind?.root?.onNoDoubleClick {
            dismiss()
        }
    }

    private fun initPopWindow() {
        mBind?.let {
            contentView = it.root
        }
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            isClippingEnabled = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val mLayoutInScreen: Field =
                    PopupWindow::class.java.getDeclaredField("mLayoutInScreen")
                mLayoutInScreen.isAccessible = true
                mLayoutInScreen.set(this, true)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    interface OnPhotoSelectListener {
        fun onTakePhoto()
        fun onSelectPhoto()
    }
}