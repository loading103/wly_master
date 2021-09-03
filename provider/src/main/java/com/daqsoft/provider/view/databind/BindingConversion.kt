package com.daqsoft.provider.view.databind

import android.view.View
import androidx.databinding.BindingConversion

/**
 * @Description databind 转换器
 * @ClassName   BindingConversion
 * @Author      luoyi
 * @Time        2020/7/23 16:56
 */
object BindingConversion {


    /**
     * 转换boolean 为 视图是否可见
     */
    @BindingConversion
    @JvmStatic
    fun convertBooleanToVisibility(visible: Boolean): Int {
        return if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
