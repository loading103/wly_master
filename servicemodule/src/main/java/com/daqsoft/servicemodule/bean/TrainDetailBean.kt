package com.daqsoft.servicemodule.bean

data class TrainDetailBean(
    var list:MutableList<TrainDetailList>
)
data class TrainDetailList(
    var station:String,
    var arrivaltime:String,
    var departuretime:String,
    var stoptime:String
)