package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :住名宿实体类
 * @author 江云仙
 * @date 2020/4/13 15:00
 */
data class ApiHoteltBean(
    var regionName:String,
    var video:String,
    var type:List<String>,
    var images:String,
//    var scenic:String,
    var recommendHomePage:String,
    var likeNum:String,
    var audioTime:String,
    var srs:String,
    var goodCommentNum:String,
    var name:String,
    var videoCover:String,
    var hotelLevel:String,
    var id:String,
    var audio:String,
    var panoramaUrl:String,
    var region:String,
    var address:String,
    var floorPrice:String,
    var longitude:Double,
    var latitude:Double,
    var vipResourceStatus:VipResourceStatus
)
