package com.daqsoft.servicemodule.bean

/**
 * desc :机票列表实体类
 * @author 江云仙
 * @date 2020/4/9 14:44
 */
data class PlaneListBean(
    var cityName:String,
    var endcityName:String,
    var list:MutableList<PlaneLists>
)
data class PlaneLists(
    var airline:String,
    var flightno:String,
    var craft:String,
    var arrivaltime:String,
    var departtime:String,
    var minprice:String
)