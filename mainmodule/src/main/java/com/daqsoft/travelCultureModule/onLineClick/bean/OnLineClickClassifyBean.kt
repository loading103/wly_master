package com.daqsoft.travelCultureModule.onLineClick.bean

/**
 * desc :网红打卡实体类
 * @author 江云仙
 * @date 2020/4/20 13:53
 */
data class OnLineClickClassifyBean (var subset:MutableList<Subset>)
data class Subset( var backgroundImg:String,
                   var name:String,
                   var summary:String,
                   var channelCode:String)