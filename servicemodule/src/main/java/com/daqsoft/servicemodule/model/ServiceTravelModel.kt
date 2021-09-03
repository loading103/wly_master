package com.daqsoft.servicemodule.model

import com.daqsoft.provider.bean.ValueKeyBean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/2 19:44
 * @version 1.0.0
 * @since JDK 1.8
 */
object ServiceTravelModel {
    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("距离优先", "1")
    )

    /**
     * 等级类型
     */
    val levels = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("AAAAA", "travelLevel_1"),
        ValueKeyBean("AAAA", "travelLevel_2"),
        ValueKeyBean("AAA", "travelLevel_3"),
        ValueKeyBean("未分级", "travelLevel_")
    )

}