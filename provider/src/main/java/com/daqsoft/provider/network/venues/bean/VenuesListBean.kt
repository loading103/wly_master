package com.daqsoft.venuesmodule.repository.bean

import android.os.Parcelable
import com.daqsoft.provider.bean.ContentBean
import kotlinx.android.parcel.Parcelize

/**
 * 文化场馆列表
 * @author 黄熙
 * @date 2019/12/25 0025
 * @version 1.0.0
 * @since JDK 1.8
 */
@Parcelize
data class VenuesListBean(
    /**资源数据ID*/
    var dataId: Int,
    /**资源id*/
    var resourceId: Int,
    /**资源类型 */
    var resourceType: String,
    var suggestedTime: String?,
    var suggestedHour: Int,
    /*************以上是附加属性，用于添加到行程中参数******************/


    /**
     * 类型
     */
    var type: String,

    var resType: String,

    /**
     * 纬度
     */
    val latitude: String,
    /**
     * 经度
     */
    val longitude: String,
    /**
     * 开放时间
     */
    val openWeek: String,
    /**
     * 场馆概述
     */
    val briefing: String,
    /**
     * 视频
     */
    val video: String,
    /**
     * 720地址
     */
    val panoramaUrl: String,
    /**
     * 音频
     */
    val audio: String,
    /**
     * 	音频时间
     */
    val audioTime: String,
    /**
     * 标签名称
     */
    val tagName: List<String>,
    /**
     * 相关活动
     */
    val activityInfo: MutableList<ActivityInfo>,
    /**
     * 地区编码
     */
    val regionName: String,
    /**
     * 图片
     */
    val images: String,
    /**
     * 	标签
     */
    val tag: String,
    /**
     * 地址
     */
    val address: String,
    /**
     * 地区编码
     */
    val region: String,
    /**
     * 名称
     */
    val name: String,
    /**
     * 场馆id
     */
    val id: String,
    var isChecked: Boolean,
    /**
     * 资源状态
     */
    val vipResourceStatus: VipResourceStatus?,
    val venueLevel: String,
    /**
     *可预订活动室信息
     */
    val orderRoomInfo: List<OrderRoomInfo> = mutableListOf(),
    val suprsNum: Int,
    val todayOrderStatus: Int,
    val guideIsOpen: Int,
    var isOpen:Int,
    val externalIsOpen: Boolean,
    val contentDataList: MutableList<ContentVenueBean> = mutableListOf()

) : Parcelable

@Parcelize
class ContentVenueBean(
    var title: String
) : Parcelable

/**
 * 资源状态
 */
@Parcelize
data class VipResourceStatus(
    /**
     * 收藏状态
     */
    var collectionStatus: Boolean,
    /**
     * 点赞状态
     */
    var likeStatus: Boolean
) : Parcelable

/**
 * 可预订活动室信息
 */
@Parcelize
data class OrderRoomInfo(
    /**
     *活动室id
     */
    val id: Int,
    /**
     *活动室图片
     */
    val img: String,
    /**
     *	活动室名称
     */
    val name: String

) : Parcelable

@Parcelize
class ActivityInfo(
    /**
     * 图片
     */
    val images: String,
    /**
     * 地址
     */
    val address: String,
    /**
     * 结束时间
     */
    val endDate: String,
    /**
     * 开始时间
     */
    val startDate: String,
    /**
     * ID
     */
    val id: String,
    /**
     * 活动名称
     */
    val name: String,
    /**
     * 活动方式
     */
    val method: String,
    /**
     * 活动类型
     */
    val type: String,
    /**
     * 	活动诚信优享
     */
    val faithUseStatus: Int,
    /**
     * 活动诚信免审
     */
    val faithAuditStatus: Int,
    /**
     * 跳转地址
     */
    val jumpUrl: String,
    /**
     * 	价格
     */
    val money: String,
    /**
     * 积分
     */
    val integral: String,
    /**
     * 活动状态
     */
    val activityStatus: Int
) : Parcelable
