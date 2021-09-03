package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/17 16:36
 *@author: caihj
 *@des:TODO
 **/
data class VolunteerTeamInfoBean(
    val attributionName:String,
    val establishTime:String,
    val manageUnit:String,
    val serviceRegionName:String,
    val serviceType:List<String>,
    val status:Int,
    val teamAddress:String,
    val teamIcon:String,
    val teamIntroduce:String,
    val teamItemBrandList:List<BrandInfo>,
    val teamName:String,
    val teamPhone:String,
    val teamRegionName:String
)

data class BrandInfo(
    val itemBrandName:String,
    val itemBrandId:Int
)