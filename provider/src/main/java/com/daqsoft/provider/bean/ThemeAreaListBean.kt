package com.daqsoft.provider.bean

import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 * 主题区域列表
 */
data class ThemeAreaListBean(
    val id: Int?=null,
    val images: String?=null,
    val level: String?=null,
    val name: String?=null,
    val numAvg: String?=null,
    val type: String?=null
)


data class ThemeProjectDetailBean(
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
