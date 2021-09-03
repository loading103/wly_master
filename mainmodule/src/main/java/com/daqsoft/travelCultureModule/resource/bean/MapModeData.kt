package com.daqsoft.travelCultureModule.resource.bean

import com.daqsoft.provider.base.ResourceType

/**
 * @Description
 * @ClassName   MapModeData
 * @Author      luoyi
 * @Time        2020/4/23 17:58
 */
object MapModeData {

    /**
     * 获取地图模式-数据集
     */
    fun mapModeData(): MutableList<MapModeTabBean> {
        var datas: MutableList<MapModeTabBean> = mutableListOf()
        datas.add(MapModeTabBean("景区", ResourceType.CONTENT_TYPE_SCENERY))
        datas.add(MapModeTabBean("场馆", ResourceType.CONTENT_TYPE_VENUE))
        datas.add(MapModeTabBean("酒店", ResourceType.CONTENT_TYPE_HOTEL))
        datas.add(MapModeTabBean("美食", ResourceType.CONTENT_TYPE_RESTAURANT))
        datas.add(MapModeTabBean("乡村", ResourceType.CONTENT_TYPE_COUNTRY))
        datas.add(MapModeTabBean("农家乐", ResourceType.CONTENT_TYPE_AGRITAINMENT))
        datas.add(MapModeTabBean("娱乐场所", ResourceType.CONTENT_TYPE_ENTERTRAINMENT))
//        datas.add(MapModeTabBean("医疗点", ResourceType.CONTENT_TYPE_MEDICAL))
        datas.add(MapModeTabBean("乘车点", ResourceType.CONTENT_TYPE_BUS_STOP))
        datas.add(MapModeTabBean("购物点", ResourceType.CONTENT_TYPE_SHOP_MALL))
        datas.add(MapModeTabBean("研学基地", ResourceType.CONTENT_TYPE_RESEARCH_BASE))
        datas.add(MapModeTabBean("特产", ResourceType.CONTENT_TYPE_SPECIALTY))
        return datas
    }
}