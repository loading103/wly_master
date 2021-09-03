package com.daqsoft.legacyModule.bean

/**
 *@package:com.daqsoft.legacyModule.bean
 *@date:2020/5/14 9:32
 *@author: caihj
 *@des:非遗资讯分类
 **/
data class NewsCategoryBean(var subset:MutableList<Subset>)
data class Subset( var backgroundImg:String = "",
                   var name:String,
                   var summary:String = "",
                   var channelCode:String)