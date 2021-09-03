package com.daqsoft.volunteer.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.daqsoft.volunteer.R

/**
 *@package:com.daqsoft.volunteer.view
 *@date:2020/6/9 15:40
 *@author: caihj
 *@des:TODO
 **/
class TableView:ViewGroup {
    private var mContext: Context? = null
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        val typeAttr = context!!.obtainStyledAttributes(attrs!!, R.styleable.TitleMoreBar)
        typeAttr.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }
}