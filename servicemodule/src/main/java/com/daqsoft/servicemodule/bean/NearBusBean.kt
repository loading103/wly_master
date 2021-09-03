package com.daqsoft.servicemodule.bean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/3 13:45
 * @version 1.0.0
 * @since JDK 1.8
 */
data class NearBusBean(
    //区域名称
    var adname:String,
    //经纬度
    var location:String,
    //兴趣点类型
    var type:String,
    //名称
    var name:String,
    //poi所在省份名称
    var pname:String,
    //唯一ID
    var id:String,
    //离中心点距离（米）
    var distance:String,
    //地址
    var address:String,
    //城市名
    var cityname:String,
    //兴趣点类型编码
    var typecode:String
)