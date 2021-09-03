package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   CommetaryInfoBean
 * @Author      luoyi
 * @Time        2020/7/10 9:20
 */
class CommetaryInfoBean(
    /**
     * 是否允许团队预约
     */
    var teamOrderStatus: Int,
    /**
     * 团队至少提前N天
     */
    var teamAdvanceOrderDay: Int,
    /**
     * 当前是否可预约状态（可预约才能打开开关）
     */
    var status: Boolean,
    /**
     * 是否允许个人预约
     */
    var personOrderStatus: Int,
    /**
     * 个人至少提前N天
     */
    var personAdvanceOrderDay: Int,
    /**
     * 不可预约星期
     */
    var notOrderWeek: String,
    /**
     * 节假日是否开放
     */
    var historyOrderStatus: Int,
    /**
     * 预定时间列表
     */
    var guideOrderTimes: MutableList<GuideOrderTime>,
    /**
     * 讲解展厅价格信息
     */
    var guideExplainCosts: MutableList<GuideExplainCost>,
    /**
     * 讲解站点列表
     */
    var guideExhibitions: MutableList<GuideExhibition>,
    /**
     * 可预约未来N天讲解
     */
    var futureOrderDayNum: Int,
    /**
     * 每时段最多预约展厅数
     */
    var exhibitionNum: Int
)

/**
 * 预定时间列表
 */
class GuideOrderTime(
    /**
     * 开始时间段
     */
    var orderTimeSubStart: String,
    /**
     * 结束时间段
     */
    var orderTimeSubEnd: String,
    /**
     * 时间段 ID
     */
    var id: Int,
    var currTimeOrderStatus: Boolean,
    /**
     * 0:不支持预约；1：可预约；2：已约满
     */
    var guideOrderStatus: Int
)

/**
 * 讲解展厅价格信息
 */
class GuideExplainCost(
    /**
     * 最小人数
     */
    var minNum: String,
    /**
     * 最大人数
     */
    var maxNum: String,
    /**
     * 英文价格
     */
    var engExplain: String,
    /**
     * 中文价格
     */
    var chnExplain: String
)

/**
 *  讲解站点列表
 */
class GuideExhibition(
    /**
     * 是否推荐
     */
    var recommend: Int,
    /**
     * 名称
     */
    var name: String,
    /**
     * 展厅ID
     */
    var id: Int,
    var isSelect: Boolean = false
)