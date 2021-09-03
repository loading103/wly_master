package com.daqsoft.provider.view.scrollview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R

/**
 * @Description
 * @ClassName   MaxRecylerView
 * @Author      luoyi
 * @Time        2020/11/23 14:29
 */
class MaxRecylerView : RecyclerView {
    private var mMaxHeight = 0

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.MaxRecyclerView)
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxRecyclerView_maxHeight, mMaxHeight)
        arr.recycle()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val height = measuredHeight
        if (mMaxHeight in 1 until height) {
            setMeasuredDimension(widthSpec, mMaxHeight)
        }
    }
}