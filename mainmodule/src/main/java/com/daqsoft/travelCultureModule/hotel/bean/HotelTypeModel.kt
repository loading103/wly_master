package com.daqsoft.travelCultureModule.hotel.bean

import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueKeyBean

/**
 * @Description
 * @ClassName   HotelTypeModel
 * @Author      luoyi
 * @Time        2020/5/29 20:49
 */
object HotelTypeModel {
    // 排序
    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("可订优先", "orderStatus"),
        ValueKeyBean("距离优先", "disNum"),
        ValueKeyBean("推荐优先", "recommendHomePage"),
        ValueKeyBean("人气优先", "hot"),
        ValueKeyBean("好评优先", "commentLevel")
    )
    // 排序
    val scenicLevel = mutableListOf(
        ResourceTypeLabel("", "", "", "", "全部").setSelects(true),
        ResourceTypeLabel("", "", "", "五星级", "五星级"),
        ResourceTypeLabel("", "", "", "四星级", "四星级"),
        ResourceTypeLabel("", "", "", "三星级", "三星级"),
        ResourceTypeLabel("", "", "", "二星级", "二星级"),
        ResourceTypeLabel("", "", "", "一星级", "一星级"),
        ResourceTypeLabel("", "", "", "金树叶级绿色饭店", "金树叶级绿色饭店"),
        ResourceTypeLabel("", "", "", "银树叶级绿色饭店", "银树叶级绿色饭店"),
        ResourceTypeLabel("", "", "", "金鼎级文化主题旅游饭店", "金鼎级文化主题旅游饭店"),
        ResourceTypeLabel("", "", "", "银鼎级文化主题旅游饭店", "银鼎级文化主题旅游饭店"),
        ResourceTypeLabel("", "", "", "三星级农家乐", "三星级农家乐"),
        ResourceTypeLabel("", "", "", "四星级农家乐", "四星级农家乐"),
        ResourceTypeLabel("", "", "", "五星级农家乐", "五星级农家乐"),
        ResourceTypeLabel("", "", "", "一级主题饭店", "一级主题饭店"),
        ResourceTypeLabel("", "", "", "二级主题饭店", "二级主题饭店"),
        ResourceTypeLabel("", "", "", "三级主题饭店", "三级主题饭店"),
        ResourceTypeLabel("", "", "", "四级主题饭店", "四级主题饭店"),
        ResourceTypeLabel("", "", "", "五级主题饭店", "五级主题饭店")

    )
    // 第一层排序
    val firstType = mutableListOf(
        ResourceTypeLabel("", "", "", "level", "酒店星级").setSelects(true),
        ResourceTypeLabel("", "", "", "type", "酒店类型"),
        ResourceTypeLabel("", "", "", "eqt", "酒店设施"),
        ResourceTypeLabel("", "", "", "special", "特色服务")
    )
}