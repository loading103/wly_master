package com.daqsoft.provider.bean

/**
 * @Description 景区预约须知
 * @ClassName   ScenicReservationBean
 * @Author      luoyi
 * @Time        2020/4/16 17:22
 */
data class ScenicReservationBean(
    /**
     * 今日是否可定（直接展示）
     */
    val purchaseLimit: String,
    /**
     * 售卖状态
     */
    val isOnSale: Boolean,
    /**
     * 入园人数
     */
    val adultTicket: String,
    /**
     * 预订限购
     */
    val limit: String,
    /**
     * 是否可退（直接展示）
     */
    val allowRefund: String,
    /**
     * 换票地址
     */
    val pickupAddress: String,
    /**
     * 入园地址
     */
    val passAddress: String,
    /**
     * 换票时间
     */
    val pickupTime: String,
    /**
     * 入园方式
     */
    val passType: String,
    /**
     * 是否需要人脸识别
     */
    val needFaceRecognition: Boolean,
    /**
     * 退款设置
     */
    val refundLimit: String,
    /**
     * 	商品名称
     */
    val productName: String,
    /**
     * 详细描述
     */
    val description: String,
    /**
     * 提前预订
     */
    val enterSightLimit: String,
    /**
     * 入园凭证
     */
    val exchangeVoucherType: String,
    /**
     * 入园限制
     */
    val enterSightDelayBookCheck: String,
    /**
     * 入园次数
     */
    val checkTimes: String,
    /**
     * 是否需要取票（直接展示）
     */
    val needTicket: String,
    /**
     * 入园时间
     */
    val useTimeRange: String,
    /**
     * 订单取消
     */
    val autoCancelTime: String,
    /**
     * 费用包含
     */
    val costIncludes: String
)

data class RouterReservationBean(
    /**
     * 线路类型
     */
    val route: String,
    /**
     *  跟团游,自助游,研学游,国内游,港澳台,出境游,半自助游
     */
    val crowd: String,
    /**
     * 	产品id
     */
    val id: String,
    /**
     *  产品编号
     */
    val productSn: String,
    /**
     * 费用包含
     */
    val included: String,
    /**
     *  费用不包含
     */
    val exclude: String,
    /**
     * 注意事项
     */
    val considerations: String,
    /**
     * 预订须知
     */
    val reservationNotes: String,
    /**
     *  商品详情
     */
    val description: String,
    var name:String
)