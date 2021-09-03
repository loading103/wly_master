package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   OrderAttacthPerson
 * @Author      luoyi
 * @Time        2020/8/13 20:38
 */
data class OrderAttacthPersonBean(
    /**
     * 用户手机号
     */
    var userPhone: String?,
    /**
     * 用户名
     */
    var userName: String?,
    /**
     * 证件类型
     */
    var userCardType: String?,
    /**
     * 证件号
     */
    var userCardNumber: String?,
    /**
     * 订单状态
     */
    var orderStatus: Int,
    /**
     * 是否选中
     */
    var isSelect: Int,
    var isContainName:Boolean,
    /**
     * 是否为预约人 1 是 0 不是
     */
    var leader: Int,
    var id: Int,
    var healthInfo: OrderhealthInfoBean?
)

/**
 *  健康码订单
 */
data class OrderhealthInfoBean(
    /**
     * 旅游码状态 true(已注册)
     */
    var smartTravelRegisterStatus: Boolean,
    /**
     * 健康码注册地
     */
    var regionName: String?,
    /**
     * 健康状态
     */
    var healthCode: String?,
    var smartTravelName: String?,
    var enableTravelCode:Boolean,
    var enableHealthyCode:Boolean
)

data class OrderAttachPMapBean(
    /**
     * 已取消人数
     */
    var cancel: MutableList<OrderAttacthPersonBean>?,
    /**
     * 已失效人数
     */
    var lose: MutableList<OrderAttacthPersonBean>?,
    /**
     * 待使用人数
     */
    var wait: MutableList<OrderAttacthPersonBean>?,
    /**
     * 已使用
     */
    var end: MutableList<OrderAttacthPersonBean>?
)