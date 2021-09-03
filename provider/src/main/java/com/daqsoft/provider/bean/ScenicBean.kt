package com.daqsoft.provider.bean

import android.os.Parcelable
import com.daqsoft.provider.network.venues.bean.AudioInfo
import kotlinx.android.parcel.Parcelize

/**
 * 景区实体
 */
@Parcelize
data class ScenicBean(
    // 活动
    val activity: List<ActivityBean> = mutableListOf(),
    val suggestedTime: String,
    var suggestedHour: Int,
    // 音频
    val audio: String,
    // 语音时长
    val audioTime: String,
    // 是否收费0不收费1收费
    val chargeStatus: String,
    // 金牌解说
    val goldStory: GoldStory,
    // id
    val id: String,
    // 图片
    val images: String,
    // 介绍
    val Stringroduce: String,
    // 纬度
    val latitude: String,
    // 等级
    val level: String,
    // 经度
    val longitude: String,
    // 景区名称
    val name: String,
    //景区地址
    val address: String,
    // 开放结束时间
    val openTimeEnd: String,
    // 开放开始时间
    val openTimeStart: String,
    // 是否可以预订0不可预订1可预订
    val orderStatus: String,
    // 720,链接
    val panoramaUrl: String,
    // 是否推荐首页0否
    val recommendHomePage: String,
    // 地区名称
    val regionName: String,
    // 备注
    val remarks: String,
    // 资源编码
    val resourceCode: String,
    // 浏览量
    val srs: String,
    // 概述
    val summary: String,
    //主题
    val theme: MutableList<String>,
    // 视频
    val video: String,
    // 资源状态
    val vipResourceStatus: VipResourceStatus,
    val briefing: String,
    val resourceType: String,
    //是否选中
    var isChecked: Boolean,
    var dataId: Int,
    var type: String,
    var todayOrderStatus: Int,
    var suprsNum: Int,
    val contentDataList: MutableList<ContentVoBean>? = mutableListOf(),
    // 景点数据
    var spots: MutableList<Spots> = mutableListOf(),
    var externalIsOpen: Boolean,

    // 开放结束时间
    val openEndTime: String,
    // 开放开始时间
    val openStartTime: String,

    val consumPerson: String,

    val activityInfo: String,
    var isOpen: String
) : Parcelable

/**
 * 金牌解说和音频公用
 */
@Parcelize
class GoldStory(
    // 金牌解说链接
    val link: String,
    // 解说音频地址
    val turl: String,
    // 音频时长
    val audioTime: String,
    // 音频地址
    val audio: String
) : Parcelable

/**
 * 景点数据
 */
@Parcelize
data class Spots(
    // 景点id
    val id: String?,
    // 图片
    val images: String?,
    // 景点名称
    val name: String?,
    // 是否拍摄点1是 0不是
    val shootStatus: String?,
    // 介绍
    val introduce: String?,
    // 直播地址
    val liveUrl: String?,
    // 720全景
    val panoramaUrl: String?,
    var panoramaCover: String? = "",
    // 简介
    val summary: String?,
    // 音频
    val audio: String?,
    // 音频信息
    val audioInfo: List<AudioInfo>? = mutableListOf(),
    // 音频时间
    val audioTime: String?,
    // 游客中心结束时间
    val centerTimeEnd: String?,
    // 游客中心开始时间
    val centerTimeStart: String?,
    // 地区编码
    val cutRegionName: String?,
    // 海拔
    val elevation: String?,
    // 金牌解说
    val goldStory: GoldStory?,
// 纬度
    val latitude: Double,
// 经度
    val longitude: Double,
// 闭园时间
    val openTimeEnd: String?,
    // 开放时间
    val openTimeStart: String?,
// 地区编码
    val region: String?,
    // 资源编码
    val resourceCode: String?,
    // 景区等级
    val scenicLevel: String?,
    // 景区名称
    val scenicName: String?,
    // 拍摄示例 图片
    val shootImgExample: String?,
    // 拍摄点介绍
    val shootPointIntroduce: String?,
    // 拍摄时间
    val shootTime: String?,
    // 建议游玩时间
    val suggestedTime: String?,
// 票务政策
    val ticketPolicy: String?,
    // 天气
    val weather: Weather?,
    val radiationDis: String?,
    val phone: String?,
    /**
     * 景区id
     */
    var scenicId: String?,
    // 地址
    var address: String,
    // 概况
    var briefing: String,
    // 备注
    var remarks: String,
    // 乡村id
    var ruralId: Int,
    // 站点id
    var siteId: Int
) : Parcelable

/**
 * 景区标签
 */
@Parcelize
data class ScenicTags(
    var level: String?,
    var spotNum: String?,
    var tags: String?
) : Parcelable


/**
 * 景区详情实体
 */
@Parcelize
data class ScenicDetailBean(
    // 相关活动
    var activity: List<ActivityBean> = mutableListOf(),
    // 音频
    var audio: String?,
    // 音频信息
    val audioInfo: MutableList<AudioInfo> = mutableListOf(),
    // 音频时间
    val audioTime: String,
    // 最佳游览时间
    val bestTravelTime: String,
    // 收费状态1收费，0免费
    val chargeStatus: String,
    // 收藏数量
    var collectionNum: String,
    // 评论数量
    val commentNum: String,
    // 适合人群
    val crowd: List<String>?,
    // 地区编码拼接名称
    val cutRegionName: String,
    // 海拔
    val elevation: String,
    // 地理文化
    val geogCulture: String,
    // 金牌解说
    val goldStory: GoldStory,
    // 数据id
    val id: String,
    // 图片
    val images: String,
    // 介绍
    val Stringroduce: String,
    // 纬度
    val latitude: String,
    // 等级
    val level: String,
    // 点赞数量
    var likeNum: String,
    // 经度
    val longitude: String,
    // 最大承载量
    val maxNum: String,
    // 名称
    val name: String,
    // 闭园时间
    val openEndTime: String,
    // 开放时间
    val openTimeStart: String,
    // 720全景
    val panoramaUrl: String,
    /**
     * 720封面
     */
    var panoramaCover: String? = "",
    // 地区编码
    val region: String,
    // 详细地址
    val regionName: String,
    // 资源编码
    var resourceCode: String,
    // 线路
    val route: String,
    // 景点
    val scenicSpots: String,
    // 店铺名称
    val shopName: String,
    // 购买链接
    val shopUrl: String,
    // 建议游玩时间
    val suggestedTime: String,
    // 概况
    val summary: String,
    // 小时
    val suggestedHour: String,
    // 电商code
    val sysCode: String,
    // 主题
    val theme: List<String> = ArrayList<String>(),
    // 票务政策
    val ticketPolicy: String,
    // 交通
    val trafficInfo: String,
    // 视频
    val video: String,
    // 资源状态
    val vipResourceStatus: VipResourceStatus,
    // 天气
    val weather: Weather,
    // 介绍
    val introduce: String,
    // 手机电话
    val phone: String,
    // 官网地址
    val websiteUrl: String,
    // 公众号地址
    val officialUrl: String,
    // 公众号名称
    val officialName: String,
    // 下单地址
    val orderAddressType: String,
    /**
     * 游客中心图片
     */
    val centerImage: String,
    /**
     * 游客中心电话号码
     */
    val centerPhone: String,
    /**
     * 游客中心地址
     */
    val centerAddress: String,
    val centerLatitude: String,
    val centerLongitude: String,
    var tour: TourBean?,
    var briefing: String,
    var isOpen: Int,

    var themeForA: String?,
    var mien: String?,
    var scenicHealthCode: String?
) : Parcelable{
    fun getadresses():String{
        if(regionName?.isNullOrBlank()){
            return ""
        }else{
            return "景区地址： ${regionName}"
        }
    }
    fun getOpenTimes():String{
        if(openTimeStart?.isNullOrBlank()){
            return ""
        }else{
            return "$openTimeStart-$openEndTime"
        }
    }
}


/**
 * 天气
 */
@Parcelize
data class Weather(
    // 最高温度
    val max: String,
    // 最低温度
    val min: String,
    // 图片
    val pic: String,
    // 说明
    val txt: String,
    // 编码
    val unicode: String
) : Parcelable

/**
 * 景区详情 导游导览数据
 */
@Parcelize
data class TourBean(
    var images: String?,
    var parkNum: Int? = 0,
    var spotsNum: Int? = 0,
    var type: Int? = 0,
    var toiletNum: Int? = 0,
    var id: String?,
    var routeNum: Int? = 0,
    var name: String? = ""
) : Parcelable

/**
 * 景区详情 导游导览数据
 */
@Parcelize
data class TourHomeBean(
    var images: String?,
    var parkingNum: Int? = 0,
    var placeNum: Int? = 0,
    var type: Int? = 0,
    var toiletNum: Int? = 0,
    var id: String?,
    var lineNum: Int? = 0,
    var name: String? = ""
) : Parcelable

/**
 * 景点 实景直播列表
 */
data class SpotsLiveBean(
    /**
     * 图片
     */
    var images: String,
    /**
     * 播放地址
     */
    var url: String,
    /**
     * 名称
     */
    var name: String,
    /**
     * 数据id
     */
    var id: Int
)

@Parcelize
data class ContentVoBean(
    val author: String,
    val channelName: String,
    val commentNum: Int,
    val contentType: String,
    val createCompany: String,
    val createCompanyLogo: String,
    val id: Int,
    val likeNum: Int,
    val publishTime: String,
    val recommendChannelHomePage: Int,
    val recommendHomePage: Int,
    val showNum: Int,
    val sort: Int,
    val status: Int,
    val summary: String,
    val title: String,
    val top: Int
) : Parcelable

