package com.daqsoft.provider.network.home.bean

/**
 * 被投诉方对象
 * @author 黄熙
 * @date 2020/3/1 0001
 * @version 1.0.0
 * @since JDK 1.8
 */
data class ComplaintResourceBean(
    /**
     * 地址
     */
    val address: String,
    /**
     * 内容类型
     */
    val contentType: String,
    val floorPrice: String,
    val image: String,
    val jumpUrl: String,
    val latitude: String,
    val longitude: String,
    /**
     * 名称
     */
    val name: String,
    val orderStatus: String,
    /**
     * 地区编码
     */
    val region: String,
    /**
     * 资源ID
     */
    val resourceId: String,
    /**
     * 资源类型
     */
    val resourceType: String,
    val siteId: Int,
    val summary: String
)