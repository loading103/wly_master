package com.daqsoft.provider.network.home.bean

/**
 * 用一句话来描述功能
 * @author 黄熙
 * @date 2020/3/4 0004
 * @version 1.0.0
 * @since JDK 1.8
 */
data class UserPointTaskInfoBean(
    /**
     *	已完成任务，但未领取积分的任务数量
     */
    val notReceiveNum: Int,
    /**
     *	签到奖励积分(若不存在签到任务，则为空)
     */
    val signPoint: Int,
    /**
     *签到状态，同任务中心常量一致
     */
    val signStatus: Int
)