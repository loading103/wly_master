package com.daqsoft.provider.bean

/**
 * 订单列表实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
data class OrderListBean(
    val list: List<X>?,
    val pageNum: Int?,
    val pageSize: Int?,
    val total: Int?
){
    data class X(
        /**
         * 总金额
         */
        val amount: Double?,
        /**
         * 已预约数量 虚拟产品
         */
        val bookingProductNum: Int?,
        /**
         * 商家名称 实物&虚拟
         */
        val businessName: String?,
        /**
         * 消费状态 0未消费，1已消费，2已失效（虚拟&门票）
         */
        val consumptionStatus: Int?,
        /**
         * 已发货数量 实物产品
         */
        val deliveryProductNum: Int?,
        /**
         * 订单id
         */
        val id: Int?,
        /**
         * 判断需要去评价  orderStatus=2   openEvaluate=true  isEvaluate=false
         */
        val isEvaluate: Boolean?,
        /**
         * 物流状态
         * 实物产品：0待发货，1已发货，2已收货，3部分发货
         */
        val logisticsStatus: Int?,
        val needBookingTime: Int?,
        /**
         * 应发货数量 实物产品
         */
        val needDeliveryProductNum: Int?,
        val needFaceRecognition: Boolean?,
        val needPrecontract: Boolean?,
        /**
         * 是否开启评价
         */
        val openEvaluate: Boolean?,
        /**
         * 订单支付总价
         */
        val orderPayAmount: String?,
        /**
         * 订单状态
         * 前端虚拟订单状态查询对应：
         * 待支付0  待预约40 待确认32  待消费10  已完成2  已失效12  已关闭3
         * 0-9 订单状态：0待支付 1已支付 2已完成 3已关闭
         * 10-19 消费状态：10待消费，11已消费,12已失效
         * 20-29 物流状态：20待发货，21已发货，22已收货
         * 30-39 预约表中的预约状态：30待支付 31已取消 32已支付/待确认 33驳回预约 34确认预约 35已过期 36已退款
         * 40-49 订单表中的预约状态：40待预约  41已预约
         */
        val orderStatus: Int?,
        /**
         * 订单状态名称
         */
        val orderStatusName: String?,
        /**
         * 订单类型 1实物；2虚拟；3门票
         */
        val orderType: Int?,
        /**
         * 产品名称
         */
        val productName: String?,
        /**
         * 产品数量
         */
        val productNum: Int?,
        /**
         * 余可预约数量 虚拟产品
         */
        val remainProductNum: Int?,
        /**
         * 0未预约，1已预约（虚拟产品，需要预约）
         */
        val reservationStatus: Int?,
        /**
         * 景区名称
         */
        val scenicName: String?,
        /**
         * 票型（门票）
         */
        val scenicSightTypeTitle: String?,
        /**
         * 发码数量
         */
        val sendVoucherQuantity: Int?,
        val standardName: String?,
        /**
         * 图片地址
         */
        val thumbImageUrl: String?,

        var routeOrderNum:List<RouteOrderNum>?,
        val days:Int,
        val useStartTime:String,
        val useEndTime:String
    )
    data class RouteOrderNum(
        var quantity:Int,
        var name:String
    )
}

