package com.daqsoft.itinerary.bean

import com.contrarywind.interfaces.IPickerViewData

/**
 * @Author：      邓益千
 * @Create by：   2020/5/30 16:20
 * @Description： 省份Bean
 */
data class ProvinceBean (
    val name: String,
    val region: String,
    val child: Boolean,
    val sub: MutableList<ProvinceBean>
) : IPickerViewData {
    override fun getPickerViewText(): String {
        return name
    }
}