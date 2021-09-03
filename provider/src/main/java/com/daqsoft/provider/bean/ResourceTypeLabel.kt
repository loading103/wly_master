package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ResourceTypeLabel
 * @Author      PuHua
 * @Time        2020/2/24 16:49
 */
data class ResourceTypeLabel(
    var activityImgFirst: String,
    var activityImgSecond: String,
    // 标签图标
    var icon: String,
    // 标签 ID
    var id: String,
    // 标签名称
    var labelName: String,
    var name: String?=null,
    var type: String?=null,
    var value: String?=null

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