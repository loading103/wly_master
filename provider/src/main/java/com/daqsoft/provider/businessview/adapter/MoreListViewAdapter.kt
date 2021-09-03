package com.daqsoft.provider.businessview.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter

/**
 *@package:com.daqsoft.provider.businessview.adapter
 *@date:2020/5/14 14:20
 *@author: caihj
 *@des:TODO
 **/
abstract class MoreListViewAdapter<T : ViewDataBinding, D : Any>(resourceId: Int,data: MutableList<D>):
    RecyclerViewAdapter<T, D>(resourceId, data) {
    constructor(resourceId: Int) : this(resourceId, data = mutableListOf())

    var isNeedMore: Boolean = false

    /**
     * 至少显示条数
     */
    var maxCount:Int = 2

    override fun getItemCount(): Int {
        return if (isNeedMore) {
            // 需要判断
            if (getData().size > maxCount) {
                maxCount
            } else {
                getData().size
            }
        } else {
            getData().size
        }
    }
}