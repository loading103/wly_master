package com.daqsoft.itinerary.interfa

/**
 * @Author：      邓益千
 * @Create by：   2020/5/13 13:32
 * @Description：
 */
interface OnItemSelectedListener<T> {

    fun onSelected(position: Int,item: T)

}