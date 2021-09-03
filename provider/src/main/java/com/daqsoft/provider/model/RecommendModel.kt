package com.daqsoft.provider.model

import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @Description 推荐数据集
 * @ClassName   RecommendModel
 * @Author      luoyi
 * @Time        2020/3/27 17:47
 */
object RecommendModel {


    /**
     * 排序类型
     */
    val recommendsScenic = mutableListOf(
        ValueKeyBean("酒店", ResourceType.CONTENT_TYPE_HOTEL),
        ValueKeyBean("餐饮", ResourceType.CONTENT_TYPE_RESTAURANT),
        ValueKeyBean("场馆", ResourceType.CONTENT_TYPE_VENUE),
        ValueKeyBean("景区", ResourceType.CONTENT_TYPE_SCENERY),
        ValueKeyBean("娱乐场所", ResourceType.CONTENT_TYPE_ENTERTRAINMENT),
        ValueKeyBean("特产", ResourceType.CONTENT_TYPE_SPECIALTY)

    )
    val recommendsHotel = mutableListOf(
        ValueKeyBean("餐饮", ResourceType.CONTENT_TYPE_RESTAURANT),
        ValueKeyBean("场馆", ResourceType.CONTENT_TYPE_VENUE),
        ValueKeyBean("景区", ResourceType.CONTENT_TYPE_SCENERY),
        ValueKeyBean("酒店", ResourceType.CONTENT_TYPE_HOTEL),
        ValueKeyBean("娱乐场所", ResourceType.CONTENT_TYPE_ENTERTRAINMENT),
        ValueKeyBean("特产", ResourceType.CONTENT_TYPE_SPECIALTY)

    )
    val recommendsFood = mutableListOf(
        ValueKeyBean("景区", ResourceType.CONTENT_TYPE_SCENERY),
        ValueKeyBean("场馆", ResourceType.CONTENT_TYPE_VENUE),
        ValueKeyBean("酒店", ResourceType.CONTENT_TYPE_HOTEL),
        ValueKeyBean("餐饮", ResourceType.CONTENT_TYPE_RESTAURANT),
        ValueKeyBean("娱乐场所", ResourceType.CONTENT_TYPE_ENTERTRAINMENT),
        ValueKeyBean("特产", ResourceType.CONTENT_TYPE_SPECIALTY)

    )
    val recommendsVenue = mutableListOf(
        ValueKeyBean("景区", ResourceType.CONTENT_TYPE_SCENERY),
        ValueKeyBean("酒店", ResourceType.CONTENT_TYPE_HOTEL),
        ValueKeyBean("餐饮", ResourceType.CONTENT_TYPE_RESTAURANT),
        ValueKeyBean("场馆", ResourceType.CONTENT_TYPE_VENUE),
        ValueKeyBean("娱乐场所", ResourceType.CONTENT_TYPE_ENTERTRAINMENT),
        ValueKeyBean("特产", ResourceType.CONTENT_TYPE_SPECIALTY)
    )
}