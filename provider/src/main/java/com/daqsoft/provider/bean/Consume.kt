package com.daqsoft.provider.bean

import android.os.Parcelable
import com.daqsoft.provider.network.venues.bean.VenuesDetailsBean
import kotlinx.android.parcel.Parcelize

/**
 * 消费码实体
 */
@Parcelize
data class Consume(
    val activity: ConsumeActivity,
    val activityRoom: ConsumeActivityRoom,
    var venueInfo: VenueInfo,
    val backIntegral: String,
    val backMoney: String,
    val backNum: String,
    val commentStatus: String,
    val endTime: String,
    val id: String,
    val orderCode: String,
    val orderNum: String,
    val orderType: String,
    val payIntegral: String,
    val payMoney: String,
    val startTime: String,
    val status: String,
    var isGuideOrder:Int
) : Parcelable

/**
 * 消费码活动实体
 */
@Parcelize
data class ConsumeActivity(
    val activityId: String,
    val activityType: String,
    val address: String,
    val faithAuditStatus: String,
    val faithAuditValue: String,
    val faithUseStatus: String,
    val faithUseValue: String,
    val image: String,
    val integral: String,
    val lat: String,
    val lon: String,
    val money: String,
    val name: String,
    val phone: String,
    val resourceId: String,
    val resourceName: String,
    val resourceType: String,
    val seats: MutableList<SeatsInfo> = mutableListOf(),
    val serviceTime: String,
    val signEndTime: String,
    val signNum: String,
    val signStartTime: String,
    val useEndTime: String,
    val useStartTime: String
) : Parcelable

/**
 * 消费码活动室实体
 */
@Parcelize
data class ConsumeActivityRoom(
    val activityRoomId: String,
    val address: String,
    val faithAuditStatus: String,
    val faithAuditValue: String,
    val faithUseStatus: String,
    val faithUseValue: String,
    val image: String,
    val integral: String,
    val lat: String,
    val lon: String,
    val money: String,
    val name: String,
    val phone: String,
    val resourceId: String,
    val resourceType: String,
    val useEndTime: String,
    val useStartTime: String,
    val venueId: String,
    val venueName: String
) : Parcelable

/**
 * 消费码详情
 */
data class ConsumeDetail(
    val activity: ConsumeActivity,
    val activityRoom: ConsumeActivityRoom,
    val venueInfo: VenueInfo,
    val auditRemark: String,
    val code: String,
    val consumeNum: String,
    val orderEndTime: String,
    val orderNum: String,
    val orderRemark: String,
    val orderStartTime: String,
    val orderType: String,
    val qrCode: String,
    val status: String,
    var idCard: String?,
    val userName: String,
    val userPhone: String,
    var orderId:String,
    var isGuideOrder: Int,
    var hasAttached:Int,
    var cardType:String?
)

@Parcelize
data class VenueInfo(
    val address: String,
    val adultNum: String,
    val cancelStatus: String,
    val cancelTime: String,
    val childNum: String,
    val companyName: String,
    val expectedTime: String,
    val image: String,
    val integral: String,
    val lat: String,
    val lon: String,
    val maxNum: String,
    val money: String,
    val oldManNum: String,
    val phone: String,
    val reservationType: String,
    val resourceId: String,
    val resourceType: String,
    val teenagerNum: String,
    val useEndTime: String,
    val useNum: String,
    val useStartTime: String,
    val validEndValue: String,
    val validTimeEndType: String,
    val validTimeStartType: String,
    val venueId: String,
    val venueName: String,
    var guideOrderLanguage: String,
    var guideOrderPayMoney: String,
    var guideName: String?,
    var guideExhibitions: MutableList<GuidEexhibit>?,
    /**
     * 关联讲解员信息
     */
    var hasRelationResource: HasRelationResource
) : Parcelable

@Parcelize
data class GuidEexhibit(
    var name: String?,
    var recommend: Int
) : Parcelable

@Parcelize
data class SeatsInfo(
    var col: String,
    var realCol: String,
    var row: String,
    var realRow: String
) : Parcelable


