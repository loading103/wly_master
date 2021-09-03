package com.daqsoft.servicemodule.uitils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.baselib.base.BaseApplication

/**
 * desc :drawable位置工具类
 * @author 江云仙
 * @date 2020/4/3 9:56
 * @version 1.0.0
 * @since JDK 1.8
 */
object DrawableUtil {
    /**
    *设置控件左边添加UI图
    */
    fun rightDrawable(rightDrawable: Drawable,rightView:TextView){
        rightDrawable.setBounds(0, 0, rightDrawable.minimumWidth, rightDrawable.minimumHeight) // left, top, right, bottom
        rightView.setCompoundDrawables(null, null, rightDrawable, null);  // left, top, right, bottom
    }
    /**
    *设置控件左边添加UI图
    */
    fun leftDrawable(leftDrawable: Drawable,leftView:TextView){
        leftDrawable.setBounds(0, 0, leftDrawable.minimumWidth, leftDrawable.minimumHeight) // left, top, right, bottom
        leftView.setCompoundDrawables(leftDrawable, null, null, null);  // left, top, right, bottom
    }

}