package com.daqsoft.thetravelcloudwithculture.xj.ui.view

import android.os.Build
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 *@package:com.daqsoft.thetravelcloudwithculture.xj.ui.view
 *@date:2020/4/18 11:50
 *@author: caihj
 *@des:城市名牌效果
 **/
class GralleryTransformer : ViewPager.PageTransformer {

    private var mOffset: Float = 0F;
    private var SCALE: Float = 1f;
    private var ALPH: Float = 1f;
    private var offscreenPageLimit: Int = 3

    constructor(offscreenPageLimit: Int, mOffset: Float) {
        this.mOffset = mOffset;
        this.offscreenPageLimit = offscreenPageLimit;
    }

    override fun transformPage(page: View, position: Float) {
        move(page, position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            page.setTranslationZ(-Math.abs(position));
        }
    }

    private var pagerWidth: Int = 0;
    private var horizontalOffsetBase: Float = 0.0f;
    fun move(page: View, position: Float) {
        if (pagerWidth == 0)
            pagerWidth = page.getWidth();//vp width
        if (horizontalOffsetBase == 0f)
            horizontalOffsetBase = (pagerWidth - pagerWidth * SCALE) / 2 / offscreenPageLimit + mOffset;
        page.setTranslationX((horizontalOffsetBase - pagerWidth) * position);
        //setScale
        var scaleFactor = Math.min(SCALE - Math.abs(position) * 0.2f, SCALE);
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);
        var alpa = Math.min(ALPH - Math.abs(position) * 0.5f, ALPH)
        if(position<-1||position>1){
            page.alpha = 0f
        }else{
            page.alpha = alpa
        }
    }


}