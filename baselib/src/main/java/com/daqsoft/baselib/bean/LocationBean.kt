package com.daqsoft.baselib.bean

data class LocationBean(
    val acronym: String,
    val id: String,
    val lang: String,
    val latitude: String,
    val longitude: String,
    val memo: String,
    val name: String,
    val nlevel: Int,
    val parent: String,
    val pinyin: String,
    val region: String,
    val scort: String,
    val state: Int
) {
    override fun toString(): String {
        return name
    }
}

data class LocationData(
    val child: Boolean,
    val name: String,
    val region: String,
    var memo: String,
    val sub: MutableList<LocationData>
){
    override fun toString(): String {
        return name
    }
}
/**
 * @des 站点下级区域
 * @author PuHua
 * @Date 2020/1/9 11:36
 */
data class ChildRegion(
    // 地区全称
    val memo: String,
    // 地区简称
    val name: String,
    // 父级region
    val parent: String,
    // region
    var region: String,
    // 子级列表
    val subList: List<ChildRegion>?,
    // 是否是选中状态 0 表示未选中
    var selected:Int = 0,
    var siteId: String = ""

){
    var select = false

    fun setSelects(s :Boolean): ChildRegion {
        this.select = s
        return this
    }
    override fun toString(): String {
        return name
    }
}