package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/15 16:03
 */
data class CountryHapDetailBean(
    var id:String,
    var level:String,
    var resourceCode:String,
    var images:String,
    var video:String,
    var videoCover:String,
    var panoramaUrl:String,
    var name:String,
    var isOpen:String,
    var type:String,
    var regionName:String,
    var businessTime:String,
    var consumeAvg:String,
    var boxNum:String,
    var lon:String,
    var lat:String,
    var phone:String,
    var introduce:String,
    var commentNum:String,
    var startStatus:String,
    var address:String,
    var likeNum:String,
    var summary:String,
    var collectionNum:String,
    var sysCode:String,
//    var orderAddress:String,
    var vipResourceStatus:VipResourceStatus,
    /**
     * 720封面
     */
    var panoramaCover: String? = ""
)

class VipResourceStatus(
    /**
     * 收藏状态
     */
    var collectionStatus: Boolean,
    /**
     * 关注状态
     */
    val fansStatus: Boolean,
    /**
     * 点赞状态
     */
    var likeStatus: Boolean
)