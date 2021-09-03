package com.daqsoft.itinerary.interfa

import com.daqsoft.itinerary.bean.ItineraryDetailBean
import com.daqsoft.itinerary.bean.ItineraryDetailBean.AgendaBean.PlansBean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/15 16:23
 * @Description： 附近吃，住，订票、增加，删除监听
 */
interface OnItemAssetsOperateListener {

    /**
     * 附近吃或住
     * @param type 吃或住
     */
    fun nearby(type:String,plan: PlansBean)

    /**订票*/
    fun nearbyBooking(plan: PlansBean)

    /**导航*/
    fun onNavi(start:PlansBean, end: PlansBean)

    /**更多交通方式*/
    fun onMoreTraffic(start:PlansBean, end: PlansBean)

    /**删除*/
    fun onDelete(plan: PlansBean)

    /**添加*/
    fun onAdd(plan: PlansBean)

    /**详情*/
    fun onDetail(plan: PlansBean)
}