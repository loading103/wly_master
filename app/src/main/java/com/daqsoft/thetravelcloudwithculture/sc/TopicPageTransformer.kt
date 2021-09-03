package com.daqsoft.thetravelcloudwithculture.sc

import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.home.view.gallery.BranchScalePageTransformer

/**
 *@package:com.daqsoft.thetravelcloudwithculture.sc
 *@date:2020/5/20 13:41
 *@author: caihj
 *@des:TODO
 **/
class TopicPageTransformer:ViewPager.PageTransformer {

    private val MAX_SCALE = 1.0f
    private val MIN_SCALE = 1.0f

    override fun transformPage(view: View, position: Float) {
        var pos = position
        val tempScale = if (pos < 0) 1 + pos else 1 - pos
        val slope =
            (MAX_SCALE - MIN_SCALE) / 1
        val scaleValue = MIN_SCALE + tempScale * slope

        val imageView: ImageView = view.findViewById(R.id.image)
        val pivotY = imageView.height.toFloat()
        imageView.pivotY = pivotY
        imageView.scaleY = scaleValue

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            view.parent.requestLayout()
        }
    }
}