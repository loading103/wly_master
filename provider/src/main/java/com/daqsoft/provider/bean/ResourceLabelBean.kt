package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ResourceLabel
 * @Author      luoyi
 * @Time        2020/4/10 11:02
 */
data class ResourceLabelBean(
    /**
     * 资源Id
     */
    val id: Int,
    /**
     * 资源标签名称
     */
    val labelName: String,
    /**
     * 资源标签拥有者
     */
    val labelOwner: String,
    /**
     * 资源第一张图片
     */
    val activityImgFirst: String,
    /**
     * 资源第二张图片
     */
    val activtiyImgSecond: String,
    /**
     * 图片icon
     */
    val icon: String
)