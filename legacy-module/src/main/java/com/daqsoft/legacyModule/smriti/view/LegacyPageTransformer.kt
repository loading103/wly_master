package com.daqsoft.legacyModule.smriti.view

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/30 10:14
 */
class LegacyPageTransformer : ViewPager.PageTransformer {
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