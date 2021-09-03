package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/8 15:40
 *@author: caihj
 *@des:志愿品牌实体类
 **/
data class VolunteerBrandBean(
    val activityNum:Int,
    val declaration:String,// 活动宣言
    val id:Int,
    val image:String,
    val name:String
)