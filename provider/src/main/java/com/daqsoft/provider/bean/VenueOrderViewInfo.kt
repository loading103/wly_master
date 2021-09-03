package com.daqsoft.provider.bean

/**
 * @Description 场馆预订订单view
 * @ClassName   VenueOrderViewInfo
 * @Author      luoyi
 * @Time        2020/5/13 17:07
 */
class VenueOrderViewInfo(
    /**
     *  成年人/老人 可带小孩数
     */
    var childNum: Int,
    /**
     *   团队预约人数区间最大值
     */
    var teamNumMax: Int,
    /**
     * 团队预约人数区间最小值
     */
    var teamNumMin: Int,
    /**
     * 个人预约人数区间最大值
     */
    var personNumMax: Int,
    /**
     * 个人预约人数区间最小值
     */
    var personNumMix: Int,
    /**
     * 图片
     */
    var images: String,
    /**
     * 最大接待人数
     */
    var maxNum: Int,
    /**
     *  场馆类型
     */
    var type: String,
    /**
     *  场馆名称
     */
    var venueName: String,
    /**
     * 场馆 ID
     */
    var id: Int,
    var times: MutableList<VenueOrderTime> = mutableListOf(),
    var personAdvanceOrderDay: Int,
    var teamAdvanceOrderDay: Int,
    /**
     * 预订须知
     */
    var orderNotice: String,
    var scenicName: String,
    /**
     * 预订用户信息选择类型1仅需一位用户，2需每位用户
     */
    var orderUserStatus: Int,
    /**
     * 用户预订需要的基本信息
     */
    var orderUserInfoType: String?,
    /**
     * 用户预订需要证件类型
     */
    var orderUserCredentialsType: String?,
    var originalOrgId: Int
)

/** 场馆预订时段
 * @Description
 * @ClassName  VenueOrderTime
 * @Author      luoyi
 * @Time        2020/5/13 17:07
 */
data class VenueOrderTime(
    /**
     * 备注
     */
    val remark: String,
    /**
     * 时间段结束时间
     */
    val orderTimeSubEnd: String,
    /**
     * 时间段开始时间
     */
    val orderTimeSubStart: String,
    /**
     * 日期结束
     */
    val orderDateEnd: String,
    /**
     * 日期开始
     */
    val orderDateStart: String,
    /**
     * 时间段ID
     */
    var id: Int,
    /**
     * 剩余库存
     */
    var stock: Int,
    /**
     * 是否预约过
     */
    var existVipOrder: Boolean,
    /**
     * 当天讲解员 是否可预约 0 不支持预约 1可预约 0 已约满
     */
    var guideOrderStatus: Int,
    var currTimeOrderStatus: Boolean
)
