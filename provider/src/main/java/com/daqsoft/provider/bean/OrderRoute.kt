package com.daqsoft.provider.bean
/**
 * @des 购买途径
 * @author PuHua
 * @Date 2019/12/13 9:56
 */
data class OrderRoute(
    val adultCostPrice: Double,
    val adultPrice: Double,
    val adultQuantity: Int,
    val allQuantity: List<AllQuantity>,
    val childrenCostPrice: Any,
    val childrenPrice: Any,
    val childrenQuantity: Int,
    val confirmStatus: Int,
    val consumeQuantity: Int,
    val consumeQuantityStr: String,
    val consumeStatus: Int,
    val consumptionTime: Long,
    val contractId: Int,
    val contractName: String,
    val customQuantity: Any,
    val finishTime: Any,
    val gmtCreate: Long,
    val gmtModified: Long,
    val goodsSn: String,
    val id: Int,
    val oldsterCostPrice: Any,
    val oldsterPrice: Any,
    val oldsterQuantity: Int,
    val orderSn: String,
    val partRefund: Boolean,
    val playDate: Long,
    val refundEndTimes: String,
    val refundStartTimes: String,
    val roomDiffPrice: Any,
    val roomDiffQuantity: Int,
    val surchargePrice: Any,
    val sysCode: String
)
/**
 * @des
 * @author PuHua
 * @Date 2019/12/13 9:58
 */
data class AllQuantity(
    val costPrice: Double,
    val name: String,
    val price: Double,
    val quantity: Int,
    val type: String
)
/**
 * @des
 * @author PuHua
 * @Date 2019/12/13 9:58
 */
data class OrderRouteTourists(
    val credentialsNumber: String,
    val credentialsType: String,
    val email: Any,
    val extInfo: Any,
    val gmtCreate: Long,
    val goodsSn: Any,
    val id: Int,
    val isContact: Any,
    val orderSn: String,
    val orderStatus: String,
    val orders: Int,
    val playDate: String,
    val quantity: Int,
    val sellPrice: Double,
    val supplierId: Int,
    val sysCode: String,
    val ticketName: String,
    val ticketType: String,
    val touristMobile: String,
    val touristName: String,
    val touristPinyin: Any
)
/**
 * @des 预订酒店
 * @author PuHua
 * @Date 2019/12/13 10:35
 */
data class OrderHotel(
    val allowRefund: Boolean,
    val bedName: String,
    val checkInTime: Any,
    val checkOutTime: Long,
    val checkStatus: Int,
    val days: Int,
    val hotelName: String,
    val hotelSn: String,
    val inDate: Long,
    val orderSn: String,
    val outDate: Long,
    val productSn: String,
    val refundAudit: Boolean,
    val roomDatePrice: String,
    val roomName: String,
    val roomSn: String
)
/**
 * @des 酒店
 * @author PuHua
 * @Date 2019/12/13 10:36
 */
data class Hotel(
    val address: String,
    val areaCode: String,
    val areaCodeAddress: String,
    val fullAddress: String,
    val hotelBizHours: String,
    val hotelLevel: String,
    val hotelName: String,
    val hotelPhone: String,
    val latitude: String,
    val longitude: String,
    val qyCode: String,
    val sn: String
)