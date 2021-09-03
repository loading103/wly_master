package com.daqsoft.itinerary.bean

/**自定义目的地推荐*/
data class CunsomScenicBean(
    val regionName: String,
    var playedDay: Int,
    val list: MutableList<RecommScenicItem>
)

data class RecommScenicItem(
    val audio: String,
    val audioTime: String,
    val briefing: String,
    val chargeStatus: Int,
    val id: Int,
    val images: String,
    val introduce: String,
    val latitude: Double,
    val level: String,
    val link: String,
    val longitude: Double,
    val name: String,
    val openTimeEnd: String,
    val openTimeStart: String,
    val orderStatus: Int,
    val panoramaUrl: String,
    val recommendHomePage: Int,
    val region: String,
    val remarks: String,
    val resourceCode: String,
    val scenicUrl: String,
    val srs: Int,
    val suggestedHour: Int,
    val summary: String,
    val theme: List<String>,
    val turl: String,
    val video: String,
    var isChecked: Boolean
)