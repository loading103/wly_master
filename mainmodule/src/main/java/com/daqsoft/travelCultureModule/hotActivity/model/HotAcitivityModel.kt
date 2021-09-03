package com.daqsoft.travelCultureModule.hotActivity.model

import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @Description 活动数据
 * @ClassName  HotAcitivityModel
 * @Author      luoyi
 * @Time       2020-3-20 14:50
 */

object HotActivityModel {

    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("距离优先", "2"),
        ValueKeyBean("人气优先", "3"),
        ValueKeyBean("最新", "4")
    )


    /**
     * 选择类型
     */
    val selectMethods = mutableListOf(
        ValueKeyBean("免费预订", "ACTIVITY_MODE_FREE"),
        ValueKeyBean("积分预订", " ACTIVITY_MODE_INTEGRAL"),
        ValueKeyBean("免费报名", "ACTIVITY_MODE_ENROLL_FREE"),
        ValueKeyBean("积分报名", "ACTIVITY_MODE_ENROLL_INTEGRAL"),
        ValueKeyBean("宣传活动", "ACTIVITY_TYPE_PLAIN"),
        ValueKeyBean("普通活动", "ACTIVITY_TYPE_SERVICE"),
        ValueKeyBean("志愿招募", "ACTIVITY_MODE_VOLUNT"),
        ValueKeyBean("诚信优享", "CREDIT_USE"),
        ValueKeyBean("诚信免审", " CREDIT_AUDIT"),
        ValueKeyBean("付费报名", "ACTIVITY_MODE_ENROLL_PAY"),
        ValueKeyBean("付费预订", "ACTIVITY_MODE_INTEGRAL_PAY"),
        ValueKeyBean("其他活动", " ACTIVITY_MODE_OTHER")
    )

    /**
     * 月份选择
     */
    val months = mutableListOf(
        ValueKeyBean("1月", "1"),
        ValueKeyBean("2月", " 2"),
        ValueKeyBean("3月", "3"),
        ValueKeyBean("4月", "4"),
        ValueKeyBean("5月", "5"),
        ValueKeyBean("6月", "6"),
        ValueKeyBean("7月", "7"),
        ValueKeyBean("8月", " 8"),
        ValueKeyBean("9月", "9"),
        ValueKeyBean("10月", "10"),
        ValueKeyBean("11月", " 11"),
        ValueKeyBean("12月", " 12")
    )
    /**
     * 月份英文
     */
    val enMonths = mutableListOf(
        ValueKeyBean("1", "Jan."),
        ValueKeyBean("2", " Feb."),
        ValueKeyBean("3", "Mar."),
        ValueKeyBean("4", "Apr."),
        ValueKeyBean("5", "May"),
        ValueKeyBean("6", "Jun."),
        ValueKeyBean("7", "Jul."),
        ValueKeyBean("8", "Aug."),
        ValueKeyBean("9", "Sep."),
        ValueKeyBean("10", "Oct."),
        ValueKeyBean("11", "Nov."),
        ValueKeyBean("12", "Dec.")
    )

}