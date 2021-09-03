package com.daqsoft.servicemodule.bean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/2 19:05
 * @version 1.0.0
 * @since JDK 1.8
 */
data class TravelAgencyBean(
    //旅行社地址
    var address:String,
    //纬度
    var latitude:String,
    //经度
    var longitude:String,
    //名称
    var name:String,
    //联系电话
    var contactNumber:String

)