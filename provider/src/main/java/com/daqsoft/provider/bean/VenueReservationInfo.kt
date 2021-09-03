package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   VenueReservationInfo
 * @Author      luoyi
 * @Time        2020/4/30 17:44
 */
data class VenueReservationInfo(
    /**
     * 最大月份
     */
    var maxMonth: String,
    /**
     * 下一月份
     */
    var nextMonth: String,
    /**
     * 上一个月份
     */
    var preMonth: String,
    /**
     * 当月第一天
     */
    var firstWeek: Int,
    /**
     * 当前月份
     */
    var currMonth: String,
    /**
     * 预定需知
     */
    var orderNotice: String,
    /**
     * 节假日是否开放
     */
    var historyOrderStatus: Int,
    /**
     * 团队至少提前?天
     */
    var teamAdvanceOrderDay: Int,
    /**
     * 个人至少提前?天
     */
    var personAdvanceOrderDay: Int,
    /**
     * 团队是否可预定
     */
    var teamOrderStatus: Int,
    /**
     * 个人是否允许预定
     */
    var personOrderStatus: Int,
    /**
     * 当前日期
     */
    var currDay: Int,
    /**
     * 场馆名称
     */
    var venueName: String,
    /**
     * 场馆ID
     */
    var venueId: Int,
    var futureOrderDayNum: Int,
    var dateInfo: MutableList<VenueDateInfo>
)

/**
 * @Description 场馆日期实体
 * @ClassName   VenueDataInfo
 * @Author      luoyi
 * @Time        2020/4/30 17:44
 */
data class VenueDateInfo(
    /**
     * 天数
     */
    var index: Int,
    /**
     * 开放状态
     */
    var openStatus: Int,
    /**
     * 最大容量
     */
    var maxNum: Int,
    /**
     * 日期
     */
    var date: String,
    /**
     * 本地状态
     * 1 不在当月日期
     * 2 小于当前时间
     * 3 闭馆
     * 4 正常
     * 5 已预约过
     * 6 库存不足
     * 7 不在预约范围内
     */
    var status: Int,
    /**
     * 0 正常数据 1 更多实体
     */
    var type: Int = 0
) {
    /**
     * 预约数量
     */
    var num: Int = 0
    /**
     * 是否已经预约
     */
    var isHavedReservation: Boolean = false

    constructor(index: Int) : this(-1, 0, 0, "", 1) {

    }

    constructor(index: Int, type: Int) : this(-1, 0, 0, "", 1, 1) {
    }
}

/**
 * 场馆日期数量实体
 */
data class VenueDateNumBean(
    /**
     * 天数
     */
    var index: Int,
    /**
     * 订单数
     */
    var orderNum: Int,
    /**
     * 日期	string
     */
    var orderTime: String,
    /**
     * 	当前登录用户是否存在订单	1:存在，0：不存在
     */
    var existOrder: Int

) {
    constructor(index: Int) : this(-1, 0, "", 0) {

    }

}