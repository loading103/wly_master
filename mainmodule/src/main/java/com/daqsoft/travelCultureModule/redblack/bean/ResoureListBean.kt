package com.daqsoft.travelCultureModule.redblack.bean

/**
 * 红黑榜数据
 */
data class ResoureListBean(
    val datas: MutableList<ResoureListBeanItem>
)
data class ResoureListBeanItem(
    val commentNum: String,
    val id: Int,
    val images: String,
    val level: String,
    val name: String,
    val numAvg: String,
    val type: String
)