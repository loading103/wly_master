package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :本地数据对应的值
 * @author 江云仙
 * @date 2020/4/14 19:44
 */
 data class ValueKeyBean(
    val name: String,
    val value: String,
    var select: Boolean = false
) {
    override fun toString(): String {
        return name
    }
}
/**
 */
data class ValueIdKeyBean(
    val name: String,
    var id: Int
)