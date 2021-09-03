package com.daqsoft.itinerary.bean

data class Traffbean(
    val endSourceId: Int,
    val endSourceName: String,
    val endSourceType: String,
    val startSourceId: Int,
    val startSourceName: String,
    val startSourceType: String,
    val trafficList: MutableList<TrafficItem>
)
