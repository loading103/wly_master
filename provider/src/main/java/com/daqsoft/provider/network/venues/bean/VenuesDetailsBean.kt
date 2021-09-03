package com.daqsoft.provider.network.venues.bean

import android.os.Parcelable
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.ActivityRoomBean
import kotlinx.android.parcel.Parcelize

/**
 * 文化场馆详情实体类
 * @author 黄熙
 * @date 2020/1/21 0021
 * @version 1.0.0
 * @since JDK 1.8
 */
class VenuesDetailsBean(
    /**
     * 场馆名称
     */
    val name: String,
    /**
     * 电话
     */
    val phone: String,
    /**
     * ID
     */
    val id: Int,
    /**
     * 收藏数量
     */
    var collectionNum: Int,
    /**
     * 纬度
     */
    val latitude: String,
    /**
     * 经度
     */
    val longitude: String,
    /**
     * 地区名称
     */
    val regionName: String,
    /**
     * 视频
     */
    val video: String,
    /**
     * 类型
     */
    val type: String,
    /**
     * 点赞数量
     */
    var likeNum: Int,
    /**
     * 支付规则
     */
    val payRule: String,
    /**
     * 语音
     */
    val audio: String,
    /**
     * 标签
     */
    val tag: String,
    /**
     * 官网地址
     */
    val websiteUrl: String,
    /**
     * 标签名称
     */
    val tagName: List<String>,
    /**
     * 虚拟体验720
     */
    val panoramaUrl: String,
    var panoramaCover:String?="",
    /**
     * 图片
     */
    val images: String,
    /**
     * 场馆简介
     */
    val summary: String,
    /**
     *介绍
     */
    val introduce: String,
    /**
     *交通信息
     */
    val trafficInfo: String,
    /**
     *开放时间
     */
    var openWeek: String?,
    /**
     * 评论数量
     */
    val commentNum: Int,
    var venueLevel: String,
    /**
     * 公众号名称
     */
    val officialName: String,
    /**
     * 公众号地址
     */
    val officialUrl: String,
    /**
     * 在线阅读地址
     */
    val onlineReadingImage: String,
    /**
     * 资源状态
     */
    val vipResourceStatus: VipResourceStatus,
    /**
     * 音频
     */
    val audioInfo: MutableList<AudioInfo>,
    /**
     * 活动
     */
    val activity: MutableList<ActivityBean>,
    /**
     * 活动室
     */
    val activityRoom: MutableList<ActivityRoomBean>,
    /**
     * 在线阅读名称
     */
    val onlineReadingOfficialName: String,
    /**
     * 是否开馆
     */
    val isOpen: Int,
    var orderAddressType:String
)

class VipResourceStatus(
    /**
     * 收藏状态
     */
    var collectionStatus: Boolean,
    /**
     * 点赞状态
     */
    var likeStatus: Boolean
)

@Parcelize
class AudioInfo() : Parcelable {
    /**
     * 语音
     */

    var audio: String? = null
    /**
     *  地址
     */
    var url: String? = null
    /**
     *  总时长
     */
    var time: String? = null
    /**
     *  封面图
     */
    var cover: String? = null
    /**
     * 名称
     */
    var name: String? = null
    /**
     * 播放状态 0 未播放 1 播放中 2暂停
     */
    var state: Int = 0
    var progress: Int = 0
    var duration: Int = 0
    /**
     * 1为金牌解说
     */
    var type: Int = 0
    /**
     * 金牌解说地址
     */
    var linkUrl: String? = null

    constructor(type: Int, linkUrl: String, audio: String) : this() {
        this.type = type
        this.linkUrl = linkUrl
        this.audio = audio
    }

    constructor(audio: String, time: String) : this() {
        this.time = time
        this.audio = audio
    }
}