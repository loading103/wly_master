package com.daqsoft.provider.bean

/**
 * @Description 邀请信息
 * @ClassName   InviteInfo
 * @Author      luoyi
 * @Time        2020/7/27 16:49
 */
data class InviteInfo(
    /**
     * 奖励积分
     */
    var rewardPoints: Int,
    /**
     * 最大邀请人数
     */
    var maxInvitePeople: Int,
    /**
     * 已邀请人数
     */
    var inviteSuccessPeople: Int,
    /**
     * 邀请码
     */
    var inviteCode: String?,
    /**
     * 最大邀请数
     */
    var existMaxInvitePeople: Boolean,
    /**
     * 站点名称
     */
    var siteName: String?
)

/**
 * @Description  邀请列表
 * @ClassName   InviteBean
 * @Author      luoyi
 * @Time        2020/7/27 16:49
 */
data class InviteBean(
    /**
     * 奖励积分
     */
    var rewardPoints: Int,
    /**
     * 昵称
     */
    var nickName: String?,
    /**
     * 成功邀请时间
     */
    var inviteTime: String?,
    /**
     * 邀请状态 ，目前只会等于1 ，表示正常
     */
    var inviteStatus: Int,
    /**
     * 头像地址
     */
    var headUrl: String?
)

/**
 * @Description   绑定（使用）邀请码
 * @ClassName  InviteInputBean
 * @Author      luoyi
 * @Time        2020/7/27 16:49
 */
data class InviteInputBean(
    /**
     * 当前用户头像
     */
    var myHeadUrl: String?,
    /**
     * 邀请码对应用户头像
     */
    var otherHeadUrl: String?
)

/**
 * 邀请码信息
 */
data class InviteCodeInfo(
    /**
     * 站点名称
     */
    var siteName: String,
    /**
     * 昵称
     */
    var nickName: String,
    /**
     * 邀请码
     */
    var inviteCode: String,
    /**
     * 是否已经邀请过
     */
    var beforeUseInviteCode: Boolean
)