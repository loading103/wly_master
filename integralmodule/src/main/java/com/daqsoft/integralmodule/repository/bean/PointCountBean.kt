package com.daqsoft.integralmodule.repository.bean

/**
 * 会员当前积分统计实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
data class PointCountBean(
    /**
     * 会员当前积分
     */
    val currPoint: Int?,
    /**
     * 最近30天获取积分
     */
    val getPoint: Int?,
    /**
     * 头像
     */
    val headIcon: String?,
    /**
     * 	积分等级图标
     */
    val icon: String?,
    /**
     * 	等级名称
     */
    val levelName: String?,
    /**
     * 下一等级积分
     */
    val nextLevelPoint: String,
    /**
     * 会员昵称
     */
    val nickName: String?,
    /**
     * 	体系名称
     */
    val sysName: String?,
    /**
     * 	历史总积分
     */
    val totalPoints: Int?,
    /**
     * 	最近30天使用积分
     */
    val usePoint: Int?
)