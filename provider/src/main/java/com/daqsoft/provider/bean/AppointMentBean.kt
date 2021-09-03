package com.daqsoft.provider.bean

import java.math.BigDecimal

/**
 * @Description
 * @ClassName   AppointMentBean
 * @Author      luoyi
 * @Time        2020/5/27 9:55
 */
data class AppointMentBean(
    /**
     * 资源信息
     */
    var resource: Resource,
    /**
     *  详情URL
     */
    var infoUrl: String,
    /**
     * 取消终止时间，超过该时间不允许取消
     */
    var cancelTime: String,
    /**
     * 取消状态 0：不可取消, 1可取消
     */
    var cancelStatus: String,
    /**
     * 实付金额
     */
    var payMoney: BigDecimal,
    /**
     *  票价
     */
    var ticketPrice: BigDecimal,
    /**
     * 订单数量
     */
    var orderNum: Int,
    /**
     *  预定结束时间
     */
    var orderEndTime: String,
    /**
     * 预定开始时间
     */
    var orderStartTime: String,
    /**
     * 订单状态
     */
    var orderStatus: Int,
    /**
     * 订单编号
     */
    var orderCode: String
)

data class Resource(
    /**
     * 	图片链接，多个，号隔开
     */
    var images: String,
    /**
     * 	资源名称
     */
    var resourceName: String
)