package com.daqsoft.travelCultureModule.redblack.bean

/**
 * 红黑榜数据
 */
data class AreaListBean(
    val datas: MutableList<AreaListBeanItem>
)
data class AreaListBeanItem(
    val agr: Double,
    val dining: Double,
    val hotel: Double,
    val image: String,
    val region: String,
    val regionName: String,
    val scenic: Double,
    val totalAvg: Double
)