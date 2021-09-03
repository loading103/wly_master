package com.daqsoft.provider.bean

/**
 * 直播地址实体
 */
data class ScenicSpotsPanor(
    // 数据id
    val id: Int,
    // 图片
    val images: String,
    // 名称
    val name: String,
    // 景点信息
    val scenicSpots: List<ScenicSpot>,
    // 播放地址
    val url: String
)

/**
 * 景点信息
 */
data class ScenicSpot(
    // 数据id
    val id: Int,
    // 景点图片
    val images: String,
    // 名称
    val name: String,
    // 720播放地址
    val url: String
)