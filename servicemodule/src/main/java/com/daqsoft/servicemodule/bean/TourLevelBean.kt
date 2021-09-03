package com.daqsoft.servicemodule.bean

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/2 11:25
 * @version 1.0.0
 * @since JDK 1.8
 */
data class TourLevelBean(
    //等级名称
    var name: String,
    //等级标识
    var level: String

){
    override fun toString(): String {
        return name
    }
}
