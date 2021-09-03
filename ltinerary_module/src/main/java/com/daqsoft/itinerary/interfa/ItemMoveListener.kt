package com.daqsoft.itinerary.interfa

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 15:58
 * @Description：
 */
interface ItemMoveListener {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

//    fun onMoveChanged(fromPosition: Int, toPosition: Int)
}