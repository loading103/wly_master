package com.daqsoft.provider.view

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * @author luoyi
 * @date 2020/3/31 19点40分
 * @version 1.0.0
 */
class ScenicPageTransformer : ViewPager.PageTransformer {
    private val MIN_SCALE = 1f

    override fun transformPage(view: View, position: Float) {
        var position = position
        if (position < -1) {
            position = -1f
        } else if (position > 1) {
            position = 1f
        }
        val tempScale = if (position < 0) 1 + position else 1 - position // [0,1]
        val scaleValue = MIN_SCALE
        view.scaleX = scaleValue
        view.scaleY = scaleValue
    }
}