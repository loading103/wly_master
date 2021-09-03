package com.daqsoft.servicemodule.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * desc :根据地址获取经纬度实体类
 * @author 江云仙
 * @date 2020/4/3 15:42
 * @version 1.0.0
 * @since JDK 1.8
 */
@Parcelize
class BusAddressBean(
    //城市编码
    var citycode:String,
    //地址所在的区
    var district:String,
    //匹配级别
    var level:String,
    //坐标点	(经度、纬度)
    var location:String,
    //地址所在的省份名
    var province:String,
    //区域编码
    var adcode:String,
    //地址所在的城市名
    var city:String

    ): Parcelable