package com.daqsoft.provider.bean

/**
 * @Description 本地数据对应的值
 * @ClassName   ValueKeyBean
 * @Author      PuHua
 * @Time        2020/1/9 15:34
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
 * @Description 本地数据对应的值
 * @ClassName   ValueIdKeyBean
 * @Author      luoyi
 * @Time        2020/3/28 17:09
 */
data class ValueIdKeyBean(
    val name: String,
    var id: Int
) {

}

/**
 * @Description 本地数据对应的值
 * @ClassName   ValueResBean
 * @Author      luoyi
 * @Time        2020/9/7 11点09分
 */
data class ValueResBean(
    val name: String,
    val value: String,
    var res: Int
) {
}