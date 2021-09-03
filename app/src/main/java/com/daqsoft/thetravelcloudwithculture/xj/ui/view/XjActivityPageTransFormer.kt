package com.daqsoft.thetravelcloudwithculture.xj.ui.view

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * @Description
 * @ClassName   XjActivityPageTransFormer
 * @Author      luoyi
 * @Time        2020/6/3 19:16
 */
class XjActivityPageTransFormer : ViewPager.PageTransformer {
    private val MIN_SCALE = 1f
    override fun transformPage(page: View, position: Float) {


        var position = position
        if (position < -1) {
            position = -1f
        } else if (position > 1) {
            position = 1f
        }
        val tempScale = if (position < 0) 1 + position else 1 - position // [0,1]
        val scaleValue = MIN_SCALE
       page.scaleX = scaleValue
        page.scaleY = scaleValue
    }
}