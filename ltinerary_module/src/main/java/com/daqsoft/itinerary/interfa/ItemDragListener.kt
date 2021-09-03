package com.daqsoft.itinerary.interfa

import androidx.recyclerview.widget.RecyclerView

/**
 * @Author：      邓益千
 * @Create by：   2020/5/27 16:11
 * @Description：
 */
interface ItemDragListener {

    fun onStartDrags(viewHolder: RecyclerView.ViewHolder)

}