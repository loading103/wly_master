package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/1 15:52
 *@author: caihj
 *@des:关注团队实体类
 **/
data class VolunteerTeamBean(
    val type:String,// 类型 ACTIVITY_MODE_VOLUNT','ACTIVITY_MODE_VOLUNT_SERVICE','ACTIVITY_MODE_VOLUNT_SERVICE','ACTIVITY_MODE_VOLUNT_SERVICE','ACTIVITY_MODE_VOLUNT_SERVICE'
    val teamName:String,
    val teamId:String,
    val signStatus:Boolean,
    val images:String,
    val id:String,
    val createTime:String,
    val content:String,
    val teamLogo:String
)