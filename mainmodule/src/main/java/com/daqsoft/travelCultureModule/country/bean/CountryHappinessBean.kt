package com.daqsoft.travelCultureModule.country.bean


/**
 * desc :本地数据对应的值
 * @author 江云仙
 * @date 2020/4/14 19:33
 */
object CountryHappinessBean {
    /**
     * 排序类型
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("订单优先", "order"),
        ValueKeyBean("诚信优先", "credit"),
        ValueKeyBean("距离优先", "distance"),
        ValueKeyBean("推荐优先", "recommend"),
        ValueKeyBean("人气优先", "popularity"),
        ValueKeyBean("好评优先", "praise")
    )
}