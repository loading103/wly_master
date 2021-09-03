package com.daqsoft.provider.bean

import android.os.Parcelable
import android.view.View
import com.daqsoft.provider.network.venues.bean.AudioInfo
import kotlinx.android.parcel.Parcelize

/**
 * 景区实体
 */
@Parcelize
data class ResearchBean(
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
 * 景区详情实体
 */
@Parcelize
data class ResearchDetailBean(
    // 相关活动
    var activity: List<ActivityBean> ?= mutableListOf(),
    // 音频
    var audio: String?,
    // 音频信息
    val audioInfo: MutableList<AudioInfo> ?=mutableListOf(),
    // 音频时间
    val audioTime: String ?,
    // 最佳游览时间
    val bestTravelTime: String?,
    // 收费状态1收费，0免费
    val chargeStatus: String?,
    // 收藏数量
    var collectionNum: String?,
    // 评论数量
    val commentNum: String?,
    // 适合人群
    val crowd: List<String>?,
    // 地区编码拼接名称
    val cutRegionName: String ?= null,
    // 海拔
    val elevation: String?,
    // 地理文化
    val geogCulture: String?,
    // 金牌解说
    val goldStory: GoldStory ?=null,
    // 数据id
    val id: String?,
    // 图片
    val images: String?,
    // 介绍
    val Stringroduce: String?,
    // 纬度
    val latitude: String?,
    // 等级
    val level: String?,

    val scenicLevel: String?,
    // 点赞数量
    var likeNum: String?,
    // 经度
    val longitude: String?,
    // 最大承载量
    val maxNum: String?,
    // 名称
    val name: String?,
    // 闭园时间
    val openEndTime: String?,
    // 开放时间
    val openTimeStart: String?,
    // 720全景
    val panoramaUrl: String?,
    /**
     * 720封面
     */
    var panoramaCover: String? = "",
    // 地区编码
    val region: String?,
    // 详细地址
    val regionName: String?,
    // 资源编码
    var resourceCode: String?,
    // 线路
    val route: String?,
    // 景点
    val scenicSpots: String?,
    // 店铺名称
    val shopName: String?,
    // 购买链接
    val shopUrl: String?,
    // 建议游玩时间
    val suggestedTime: String?,
    // 概况
    val summary: String?,
    // 小时
    val suggestedHour: String?,
    // 电商code
    val sysCode: String?,
    // 主题
    val theme: List<String> ?= ArrayList<String>(),
    // 票务政策
    val ticketPolicy: String?,
    // 交通
    val trafficInfo: String?,
    // 视频
    val video: String?,
    // 资源状态
    val vipResourceStatus: VipResourceStatus?=null,
    // 天气
    val weather: Weather?,
    // 介绍
    val introduce: String?,
    // 手机电话
    val phone: String?,
    // 官网地址
    val websiteUrl: String?,
    // 公众号地址
    val officialUrl: String?,
    // 公众号名称
    val officialName: String?,
    // 下单地址
    val orderAddressType: String?,
    /**
     * 游客中心图片
     */
    val centerImage: String?,
    /**
     * 游客中心电话号码
     */
    val centerPhone: String?,
    /**
     * 游客中心地址
     */
    val centerAddress: String?,
    val centerLatitude: String?,
    val centerLongitude: String?,
    var tour: TourBean?,
    var briefing: String?,
    var isOpen: Int?,

    var scenicHealthCode: String?,
    var themeForA: String?,
    var tag: MutableList<String>?,
    var mien: String?

) : Parcelable{
    fun getadresses():String{
        if(regionName.isNullOrBlank()){
            return ""
        }else{
            return "基地地址： ${regionName}"
        }
    }

    fun getOpenTimes():String{
        if(openTimeStart.isNullOrBlank()){
            return ""
        }else{
            return "$openTimeStart-$openEndTime"
        }
    }
    fun getChaVisible():Int{
        if(phone.isNullOrBlank() && openTimeStart.isNullOrBlank() && ticketPolicy.isNullOrBlank()){
            return View.GONE
        }else{
            return View.VISIBLE
        }
    }

    fun getTags():String{
        if(tag.isNullOrEmpty()){
            return " "
        }else{
            var sb=StringBuffer()
            tag?.forEach {
                sb.append("$it、")
            }
            return sb.toString().substring(0,sb.length-1)
        }
    }

}
