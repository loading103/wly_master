package com.daqsoft.guidemodule.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 *
 * @Description  MarqueeTextView
 * @ClassName   MarqueeTextView
 * @Author      Wongxd
 * @Time        2020/4/13 19:20
 */
internal class MarqueeTextView : AppCompatTextView {


    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //        自定义设置让focusable为true
    //        这个方法相当于在layout中
    //        android:focusable="true"
    //        android:focusableInTouchMode="true"
    override fun isFocused(): Boolean {
        return isMarquee
    }


    /**
     * 详情见TextView中setSelected方法
     * TextView中setSelected方法调该方法，返回false才有跑马灯效果
     * @return 返回false
     */
    override fun isSelected(): Boolean {
//        return super.isSelected()
        return false
    }


    private var isMarquee = true

    fun setMarquee(value: Boolean) {
        isMarquee = value
        postInvalidate()
    }


}