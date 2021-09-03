package com.daqsoft.travelCultureModule.story.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 *@package:com.daqsoft.travelCultureModule.story.view
 *@date:2020/3/31 11:02
 *@author: caihj
 *@des:阻止recylerView垂直滑动
 **/
class StopVercialRecylerView:RecyclerView{
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    override fun canScrollVertically(direction: Int): Boolean {
        return false
    }
}