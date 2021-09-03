package com.daqsoft.servicemodule.bean

/**
 * desc :城市列表实体类
 * @author 江云仙
 * @date 2020/4/8 14:47
 */
data class StationBean(
    //车站码
    var code:String,
    //名称
    var name:String,
    //数据id
    var id:Int?,
    var cityid:Int?,
    var cityname:String?,
    var citycode:String?
)