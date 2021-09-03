package com.daqsoft.itinerary.bean

data class ItineraryBean(
    val coverImages: String,
    val createName: String,
    val createTime: String,
    val diningCount: Int,
    val headUrl: String,
    val hotelCount: Int,
    val id: Int,
    val name: String,
    val processDay: Int,
    val processEnd: String,
    val processStart: String,
    val regionCount: Int,
    val scenicCount: Int,
    val siteId: Int,
    val sourceType: String,
    val userId: Int,
    val venueCount: Int,
    val fitTagsNames: String,
    val personalTagsNames: String,
    val datas: MutableList<ItineraryBean>
)