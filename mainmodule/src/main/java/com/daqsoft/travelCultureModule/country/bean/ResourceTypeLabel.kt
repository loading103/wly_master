package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :标签分类实体类
 * @author 江云仙
 * @date 2020/4/15 9:23
 */
data class ResourceTypeLabel(
    val activityImgFirst: String,
    val activityImgSecond: String,
    // 标签图标
    val icon: String,
    // 标签 ID
    val id: String,
    // 标签名称
    val labelName: String
) {
    var select = false
    /**
     * 子集合
     */
    var childList: MutableList<ResourceTypeLabel>? = null

    fun setSelects(s: Boolean): ResourceTypeLabel {
        this.select = s
        return this
    }

    override fun toString(): String {
        return labelName
    }
}