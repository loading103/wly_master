package com.daqsoft.provider.bean

import com.daqsoft.provider.network.venues.bean.AudioInfo


/**
 * 美食实体
 *
 *
 */
data class PlayGroundBean(
    val activity: List<ActivityBean>,
    val address: String,
    val applyTag: String,
    val applyTagName: List<String>,
    val audio: String,
    val audioTime: String,
    val consumPerson: String,
    val contentDataList: List<Any>,
    val entEqtTag: String,
    val entEqtTagName: List<String>,
    val feature: String,
    val featureName: List<String>,
    val id: Int,
    val images: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val officialUrl: String,
    val openEndTime: String,
    val openStartTime: String,
    val panoramaUrl: String,
    val phone: String,
    val recommendHomePage: Int,
    val region: String,
    val regionName: String,
    val srs: Int,
    val type: String,
    val typeName: String,
    val video: String,
    val vipResourceStatus: VipResourceStatus,
    val websiteUrl: String
)



data class PlayGroundDetailBean(
    val activity: List<ActivityBean>,
    val applyTag: List<String>,
    val area: String,
    val audio: String,
    val audioInfo: List<AudioInfo>,
    val audioTime: String,
    val briefing: String,
    val capacity: String,
    var collectionNum: String,
    val commentNum: String,
    val consumPerson: String,
    val cutRegionName: String,
    val decorationTime: String,
    val entEqtTag: List<String>,
    val feature: List<String>,
    val id: String,
    val images: String,
    val introduce: String,
    val latitude: Double,
    var likeNum: String,
    val longitude: Double,
    val name: String,
    val officialName: String,
    val officialUrl: String,
    val openEndTime: String,
    val openStartTime: String,
    val openYear: String,
    val panoramaCover: String,
    val panoramaUrl: String,
    val phone: String,
    val region: String,
    val regionName: String,
    val resourceCode: String,
    val trafficInfo: String,
    val type: String,
    val video: String,
    val videoCover: String,
    val vipResourceStatus: VipResourceStatus,
    val websiteUrl: String,
    val shopUrl: String,
    val shopName: String,
    val summary: String,
    val sysCode: String
)
