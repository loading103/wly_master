package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description
 * @ClassName   CommentaryOrderInfoBean
 * @Author      luoyi
 * @Time        2020/7/10 9:28
 */
class CommentaryOrderInfoBean(
    /**
     * 存在场馆订单时候有值
     */
    var venueOrder: VenueOrder,
    /**
     * 存在讲解订单时有值
     */
    var venueGuideOrder: GuideOrderInfo

)

@Parcelize
data class VenueOrder(
    /**
     * 会员手机号
     */
    var userPhone: String?,
    /**
     * 会员姓名
     */
    var userName: String?,
    /**
     * 使用数量
     */
    var useNum: Int,
    /**
     * 青年数量
     */
    var teenagerNum: Int,
    /**
     * 预约类型、个人/团队
     */
    var reservationType: String?,
    /**
     * 支付金额
     */
    var payMoney: Int,
    /**
     * 支付积分
     */
    var payIntegral: Int,
    /**
     * 场馆预约时间段 ID
     */
    var orderTimeId: Int,
    /**
     * 订单 ID
     */
    var orderId: Int,
    /**
     * 预约日期
     */
    var orderDate: String?,
    /**
     * 老人数量
     */
    var oldManNum: Int,
    /**
     * 	单位名称
     */
    var companyName: String?,
    /**
     * 儿童数量
     */
    var childNum: Int,
    /**
     * 成人数量
     */
    var adultNum: Int,
    var idCard: String,
    var orderTime: OrderTime?,
    var venueId: String?,
    var venueRuleId: String?,
    var idCardEncryption: String?,
    var healthCodeRegion: String?,
    var cardType:String?
) : Parcelable

class VenueGuideOrder(
    /**
     * 讲解时间段 ID
     */
    var orderTimeId: String,
    /**
     * 订单 ID
     */
    var orderId: String,
    /**
     * 预约日期
     */
    var orderDate: String,
    /**
     * 支付金额
     */
    var guideOrderPayMoney: String,
    /**
     * 讲解语言
     */
    var guideOrderLanguage: String,
    /**
     * 展厅ID，多个,号隔开
     */
    var guideOrderExhibitionId: String
)

/**
 * 讲解员订单信息
 */
class GuideOrderInfo(
    /**
     * 讲解预约时间段 ID
     */
    var orderTimeId: Int,
    /**
     * 订单ID
     */
    orderId: Int,
    /**
     * 日期
     */
    var orderDate: String,
    /**
     * 支付金额
     */
    var guideOrderPayMoney: String,
    /**
     * 讲解语言
     */
    var guideOrderLanguage: String,
    /**
     * 展厅 ID，多个，号隔开
     */
    var guideOrderExhibitionId: String,

    var orderTime: OrderTime,
    /**
     * 展厅列表
     */
    var exhibitionList: MutableList<Exhibition>
)

/**
 * 展厅列表
 */
data class Exhibition(
    /**
     * 是否推荐，1是0否
     */
    var recommend: Int,
    /**
     * 展厅名称
     */
    var name: String
)

@Parcelize
data class OrderTime(
    /**
     * 开始时间
     */
    var startTime: String,
    /**
     * 结束时间
     */
    var endTime: String
) : Parcelable