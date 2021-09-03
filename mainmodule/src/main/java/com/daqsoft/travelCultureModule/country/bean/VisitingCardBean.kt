package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :乡村游头部信息实体类
 * @author 江云仙
 * @date 2020/4/17 17:02
 */
data class VisitingCardBean(
    var coverImage: String,
    var enableCityCard: Int,
    var enableCoverAudit: Int,
    var english: String,
    var id: Int,
    var images: List<String>,
    var introduce: String,
    var jumpUrl: String,
    var logo: String,
    var name: String,
    var panoramaUrl: String,
    var region: String,
    var regionName: String,
    var regionNameStr: String,
    var siteId: Int,
    var suggestedDay: Int,
    var summary: String,
    var video: String,
    var videoCover: String,
    var videoCoverEx: String,
    var videoEx: String
)