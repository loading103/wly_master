package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/5 11:39
 *@author: caihj
 *@des:志愿者团队签到列表
 **/
data class VolunteerTeamSignInBean(
        val id:String,
        val latitude:String,
        val level:Int,
        val longitude:String,
        val signInDateList:SignInDateList,
        val teamAddress:String,
        val teamIcon:String,
        val teamName:String,
        val teamRegion:String,
        val teamRegionName:String
)

data class SignInDateList(
        val friday:List<String>,
        val monday:List<String>,
        val saturday:List<String>,
        val sunday:List<String>,
        val thursday:List<String>,
        val tuesday:List<String>,
        val wednesday:List<String>
)