package com.daqsoft.travelCultureModule.hotel.bean

/**
 * @Description
 * @ClassName   HotelLsTypeBean
 * @Author      luoyi
 * @Time        2020/5/29 20:47
 */
class HotelLsTypeBean {

    /**
     *星级
     */
    var level: String? = ""
    /**
     *  酒店类型
     */
    var type: String? = ""
    /**
     * 酒店设施
     */
    var eqt: String? = ""
    /**
     *   特色服务
     */
    var special: String? = ""


    fun setValue(typeCode: Int, id: String) {
        when (typeCode) {
            0 -> {
                level = id
            }
            1 -> {
                type = id
            }
            2 -> {
                eqt = id
            }
            3 -> {
                special = id
            }
        }
    }

    fun clearValue(typeCode: Int) {
        when (typeCode) {
            0 -> {
                level = ""
            }
            1 -> {
                type = ""
            }
            2 -> {
                eqt = ""
            }
            3 -> {
                special = ""
            }
        }
    }

    fun clearAll() {
        level = ""
        type = ""
        eqt = ""
        special = ""
    }
}