package com.daqsoft.travelCultureModule.contentActivity.bean

/**
 *@package:com.daqsoft.travelCultureModule.contentActivity.bean
 *@date:2020/4/21 19:04
 *@author: caihj
 *@des:看演出实体类
 **/
data class WatchShowBean(
    val backgroundImg:String,
    val name:String,
    val english:String,
    val logo:String,
    val id:String,
    val subset:List<ShowBean>
)

data class ShowBean(
    val id:String,
    val name:String,
    val pid:String,
    val url:String,
    val summary:String,
    val content:String,
    val logo:String,
    val backgroundImg: String,
    val status:String,
    val siteId:String,
    val createTime:String,
    val updateTime:String,
    val showChannel:Boolean,
    val channelCode:String
)