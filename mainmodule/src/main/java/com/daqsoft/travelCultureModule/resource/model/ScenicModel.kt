package com.daqsoft.travelCultureModule.resource.model

import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @Description 景区列表静态资源
 * @ClassName   ScenicModel
 * @Author      luoyi
 * @Time        2020/3/30 14:13
 */
object ScenicModel {
    // 排序
    val sorts = mutableListOf(
        ValueKeyBean("不限", "").apply {
            select = true
        },
        ValueKeyBean("推荐排序", "recommendHomePage"),
        ValueKeyBean("热度排行", "hot"),
        ValueKeyBean("距离排行", "disNum"),
        ValueKeyBean("可订优先", "isOpen")
    )
    // 排序
    val scenicLevel = mutableListOf(
        ResourceTypeLabel("", "", "", "", "不限").apply {
            select = true
        },
        ResourceTypeLabel("", "", "", "AAAAA", "AAAAA"),
        ResourceTypeLabel("", "", "", "AAAA", "AAAA"),
        ResourceTypeLabel("", "", "", "AAA", "AAA"),
        ResourceTypeLabel("", "", "", "AA", "AA"),
        ResourceTypeLabel("", "", "", "A", "A")
    )
    // 第一层排序
    val firstType = mutableListOf(
        ResourceTypeLabel("", "", "", "level", "景区等级").setSelects(true),
        ResourceTypeLabel("", "", "", LabelType.SCENIC_THEME, "景区主题"),
        ResourceTypeLabel("", "", "", "crowd", "景区人群")
    )

    // 第一层排序(研学基地)
    val firstStudyType = mutableListOf(
        ResourceTypeLabel("", "", "", "scenicLevel", "景区等级").setSelects(true),
        ResourceTypeLabel("", "", "", "tag", "基地标签"),
        ResourceTypeLabel("", "", "", "crowd", "基地人群")
    )



    // 第一层排序(研学基地)
    val firstSpecialType = mutableListOf(
        ResourceTypeLabel("", "", "", "type", "特产类型").setSelects(true),
        ResourceTypeLabel("", "", "", "tag", "特产标签")
    )
}