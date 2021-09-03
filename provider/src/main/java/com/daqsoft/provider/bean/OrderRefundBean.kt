package com.daqsoft.provider.bean

/**
 * @ClassName    OrderRefundBean
 * @Description  购物退款列表
 * @Author       yuxc
 * @CreateDate   2020/12/2
 */
data class OrderRefundBean(
    var pageNum: Int?,
    var pageSize: Int?,
    var pages: Int?,
    var refundData: MutableList<RefundDataBean>?,
    var total: Int?
)

data class RefundDataBean(
    var amount: Double?,
    var applyAmount: Any?,
    var applyOperator: String?,
    var applyOperatorName: Any?,
    var applyReasonId: Any?,
    var applyReasonName: Any?,
    var applyRemark: Any?,
    var auditRemark: Any?,
    var auditStatus: Int?,
    var auditType: Int?,
    var businessName: String?,
    var consumptionStatus: Any?,
    var days: Any?,
    var freightAmount: Any?,
    var gmtAudit: Long?,
    var gmtCreate: Long?,
    var gmtModified: Long?,
    var gmtRefund: Long?,
    var hotelSn: Any?,
    var id: Int?,
    var logisticsStatus: Any?,
    var orderId: Int?,
    var orderSn: String?,
    var orderType: Int?,
    var playDate: Any?,
    var productId: Any?,
    var productName: String?,
    var productNum: Int?,
    var productSn: Any?,
    var refundServiceType: Any?,
    var refundStatus: Int?,
    var saleType: Int?,
    var serviceAmount: Any?,
    var sn: String?,
    var standardName: String?,
    var surplusLimitAmount: Any?,
    var sysCode: String?,
    var thumbImageUrl: String?,
    var ticketNum: Any?,
    var trainTime: Any?
)