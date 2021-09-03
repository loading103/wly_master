package com.daqsoft.provider.bean

/**
 * @Description 订单申请退款的实体
 * @ClassName   OrderRefund
 * @Author      PuHua
 * @Time        2019/12/19 13:40
 */
data class OrderRefund(
    // 允许可退款商品数量
    val allowRefundNum: String,
    // 实物订单运费（若当前退款数量==允许退款数量，则退款金额+运费）
    val freight: String,
    // 订单id
    val orderId: String,
    // 订单类型 1实物订单 2 虚拟订单
    val orderType: String,
    // 是否允许部分退
    val partRefund: Boolean,
    // 订单商品数量
    val productNum: String,
    // 商品单价
    val productPrice: Double,
    // 门票订单 手续费
    val ticketFee: Double,
    // 票类型
    val ticketTypeMap:HashMap<String,TicketType>
)
/**
 * @des 退款理由
 * @author PuHua
 * @Date 2019/12/19 14:14
 */
data class RefundReason(
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}

data class TicketType(var quantity:Int,
                      val price:Double,
                      val name:String,
                      val costPrice:Double,
                      val refunded:Int,
                      val type:String
                      )