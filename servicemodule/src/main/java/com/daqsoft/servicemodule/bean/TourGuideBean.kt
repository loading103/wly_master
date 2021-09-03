package com.daqsoft.servicemodule.bean

/**
 * desc :导游列表实体类
 * @author 江云仙
 * @date 2020/4/2 9:53
 */
data class TourGuideBean(
    //语言
    var language: String,
    //所属公司
    var company: String,
    //领队证号
    var certificateNo: String,
    //导游等级
    var level: String,
    //性别
    var gender: String,
    //照片
    var photo: String,
    //性别
    var name: String
)