package com.daqsoft.itinerary.bean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/11 11:25
 * @Description：
 */
data class RecommFilterLabelBean(
    val id: String,
    val name: String,
    val code: String,
    var isSelected: Boolean,

    val fit: MutableList<RecommFilterLabelBean>,
    val personality: MutableList<RecommFilterLabelBean>,
    val playDay: MutableList<RecommFilterLabelBean>
)