package com.daqsoft.provider.bean

import com.daqsoft.provider.network.venues.bean.AudioInfo

/**
 * 美食实体
 *
 *
 */
data class FoodBean(
    val activity: List<ActivityBean>,
    val audio: String,
    val audioTime: String,
    val consumPerson: String,
    val floorPrice: String,
    val id: Int,
    val images: String,
    val introduce: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val openTime: String,
    val panoramaUrl: String,
    val recommendHomePage: Int,
    val region: String,
    val regionName: String,
    val srs: Int,
    val summary: String,
    val video: String,
    val type: List<String>,
    val vipResourceStatus: VipResourceStatus
)

/**
 * 餐厅详情实体
 */
data class FoodDetailBean(
    //地区编码
    val region: String,
    // 名称
    val name: String,
    // 设备标签
    var eqtTag: Array<String>,
    // 电话
    val phone: String,
    // 	音频时间
    val audioTime: String,
    //  交通信息
    val trafficInfo: String,
    // 评论数量
    val commentNum: String,
    // 小电商店铺地址
    val shopUrl: String,
    // 购买地址
    val introduce: String,
    // 资源状态
    val vipResourceStatus: VipResourceStatus,
    // 图片
    val images: String,
    // 简介
    val summary: String,
    // 经度
    val longitude: Double,
    // 人均消费
    val consumPerson: String,
    // 720地址
    val panoramaUrl: String,
    val panoramaCover:String,
    //  开放时间
    val openTime: String,
    // 音频
    val audio: String,
    // 数据id
    val id: Int,
    //音频信息
    val audioInfo: ArrayList<AudioInfo>,
    // 房间数量
    val roomNum: String,
    // 资源编码
    val resourceCode: String,
    //点赞数量
    var likeNum: String,
    // 类型
    val type: List<String>,
//视频
    val video: String,
//购买地址名称
    val shopName: String,
//纬度
    val latitude: Double,
//地区编码
    val regionName: String,
//收藏数量
    var collectionNum: String,
//相关活动
    val activity: MutableList<ActivityBean>,
// 小电商店铺code
    val sysCode: String,
    val cutRegionName: String,
    val officialName: String,
    val officialUrl: String,
    val websiteUrl: String,
    val briefing: String

// 设备标签
){
    fun getadresses():String{
        if(regionName.isNullOrBlank()){
            return ""
        }else{
            return "商家地址： ${regionName}"
        }
    }
}
