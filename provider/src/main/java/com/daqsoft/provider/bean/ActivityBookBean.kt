package com.daqsoft.provider.bean

data class ActivityBookBean(
    val activity: ActivityBean,
    val activityRoom: ActivityRoom,
    val backIntegral: Int,
    val backMoney: Double,
    val backNum: Int,
    val commentStatus: Int,
    val createTime: String,
    val creditOrderType: Int,
    val endTime: String,
    val id: Int,
    val idCard: String,
    val isGuideOrder: Int,
    val orderCode: String,
    val orderNum: Int,
    val orderType: String,
    val payIntegral: Int,
    val payMoney: String,
    val startTime: String,
    val status: Int,
    val venueInfo: VenueInfo
)

