package com.daqsoft.legacyModule.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 *@package:com.daqsoft.legacyModule.view
 *@date:2020/5/11 14:51
 *@author: caihj
 *@des:TODO
 **/
class WrapContentHeightViewPager: ViewPager {

    constructor(context: Context):super(context)

     constructor(context: Context,attrs:AttributeSet):super(context,attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        //下面遍历所有child的高度
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h: Int = child.measuredHeight
            if (h > height) //采用最大的view的高度。
                height = h
        }

        var heightMeasureSpecs = MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpecs)
    }
}