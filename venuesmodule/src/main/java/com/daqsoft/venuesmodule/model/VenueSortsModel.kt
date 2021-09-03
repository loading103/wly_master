package com.daqsoft.venuesmodule.model

import com.daqsoft.provider.bean.ValueKeyBean

object VenueSortsModel {

    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("距离优先", "disNum"),
        ValueKeyBean("人气优先", "hot"),
        ValueKeyBean("预约预订场馆", "isOpen"),
        ValueKeyBean("预约预订讲解", "guideIsOpen"),
        ValueKeyBean(
            "可预订活动", "activity"
        ),
        ValueKeyBean("可预订活动室", "orderRoom")
    )

}