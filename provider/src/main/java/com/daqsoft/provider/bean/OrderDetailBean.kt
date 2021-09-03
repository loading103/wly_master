package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 订单详情
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-4
 * @since JDK 1.8.0_191
 */
data class OrderDetailBean(
    /**
     * 允许退款或退货: false不允许 true允许
     */
    val allowRefund: Boolean?,
    /**
     * 总金额
     */
    val amount: Double?,

    val type: String?,
    /**
     * 未支付订单自动取消时间
     */
    val autoCancelTime: String?,
    val bookingCertificates: String?,
    val bookingConfirmType: String?,
    val bookingConsumeType: String?,
    val bookingProductNum: String?,
    val bookingTimeEnd: String?,
    val bookingTimeStart: String?,
    /**
     * 服务商名称详细地址
     */
    val businessAddress: String?,
    /**
     * 服务商名称纬度
     */
    val businessLatitude: String?,
    /**
     * 服务商名称经度
     */
    val businessLongitude: String?,
    /**
     * 服务商名称
     */
    val businessName: String?,
    /**
     * 服务商名称客服电话
     */
    val businessTel: String?,
    /**
     * 是否可以退款
     */
    val canRefund: Boolean?,
    val canRefundDate: String?,
    val commodityServiceAmount: String?,
    val commodityServiceRate: String?,
    val consumeCode: String?,
    /**
     * 已经核销商品数量
     */
    val consumeProductNum: String?,
    val consumeStatus: String?,
    val consumptionStatus: String?,
    val consumptionTime: String?,
    /**
     * 联系人地址
     */
    val contactsAddress: String?,
    /**
     * 联系人证件号
     */
    val contactsCredentials: String?,
    /**
     * 证件类型Idcard(身份证),Passport(护照),TaiwanPermit(台胞证),HKA
     */
    val contactsCredentialsType: String?,
    /**
     * 联系人姓名
     */
    val contactsName: String?,
    /**
     * 联系人手机号
     */
    val contactsTel: String?,
    /**
     * 已发货数量
     */
    val deliveryProductNum: String?,
    val expireTime: String?,
    /**
     * 运费金额
     */
    val freightAmount: Double?,
    /**
     * 运费类型 0 不支持运输、1 包邮、2 固定运费、3 运费模版
     */
    val freightType: String?,
    /**
     * 下单日期
     */
    val gmtCreate: Long?,
    /**
     * 创建时间格式化字段（yyyy-mm-dd）
     */
    val gmtCreateStr: String?,
    /**
     * 修改日期
     */
    val gmtModified: Long?,
    val hotel: Hotel?,
    /**
     * 订单主键
     */
    val id: Int?,
    /**
     * 可送积分
     */
    val integral: String?,
    /**
     * 是否评价
     */
    val isEvaluate: Boolean?,
    /**
     * 是否送积分
     */
    val isSendIntegral: Boolean?,
    val logisticsCompanyName: String?,
    /**
     * 发货时间
     */
    val logisticsDeliveryTime: Long?,
    /**
     * 收货时间
     */
    val logisticsReceiveTime: Long?,
    /**
     * 	物流状态：0待发货，1已发货，2已收货 , 3部分发货
     */
    val logisticsStatus: String?,
    val needBookingTime: String?,
    /**
     * 应发货数量 (如退货后该值与购买商品数量会不一致)
     */
    val needDeliveryProductNum: String?,
    /**
     * 是否需要人脸识别
     */
    val needFaceRecognition: Boolean?,
    /**
     * 需要预约才能消费: false无需预约 true需要预约
     */
    val needPrecontract: Boolean?,
    /**
     * 是否开启评价
     */
    val openEvaluate: Boolean?,
    /**
     * 用户openid
     */
    val openId: String?,
    /**
     * 订单总金额
     */
    val orderAmount: Double?,
    /**
     * 订单渠道
     */
    val orderChannel: String?,
    /**
     * 优惠金额
     */
    val orderFreeAmount: String?,
    val orderHotel: OrderHotel?,
    /**
     * 支付金额
     */
    val orderPayAmount: Double?,
    /**
     * 备注信息
     */
    val orderRemarks: String?,
    val orderRoute: OrderRoute?,
    val orderRouteTourists: MutableList<OrderRouteTourists>?,
    /**
     * 订单编号
     */
    val orderSn: String?,
    /**
     * 实物订单 状态：0待支付 1已支付 2已完成 3已关闭 20待发货，21已发货
     * 虚拟预约订单状态：0待支付 1已支付 2已完成 3已关闭 10待消费 12已失效 32待确认 40待预约
     */
    val orderStatus: String?,
    /**
     * 订单状态描述
     */
    val orderStatusName: String?,
    val orderSyncStatus: String?,
//    val orderTicketTouristDtoList: String?,
    /**
     * 订单类型 1实物订单 2 虚拟订单 3门票订单 5酒店订单 6线路订单
     */
    val orderType: String?,
    /**
     * OTA代码
     */
    val otaCode: String?,
    val outGoodsSn: String?,
    val outOrderSn: String?,
    val pId: String?,

    // 多少游客共享一个游客信息，0:不需要游客信息，1:每个游客需要，9999:只需要一位游玩人信息，其他
    val passengerInfoPerNum: String?,
    /**
     * 支付渠道(1平台渠道，2自有渠道)
     */
    val payChannel: String?,
    /**
     * 支付流水号
     */
    val payNo: String?,
    val paySyncStatus: String?,
    /**
     * 支付时间
     */
    val payTime: Long?,
    /**
     * 支付时间格式化字符串(yyyy-mm-dd)
     */
    val payTimeStr: String?,
    /**
     * 支付类型 1微信支付 2线下支付 3微信小程序支付
     */
    val payType: String?,
    val platformIncomeAmount: String?,
    val platformServiceAmount: String?,
    val platformServiceRate: Double?,
    val platformVersion: String?,
    /**
     * 商品金额
     */
    val productAmount: Double?,
    /**
     * 成本价格
     */
    val productCostPrice: Double?,
    /**
     * 商品id
     */
    val productId: String?,
    /**
     * 商品名称
     */
    val productName: String?,
    /**
     * 商品数量
     */
    val productNum: String?,
    /**
     * 商品单价
     */
    val productPrice: Double?,
    /**
     * 结算方式:0订单完成后结算 1支付成功后结算(酒店客房确认成功后结算)
     */
    val productSettlementType: Boolean?,
    /**
     * 商品编码
     */
    val productSn: String?,
    /**
     * 商品来源1小电商，2供销平台，3gds,4供应商端
     */
    val productSource: String?,
    /**
     * 退款金额
     */
    val refundAmount: Double?,
    /**
     * 允许退款是否需要审核 false不需要 true需要
     */
    val refundAudit: Boolean?,
    /**
     * 	退款数量
     */
    val refundNum: String?,
    /**
     * 已退款商品数量
     */
    val refundProductNum: String?,
    val refundStatus: String?,
    /**
     * 	对接名称
     */
    val relationName: String?,
    val remainProductNum: String?,
    val reservationStatus: String?,
    val reservationTime: String?,
    val reservationUseTime: String?,
    val reverseNum: String?,
    /**
     * 销售方式 1平台直接销售,2达人直销3达人间接销售
     */
    val saleType: String?,
    val scenicName: String?,
    val scenicResourcesDto: ScenicResourcesDto?,
//    val scenicResourcesDto: String?,
    val scenicSightTypeTitle: String?,
    /**
     * 商家备注
     */
    val sellerRemarks: String?,
    val sendVoucherQuantity: String?,
    val sendVoucherStatus: String?,
    val settlementType: String?,
    /**
     * 商品规格
     */
    val standardId: String?,
    /**
     * 商品规格名称
     */
    val standardName: String?,
    /**
     * 实物虚拟品类规格商品编号
     */
    val standardProductSn: String?,
    val stationList: String?,
    /**
     * 店铺服务电话
     */
    val substationServiceTel: String?,
    val supplieIncomeAmount: Double?,
    val supplieServiceAmount: String?,
    val supplieServiceRate: String?,
    val supplierId: String?,
    /**
     * 供应商服务电话
     */
    val supplierServiceTel: String?,
    /**
     * 子站编码
     */
    val sysCode: String?,
    val talentDirectCommissionAmount: String?,
    val talentDirectCommissionRate: String?,
    /**
     * 达人提成方式：0统一提成 1自定义比例
     */
    val talentExtractType: Boolean?,
    val talentId: String?,
    val talentIndirectCommissionAmount: String?,
    val talentIndirectCommissionRate: String?,
    val throughTrain: String?,
    /**
     * 	商品缩略图
     */
    val thumbImageUrl: String?,
    val useEndTime: String?,
    val useStartTime: String?,
    /**
     * 用户id
     */
    val userId: String?
)

/**
 * 获取订单地址信息
 * @author luoyi
 * @version 1.0.0
 * @date 2020-4-22 13：46
 * @since JDK 1.8.0_191
 */
@Parcelize
data class OderAddressInfoBean(
    /**
     * 订单类型
     */
    val orderType: String,
    /**
     * 平台名称
     */
    val platfromName: String,
    /**
     * 平台logo
     */
    val logo: String,
    /**
     * 店铺id
     */
    val shopId: String,
    /**
     * 店铺地址
     */
    val url: String,
    /**
     * 提示信息
     */
    var linkTips: String
) : Parcelable

data class ScenicResourcesDto(
    var id: Int,
    var scenicName: String,
    var scenicAddress: String,
    val scenicPhone: String,
    val scenicLatitude: String,
    val scenicLongitude: String
)