package com.daqsoft.travelCultureModule.food.model

import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueIdKeyBean

/**
 * @Description 视频模型
 * @ClassName   FoodModel
 * @Author      luoyi
 * @Time        2020/4/10 10:07
 */
object FoodModel {

    fun getTypes(): MutableList<ValueIdKeyBean> {
        var list: MutableList<ValueIdKeyBean> = mutableListOf()
        list.add(ValueIdKeyBean("其它", 163))
        list.add(ValueIdKeyBean("徽菜", 164))
        list.add(ValueIdKeyBean("福建菜", 165))
        list.add(ValueIdKeyBean("蒙餐", 166))
        list.add(ValueIdKeyBean("大闸蟹", 167))
        list.add(ValueIdKeyBean("客家菜", 168))
        list.add(ValueIdKeyBean("台湾菜", 163))
        list.add(ValueIdKeyBean("网红打卡", 163))
        list.add(ValueIdKeyBean("湘菜", 163))
        list.add(ValueIdKeyBean("云贵菜", 163))
        list.add(ValueIdKeyBean("东北菜", 163))
        list.add(ValueIdKeyBean("西北菜", 163))
        list.add(ValueIdKeyBean("江浙菜", 163))
        list.add(
            ValueIdKeyBean(
                "新疆菜",
                163
            )
        )
        list.add(ValueIdKeyBean("鲁菜", 163))
        list.add(ValueIdKeyBean("京菜", 163))
        list.add(ValueIdKeyBean("东南亚菜", 163))
        list.add(ValueIdKeyBean("生鲜蔬果", 163))
        list.add(ValueIdKeyBean("粤菜", 163))
        list.add(ValueIdKeyBean("创意菜", 163))
        list.add(ValueIdKeyBean("素食", 163))
        list.add(ValueIdKeyBean("私房菜", 163))
        list.add(ValueIdKeyBean("特色菜", 163))
        list.add(ValueIdKeyBean("日本菜", 163))
        list.add(ValueIdKeyBean("家常菜", 163))
        list.add(ValueIdKeyBean("食品保健", 163))
        list.add(ValueIdKeyBean("粉面馆", 163))
        list.add(ValueIdKeyBean("小龙虾", 163))
        list.add(ValueIdKeyBean("串串香", 163))
        list.add(ValueIdKeyBean("日韩料理", 163))
        list.add(ValueIdKeyBean("江河湖海鲜", 163))
        list.add(ValueIdKeyBean("茶馆", 163))
        list.add(ValueIdKeyBean("咖啡厅", 163))
        list . add (ValueIdKeyBean("西餐", 163))
        list . add (ValueIdKeyBean("自助餐", 163))
        list . add (ValueIdKeyBean("烤鱼", 163))
        list . add (ValueIdKeyBean("烧烤", 163))
        list . add (ValueIdKeyBean("饮品店", 163))
        list . add (ValueIdKeyBean("面包甜点", 163))
        list . add (ValueIdKeyBean("川菜", 163))
        list . add (ValueIdKeyBean("火锅", 163))
        list . add (ValueIdKeyBean("小吃快餐", 163))
        return list
    }

    fun getFOodsTypes(
    ) :MutableList<ResourceTypeLabel>{
        var temp:MutableList<ResourceTypeLabel> = mutableListOf()
        temp.add(ResourceTypeLabel("", "", "", "1", "餐厅"))
        return  temp
    }

}