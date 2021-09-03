package com.daqsoft.servicemodule.bean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/9 20:49
 */
data class ArtFoundListBean(
    var subset:MutableList<Subsets>
)
data class Subsets(
    var name:String,
    var content:String,
    var url:String,
    var backgroundImg:String,
    var id:Int,
    var channelCode	:String

)