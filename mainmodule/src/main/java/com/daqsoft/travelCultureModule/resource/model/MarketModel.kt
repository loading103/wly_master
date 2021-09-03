package com.daqsoft.travelCultureModule.resource.model

import com.daqsoft.mainmodule.R
import com.daqsoft.provider.base.ResourceType

/**
 * @Description 地图模式标记
 * @ClassName   MarketModel
 * @Author      luoyi
 * @Time       2020/3/17 09：40
 */
object MarketModel {

    /**
     * @param type String 类型
     *  获取标记图片REC
     */
    fun getMarketRec(type: String): Int {
        var marketImgRec = R.mipmap.map_scenic_normal
        when (type) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                marketImgRec = R.mipmap.map_scenic_normal
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                marketImgRec = R.mipmap.map_venue_normal
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                marketImgRec = R.mipmap.map_hotel_normal
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                marketImgRec = R.mipmap.map_food_normal
            }
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                marketImgRec = R.mipmap.map_universal_normal
            }
            ResourceType.CONTENT_TYPE_TOILET -> {
                marketImgRec = R.mipmap.map_toilet_normal
            }
            ResourceType.CONTENT_TYPE_PARKING -> {
                marketImgRec = R.mipmap.map_park_normal
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                marketImgRec = R.mipmap.map_country_normal
            }
            ResourceType.CONTENT_TYPE_MEDICAL -> {
                marketImgRec = R.mipmap.map_hospital_normal
            }
            ResourceType.TYPE_GAS_STATION -> {
                marketImgRec = R.mipmap.map_gasoline_normal
            }
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                marketImgRec = R.mipmap.map_rural_normal
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                marketImgRec = R.mipmap.map_shopping_normal
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                marketImgRec = R.mipmap.map_bus_normal
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                marketImgRec = R.mipmap.map_entertainment_normal
            }
            ResourceType.CONTENT_TYPE_RESEARCH_BASE -> {
                marketImgRec = R.mipmap.map_study_normal
            }
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                marketImgRec = R.mipmap.map_speciality_normal
            }
        }
        return marketImgRec
    }

    /**
     * @param type String 类型
     *  获取选中标记图片
     */
    fun getSelectedMarketRec(type: String): Int {
        var marketImgRec = R.mipmap.map_scenic_selected
        when (type) {
            ResourceType.CONTENT_TYPE_SCENERY -> {
                marketImgRec = R.mipmap.map_scenic_selected
            }
            ResourceType.CONTENT_TYPE_VENUE -> {
                marketImgRec = R.mipmap.map_venue_selected
            }
            ResourceType.CONTENT_TYPE_HOTEL -> {
                marketImgRec = R.mipmap.map_hotel_selected
            }
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                marketImgRec = R.mipmap.map_food_selected
            }
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                marketImgRec = R.mipmap.map_universal_selected
            }
            ResourceType.CONTENT_TYPE_TOILET -> {
                marketImgRec = R.mipmap.map_toilet_selected
            }
            ResourceType.CONTENT_TYPE_PARKING -> {
                marketImgRec = R.mipmap.map_park_selected
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                marketImgRec = R.mipmap.map_country_selected
            }
            ResourceType.CONTENT_TYPE_MEDICAL -> {
                marketImgRec = R.mipmap.map_hospital_selected
            }
            ResourceType.TYPE_GAS_STATION -> {
                marketImgRec = R.mipmap.map_gasoline_selected
            }
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                marketImgRec = R.mipmap.map_rural_selected
            }
            ResourceType.CONTENT_TYPE_SHOP_MALL -> {
                marketImgRec = R.mipmap.map_shopping_selected
            }
            ResourceType.CONTENT_TYPE_BUS_STOP -> {
                marketImgRec = R.mipmap.map_bus_selected
            }
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                marketImgRec = R.mipmap.map_entertainment_selected
            }
            ResourceType.CONTENT_TYPE_RESEARCH_BASE -> {
                marketImgRec = R.mipmap.map_study_selected
            }
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                marketImgRec = R.mipmap.map_speciality_selected
            }
        }
        return marketImgRec
    }
}