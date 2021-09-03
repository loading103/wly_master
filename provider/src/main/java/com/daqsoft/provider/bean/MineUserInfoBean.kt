package com.daqsoft.provider.bean

/**
 * 个人中心互动页面
 * @author 黄熙
 * @date 2020/2/28 0028
 * @version 1.0.0
 * @since JDK 1.8
 */
class MineUserInfoBean(
    /**
     * 昵称
     */
    var nickName: String,
    /**
     * 头像
     */
    var headUrl: String,
    /**
     * 电话
     */
    var phone: String,
    /**
     * 收藏数量
     */
    var collectionNum: Int,
    /**
     * 故事
     */
    var storyNum: Int,
    /**
     * 关注
     */
    var focusNum: Int,
    /**
     * 非遗传承人
     */
    val heritagePeopleId: String
)

/**
 * 刷新用户信息
 */
class MineFreeInfoBean(
    var mobile: String?,
    var name: String?,
    var siteCode: String?
)