package com.daqsoft.integralmodule.common

import com.daqsoft.integralmodule.R

/**
 * 任务类型
 * @author 黄熙
 * @date 2020/3/6 0006
 * @version 1.0.0
 * @since JDK 1.8
 */
object TaskTypeCode {
    /**
     * 签到
     */
    const val TASK_TYPE_SIGN = "SIGN"
    /**
     * 点赞
     */
    const val TASK_TYPE_THUMB = "THUMB"
    /**
     * 登录
     */
    const val TASK_TYPE_LOGIN = "LOGIN"
    /**
     * 注册
     */
    const val TASK_TYPE_REGISTER = "REGISTER"
    /**
     * 评论
     */
    const val TASK_TYPE_COMMENT = "COMMENT"
    /**
     * 收藏
     */
    const val TASK_TYPE_COLLECT="COLLECT"
    /**
     * 话题互动
     */
    const val TASK_TYPE_STORY_INTERACT = "STORY_INTERACT"
    /**
     * 上封面
     */
    const val TASK_TYPE_STORY_COVER = "COVER"
    /**
     * 参与话题
     */
    const val TASK_TYPE_STORY_TOPIC = "TOPIC"
    /**
     * 写故事、发攻略
     */
    const val TASK_TYPE_STORY_STORY = "STORY"
    /**
     * 外部任务
     */
    const val TASK_TYPE_EXTERNAL_TASK="EXTERNAL_TASK"
    /**
     * 下单
     */
    const val TASK_TYPE_PAY_ORDER="PAY_ORDER"
    /**
     * 关注公众号
     */
    const val TASK_TYPE_FOLLOW_PUBLIC_NUMBER="FOLLOW_PUBLIC_NUMBER"
    /**
     * 活动室预订
     */
    const val TASK_TYPE_ACTIVITY_ROOM = "ACTIVITY_ROOM"
    /**
     * 活动预订
     */
    const val TASK_TYPE_ACTIVITY="ACTIVITY"
    /**
     * 根据任务类型Code获得IMg
     * @param type 类型
     */
    fun TaskTypeCodeToImg(type: String): Int {
        var iconId = when (type) {
            // 登录
            TASK_TYPE_LOGIN -> R.mipmap.mine_member_portal_mission_icon_login
            // 评论
            TASK_TYPE_COMMENT -> R.mipmap.mine_member_portal_mission_icon_comment
            // 注册
            TASK_TYPE_REGISTER -> R.mipmap.mine_member_portal_mission_icon_register
            // 上封面
            TASK_TYPE_STORY_COVER -> R.mipmap.mine_member_portal_mission_icon_cover
            // 参与话题
            TASK_TYPE_STORY_TOPIC -> R.mipmap.mine_member_portal_mission_icon_topic
            // 写故事、发攻略
            TASK_TYPE_STORY_STORY -> R.mipmap.mine_member_portal_mission_icon_writestory
            // 话题互动
            TASK_TYPE_STORY_INTERACT -> R.mipmap.mine_member_portal_mission_icon_hudong
            // 点赞
            TASK_TYPE_THUMB -> R.mipmap.mine_member_portal_mission_icon_thumbsup
            // 签到
            TASK_TYPE_SIGN -> R.mipmap.mine_member_portal_mission_icon_signin
            else -> R.mipmap.mine_member_portal_mission_icon_cover
        }
        return iconId
    }
}