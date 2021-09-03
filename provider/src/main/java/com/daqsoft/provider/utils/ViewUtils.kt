package com.daqsoft.provider.utils

import android.view.View

/**
 * @Description
 * @ClassName   ViewUtils
 * @Author      luoyi
 * @Time        2020/11/9 16:57
 */
object ViewUtils {

    fun getVisible(flag: Boolean): Int {
        return if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}