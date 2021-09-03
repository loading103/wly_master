package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/4 9:15
 *@author: caihj
 *@des:团队列表实体类
 **/
data class VolunteerTeamListBean(
    val id:String,
    val latitude:String,
    val level:String,
    val longitude:String,
    val openSignIn:Boolean,// 是否开启签到
    val teamAddress:String,
    val teamIcon:String,
    val teamMemberNum:Int,// 团队成员数
    val teamName:String,
    val teamRegion:String,
    val teamRegionName:String,
    val teamServiceNum:Int,
    val teamServiceTime:Int,
    val activityId:String,
    val activityName:String,
    val activityType:String
)