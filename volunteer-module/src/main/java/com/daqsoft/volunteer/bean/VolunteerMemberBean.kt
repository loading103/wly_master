package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/8 17:43
 *@author: caihj
 *@des:成员实体类
 **/
data class VolunteerMemberBean(
    val cumulativeIntegral:Int,// 总积分
    val head:String,
    val id:String,
    val level:Int,
    val manage:Boolean,
    val me:Boolean,
    val name:String,
    val phone:String,
    val pinyinName:String,
    val serviceNum:Int,
    val serviceRegionName:String,
    val serviceTime:Int,
    val sex:String
)