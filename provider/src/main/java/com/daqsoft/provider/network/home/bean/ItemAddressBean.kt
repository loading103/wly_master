package com.daqsoft.provider.network.home.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 搜索地点的item布局实体类
 * @author 黄熙
 * @date 2020/2/19 0019
 * @version 1.0.0
 * @since JDK 1.8
 */
@Parcelize
class ItemAddressBean(
    /**
     * 资源ID
     */
    var resourceId: Int,
    /**
     * 资源类型
     */
    var resourceType: String,
    /**
     * 类型内容
     */
    var contentType: String,
    /**
     * 资源名称
     */
    var name: String,
    /**
     * 经度
     */
    var longitude: Double,
    /**
     * 纬度
     */
    var latitude: Double,
    /**
     * 地址
     */
    var address: String?,
    /**
     * 图片资源
     */
    var image: String?,
    /**
     * 简介
     */
    var summary: String?,
    /**
     * 跳转URL
     */
    var jumpUrl: String?,
    /**
     * 排序状态
     */
    var orderStatus: String?,
    /**
     * 最低价格
     */
    var floorPrice: String?,
    /**
     * 地区编码
     */
    var region: String?,
    /**
     * 站点ID
     */
    var siteId: Int,
    /**
     * 列表选中状态
     */
    var status: Boolean

) : Parcelable