package com.daqsoft.provider.bean

import android.graphics.Bitmap


/**
 * 消费码--电子码（小电商）
 */

data class ElectronicTicketData(
    val anonymousCode: String,
    val bookingConfirmType: Boolean,
    val bookingStatus: String,
    val bookingTimeEnd: String,
    val bookingTimeStart: String,
    val businessName: String,
    val currentTime: String,
    val ecodeStatus: String,
    val existRejectStatus: Boolean,
    val existWaitConfirmedStatus: Boolean,
    val id: String,
    val type: String,
    val needBookingTime: String,
    val needPrecontract: Boolean,
    /**
     * 0-9 订单状态：0待支付 1已支付 2已完成3已取消
     * 10-19 消费状态：10未消费，11已消费,12已失效
     * 20-29 物流状态：20待发货，21已发货，22已收货
     * 30-39 预约状态：30待支付 31已取消 32已支付/待确认 33驳回预约 34确认预约 35已过期 36已退款
     * 40-49 订单表中的预约状态：40待预约  41已预约
     */
    val orderStatus: String,
    val orderStatusName:String,
    val refundStatus:Boolean,
    val orderType: String,
    val productName: String,
    val productNum: String,
    val reservationUseTime: String,
    val scenicName: String,
    val standardId: String,
    val standardName: String,
    val thumbImageUrl: String,
    val useEndTime: String,
    val useStartTime: String
)

/**
 * @des 小电商消费码详情
 * @author PuHua
 * @date  
 */
data class ElectronicDetailBean(
    val allowBooking: Boolean,
    val appName: String,
    val bookingConfirmType: Boolean,
    val bookingDate: String,
    val bookingRecordDtoList: List<BookingRecordDto>?,
    val bookingTimeEnd: String,
    val bookingTimeStart: String,
    val businessDto: BusinessDto,
    val consumeCode: String,
    val consumeProductNum: Int,
    val consumptionStatus: Int,
    val consumptionTime:String,
    val contactsTel: String,
    val contactsName:String,
    val currentTime: Long,
    val goodsSn: String,
    val id: Int,
    var qrCode: Bitmap?,
    val isAdditional: String,
    val needBookingTime: Int,
    val needPrecontract: Int,
    val needUseTime: Int,
    val openid: String,
    val orderSn: String,
    val orderStatus: Int,
    val orderStatusName: String,
    val orderTicketInfoDto: OrderTicketInfo,
    val orderTicketTouristDtoList: List<OrderTicketTourist>,
    // 1实物  2虚拟  3门票
    val orderType: Int,
    val otaCode: String,
    val playDate: String,
    val productId: Int,
    val productName: String,
    val productNum: String,
    val refundProductNum: Int,
    val remainProductNum: Int,
    val reservationNum: Int,
    val reservationStatus: Int,
    val reservationTime: String,
    val reservationUseTime: String,
    val scenicResourcesDto: ScenicResource,
    val sptTitle: String,
    val standardName: String,
    val supplierId: Int,
    val sysCode: String,
    val thumbImageUrl: String,
    val useEndTime: String,
    val useStartTime: String,
    val refundStatus:Boolean,
    var canRefundDate: String
)

data class BookingRecordDto(
    val bookingDate: Long,
    val bookingIncrease: Int,
    val bookingMobile: String,
    val bookingName: String,
    val bookingQuantity: Int,
    val bookingStatus: Int,
    val bookingType: Boolean,
    val credentialsNumber: String,
    val credentialsType: Int,
    val ecodeStatus: Int,
    val gmtCreate: Long,
    val id: Int
)

data class BusinessDto(
    val address: String,
    val areaFullName: String,
    val areaId: Int,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val tel: String
)

data class ElectronicQrCode(
    val consumeCode: String,
    val qrCode: String
)

data class OrderTicketInfo(
    val adultTicket:String,
    val checkTime:String,
    val costIncludes:String,
    val enterSightLimit:String,
    val exchangeVoucherType:String,
    val guideTips:String,
    val passAddress:String,
    val passType:String,
    val perdayCheckTimes:String,
    val pickupAddress:String,
    val pickupTime:String
)

data class OrderTicketTourist(
    val consumeDate:String,
    val consumeNum:String,
    val consumeStatus:String,
    val credentialsNumber:String,
    val credentialsType:String,
    val faceImage:String,
    val isCollectFace:String,
    val localETicket:String,
    val outETicket:String,
    val refundStatus:String,
    val touristId:String,
    val touristMobile:String,
    val touristName:String
)

data class ScenicResource(
   val areaCode:String,
   val id:String,
   val outServiceProviderSn:String,
   val scenicAddress:String,
   val scenicAreaFullName:String,
   val scenicBizHours:String,
   val scenicLatitude:String,
   val scenicLongitude:String,
   val scenicName:String,
   val scenicPhone:String,
   val scenicResourceStatus:String,
   val scenicSummary:String,
   val sightLevel:String,
   val sn:String
)