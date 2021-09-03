package com.daqsoft.integralmodule.repository.bean

/**
 * 任务列表实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
data class TaskListBean(
    /**
     * 是否自动领取
     * 1:自动 0：手动
     */
    val autoDraw: Int?,
    /**
     * 创建时间
     */
    val createTime: String?,
    /**
     * 	关键值
     */
    val cruxValue: String?,
    /**
     * 	结束时间
     */
    val endTime: String?,
    /**
     * 外部URL
     */
    val externalUrl: String?,
    /**
     * 完成状态 0 用户未领取 1 待完成 2 已完成未领取 3 已过期 4 已完成且用户领取
     */
    var finishStatus: Int?,
    /**
     * 图标
     */
    val icon: String?,
    /**
     * 任务去完成跳转URL
     */
    val jumpImmatureUrl: String?,
    /**
     * 	名称
     */
    val name: String?,
    /**
     * 发布渠道
     */
    val publishChannel: String?,
    /**
     * 奖励积分
     */
    val rewardIntegral: Int?,
    /**
     * 奖励类型
     */
    val rewardType: String?,
    /**
     * 已完成数
     */
    val speedCompletedCount: Int?,
    /**
     * 完成总数
     */
    val speedTotalCount: Int?,
    /**
     * 	开始时间
     */
    val startTime: String?,
    /**
     * 简介
     */
    val synopsis: String?,
    /**
     * 任务ID
     */
    val taskId: Int?,
    /**
     * 任务持续状态
     */
    val taskLastStatus: Int?,
    /**
     * 任务规则编码
     */
    val taskRuleCode: String?,
    /**
     * 任务类型编码
     */
    val taskTypeCode: String?,
    /**
     * 用户ID
     */
    val userId: Int?
)

data class CompleteTaskBean(
    /**
     * 领取状态 0 不能再领取 1 可以再领取 2 去完成
     */
    var receiveStatus: Int
)