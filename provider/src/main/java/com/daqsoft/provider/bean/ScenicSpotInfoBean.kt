package com.daqsoft.provider.bean

/**
 * 景观点详情
 */
data class ScenicSpotInfoBean(
    var address: String,
    var audio: String,
    var audioInfo: List<AudioInfo>,
    var audioTime: String,
    var briefing: String,
    var id: Int,
    var images: String,
    var introduce: String,
    var latitude: Double,
    var longitude: Double,
    var name: String,
    var openTimeEnd: String,
    var openTimeStart: String,
    var panoramaUrl: String,
    var phone: String,
    var remarks: String,
    var resourceCode: Any,
    var ruralId: Int,
    var suggestedTime: String,
    var ticketPolicy: String
)

data class AudioInfo(
    val audio: String,
    val time: String
)