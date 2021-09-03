package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/5 10:38
 *@author: caihj
 *@des:个人志愿服务器记录实体类
 **/
data class VolunteerServiceRecordBean(
    val useStartTime:String,// 服务开始时间
    val useEndTime:String,// 服务结束时间
    val name:String,
    val images:String,
    val id:String,
    val address:String
)