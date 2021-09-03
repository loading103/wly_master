package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ResourceOrderBean
 * @Author      luoyi
 * @Time        2020/5/26 11:52
 */
data class ResourceOrderBean(
    var resourceName: String,
    var orderTimes: MutableList<ResourceDateBean>
)

/**
 * @Description
 * @ClassName  ResourceDateBean
 * @Author      luoyi
 * @Time        2020/5/26 11:52
 */
data class ResourceDateBean(
    var date: String,
    /**
     * 订单数量
     */
    var orderNum: String,
    /**
     * 限流
     */
    var currDayMaxNum: Int,
    /**
     * 周末
     */
    var week: Int,
    /**
     * 库存
     */
    var stock: Int,
    /**
     * 二维码或者链接地址
     */
    var link: String,
    /**
     * 跳转类型
     */
    var jumpType: String
)