package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ScenicHealthBean
 * @Author      luoyi
 * @Time        2020/9/8 11:02
 */
data class ScenicHealthBean(
    /**
     * 指数文字评价
     */
    var ysdTxt: String?,
    /**
     * 景区序号
     */
    var areaId: String?,
    /**
     * 指数更新时间
     */
    var updateTime: String?,
    /**
     * 指数颜色
     */
    var ysdColor: String?,
    /**
     * 宜养指数
     */
    var ysdValue: String?,
    /**
     * 指数名称
     */
    var ysdName: String?
)

/**
 * 景区舒适度实体
 */
data class ScenicComfortBean(
    var total: String?,
    /**
     * 景区名称
     */
    var scenicName: String?,
    /**
     * 资源编码
     */
    var resourcecode: String?,
    /**
     * 进入人数
     */
    var realpeopleInto: String?,
    /**
     * 最大承载量
     */
    var frontmax: String?,
    /**
     * 实时人数
     */
    var rtnumber: String?,
    /**
     * 时间
     */
    var time: String?,
    /**
     * 地区编码
     */
    var region: String?,
    /**
     * 景区id
     */
    var sceneryid: String?

)