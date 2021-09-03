package com.daqsoft.itinerary.bean

/**
 * 附近 餐馆 or 酒店
 */
data class NearbyBean(
    val id: Int,
    var sourceId: Int,
    val images: String,
    val name: String,
    val money: Int,
    val openTime: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val consumPerson: String?,
    val activity: List<Any>
)