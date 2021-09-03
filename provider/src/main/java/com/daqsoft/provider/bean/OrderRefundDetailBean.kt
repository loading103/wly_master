package com.daqsoft.provider.bean

/**
 * @ClassName    OrderRefundDetailBean
 * @Description  购物退款详情
 * @Author       yuxc
 * @CreateDate   2020/12/3
 */
data class OrderRefundDetailBean(
    var amount: Double?,
    var applyAmount: Double?,
    var applyOperator: String?,
    var applyOperatorName: String?,
    var applyReasonId: Int?,
    var applyReasonName: String?,
    var applyRemark: String?,
    var auditRemark: String?,
    var auditStatus: Int?,
    var auditType: Int?,
    var businessName: String?,
    var consumptionStatus: Int?,
    var days: Int?,
    var freightAmount: Double?,
    var gmtAudit: Long?,
    var gmtCreate: Long?,
    var gmtModified: Long?,
    var gmtRefund: Long?,
    var hotelSn: String?,
    var id: Int?,
    var logisticsStatus: Int?,
    var orderId: Int?,
    var orderSn: String?,
    var orderType: Int?,
    var playDate: String?,
    var productId: Int?,
    var productName: String?,
    var productNum: Int?,
    var productSn: String?,
    var refundServiceType: String?,
    var refundStatus: Int?,
    var saleType: Int?,
    var serviceAmount: Double?,
    var sn: String?,
    var standardName: String?,
    var surplusLimitAmount: Any?,
    var sysCode: String?,
    var thumbImageUrl: String?,
    var ticketNum: String?,
    var trainTime: String?
)