package com.daqsoft.integralmodule.repository.api

/**
 * 积分Api
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
object IntegralApi {
    /**
     * 会员当前积分统计
     */
    const val POINT_COUNT = "user/api/point/pointCount"
    /**
     * 当前积分信息
     */
    const val CURR_POINT = "user/api/point/currPoint"
    /**
     * 榜单信息
     */
    const val POINT_RECORD = "user/api/point/pointRecord"
    /**
     * 用户积分详情
     */
    const val DETAIL = "user/api/point/detail"
    /**
     * 积分配置规则
     */
    const val POINT_CONFIG_INFO = "user/api/point/pointConfigInfo"

    /**
     * 获取城市名片
     */
    const val CITY_CARD = "user/api/siteVisitingCard/getVisitingCard"

    /**
     * 签到
     */
    const val CHECK_IN = "user/api/userBind/checkIn"
    /**
     * 签到状态和未领取任务数
     */
    const val POINT_TASK_INFO = "user/api/point/pointTaskInfo"
    /**
     * C端用户任务列表
     */
    const val GET_API_TASK_LIST = "task/front/list/getApiTaskList"
    /**
     * 完成任务
     */
    const val POS_COMPLETE_TASK="task/finish/completeTask"
    /**
     * 接取任务
     */
    const val PICK_TASK="task/finish/pickTask"

    /**
     * 详情
     */
    const val SITE_INFO = "user/api/site/siteInfo"
    /**
     * 积分商城
     */
    const val SHOP = "http://sub9166280.c.xds.daqsoft.com/integral/shopping?"
    /**
     * 消息未读数1
     */
    const val NO_READ_NUMBER = "user/api/user/message/getNotReadNum"
    /**
     * 消息未读数2
     */
    const val NO_READ_NUMBER_T = "websocket/api/message/indexMessageNum"
    /**
     * 消息轮播
     */
    const val NO_READ_LIST = "websocket/api/message/importantMsg"
}