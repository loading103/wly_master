package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/9 11:23
 *@author: caihj
 *@des:签到实体
 **/
data class VolunteerSignApplyBean(
    val currMonth:String,// 当前年月
    val dateInfo:DataInfo,// 日历信息
    val firstDayWeek:Int // 月份第一天星期几
)

data class DataInfo(
    val date:String,
    val index:Int,
    val openSignIn:Boolean
)