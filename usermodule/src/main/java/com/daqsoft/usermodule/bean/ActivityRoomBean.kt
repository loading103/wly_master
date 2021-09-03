package com.daqsoft.usermodule.bean

/**
 *@package:com.daqsoft.usermodule.bean
 *@date:2020/4/10 9:46
 *@author: caihj
 *@des:预约活动室
 **/
data class ActivityRoomBean(
    // 场馆名称
    val venueName:String,
    // 取消时间
    val cancelTime:String,
    // 取消状态
    val cancelStatus:String,
    // 诚信优享值
    val faithUseValue:String,
    // 诚信优享状态
    val faithUseStatus:String,
    // 诚信免审值
    val faithAuditValue:String,
    // 诚信免审状态
    val faithAuditStatus:String,
    val openStatus:String,
    val images:String,
    val galleryful:String,
    val area:String,
    val venueId:String,
    val name:String,
    val id:String
    )