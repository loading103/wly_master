package com.daqsoft.provider.network.home.bean

/**
 * 个人当前积分
 * @author 黄熙
 * @date 2020/3/4 0004
 * @version 1.0.0
 * @since JDK 1.8
 */
class UserCurrPoint(
    /**
     * 当前积分
     */
    val currPoint: Int,
    /**
     * 等级
     */
    val level: String,
    /**
     * 图标
     */
    val icon: String
)
