package com.daqsoft.provider.bean

import com.daqsoft.provider.network.venues.bean.AudioInfo
import retrofit2.http.Url
import java.math.BigDecimal
import java.util.*

/**
 * 酒店数据
 */
data class HotelBean(
    // 相关活动
    val activity: List<ActivityBean>,
    // 音频
    val audio: String,
    // 音频时间
    val audioTime: String,
    // 设备
    val eqt: String,
    // 最低价格
    val floorPrice: String,
    // 好评数量
    val goodCommentNum: Int,
    // 酒店等级
    val hotelLevel: String,
    // 数据id
    val id: Int,
    // 图片
    val images: String,
    // 纬度
    val latitude: String,
    // 点赞数量
    val likeNum: Int,
    // 经度
    val longitude: String,
    // 名称
    val name: String,
    // 720
    val panoramaUrl: String,
    // 是否推荐
    val recommendHomePage: Int,
    // 地区编码
    val region: String,
    // 地址
    val regionName: String,
    // 4A以上景区在3公里以内的数据
    val scenic: Any,
    // 浏览量
    val srs: Int,
    // 类型
    val type: List<String>,
    // 视频
    val video: String,
    // 视频封面图
    val videoCover: String,
    // 资源状态
    val vipResourceStatus: VipResourceStatus
)

/**
 * 酒店详情
 */
class HotelDetailBean(
    // 相关活动
    val activity: List<ActivityBean>,
    // 地区编码
    val region: String,
    // 酒店等级
    val hotelLevel: String,
    // 名称
    val name: String,

    val briefing: String,
    // 电话
    val phone: String,
    // 音频时间
    val audioTime: String,
    // 装修时间
    val decorationTime: String,
    // 退房时间
    val checkOutTime: String,
    // 交通信息
    val trafficInfo: String,
    // 评论数量
    val commentNum: String,
    // 购买地址
    val shopUrl: String,
    // 订单地址类型
    val orderAddressType: String,
    // 入住时间
    val checkInTime: String,
    // 介绍
    val introduce: String,
    // 资源状态
    val vipResourceStatus: VipResourceStatus,
    //  图片地址
    val images: String,
    // 简介
    val summary: String,
    // 经度
    val longitude: String,
    // 720地址
    val panoramaUrl: String,
    var panoramaCover: String? = "",
//hotelRoom    酒店客房    object
    //  音频
    val audio: String,
    // 数据id
    val id: Int,
    // 音频信息
//    val audioInfo        object
    // 资源编码
    val resourceCode: String,
    // 房间数量
    val roomNum: String,
    // 特色服务
    val feature: List<String>,
    var likeNum: String,
    // 类型
    val type: Array<String>,
    //   视频
    val video: String,
    // 开业年份
    val openYear: String,
    // 购买地址名称
    val shopName: String,
    // 设备名称
    val eqt: List<String>,
    // 纬度
    val latitude: String,
    // 地区编码
    val regionName: String,
    // 收藏数量
    var collectionNum: String,
    // 小电商店铺code
    val sysCode: String,
    // 拼接地区编码
    val cutRegionName: String,
    val websiteUrl: String,
    val officialUrl: String,
    val officialName: String,
    val audioInfo: MutableList<AudioInfo>,
    // 金牌解说
    val goldStory: GoldStory
){
    fun getadresses():String{
        if(regionName.isNullOrBlank()){
            return ""
        }else{
            return "酒店地址： ${regionName}"
        }
    }
}

/**
 * 酒店房间
 */
class HotelRoomBean(
    val openTalent: Boolean,
//剩余库存数
    val stock: Int,
//成本价
    val costPrice: BigDecimal,
//销售价
    val salePrice: BigDecimal,
//val extractAmount
//indirectExtract
//directExtract
    val talentExtractType: Int,
    val dummySales: Int,
//销量
    val sales: Int,
//credentials
//needContactInfoExt
//needContactInfo
    val hotelName: String,
//  早餐中文
    val breakfastStr: String,
// 确认方式
    val confirmType: String,
// 退款说明
    val refundDeclare: String,
// 退款需提前几分钟
    val refundAdvanceMinute: Int,
// 退款需提前几小时
    val refundAdvanceHour: Int,
//退款需提前几天
    val refundAdvanceDay: Int,
// 退款截止日期类型：入住日期开始前 BEFORE_USEDATE_BEGIN；入住日期截止前 BEFO
    val refundType: String,
//是否允许退款
    val allowRefund: Boolean,
//cancelTimeStr
//autoCancelTime
//房型图片
    val imageUrls: Array<String>,
//产品id
    val productId: Int,
//gmtModified
//gmtCreate
//isDelete
//supplierAdd
//sysCode
//    视频封面
    val vedioCover: String,
    val vedio: String,
//specialty
// 全景
    val wholeView: String,
//feeExclude
//service
//收费金额
    val money: BigDecimal,
// 早餐是否收费
//val isCharge:String,
// 早餐：0无，1含早，2含双早，3含三早
    val breakfast: Int,
//可住人数
    val num: String,
    val window: String,
//床长宽
    val bedLongWide: String,
//床型
    val bedType: String,
//房间面积
    val acreage: String,
//房型名称
    val roomName: String,
//供应商编号
    val supplierSn: String,
//酒店编号
    val hotelSn: String,
//产品编号
    val productSn: String,
// 房型编号
    val sn: String,
//房型id
    val id: Int,
    val url: String
)

data class HotelRoomInfoBean(
    /**
     * 	证件信息列表-前端展示
     */
    val credentials: Array<String>,
    /**
     * 其他证件信息
     */
    val needContactInfoExt: String,
    /**
     * 入住人信息
     */
    val needContactInfo: String,
    /**
     * 酒店名称
     */
    val hotelName: String,
    /**
     * 早餐中文
     */
    val breakfastStr: String,
    /**
     * 确认方式
     */
    val confirmType: String,
    /**
     * 退款说明
     */
    val refundDeclare: String,
    /**
     * 退款有效期截止前的x分
     */
    val refundAdvanceMinute: Long,
    /**
     * 退款有效期截止前的x时
     */
    val refundAdvanceHour: Long,
    /**
     * 退款有效期截止前的x天
     */
    val refundAdvanceDay: Long,
    /**
     * 退款截止日期类型：入住日期开始前 BEFORE_USEDATE_BEGIN；入住日期截止前 BEFO
     */
    val refundType: String,
    /**
     * 是否允许退款
     */
    val allowRefund: Boolean,
    /**
     * 自动取消时间
     */
    val cancelTimeStr: String,
    /**
     * 自动取消时间
     */
    val autoCancelTime: Long,
    /**
     * 房型图片
     */
    val imageUrls: Array<String>,
    /**
     * 产品id
     */
    val productId: Long,
    /**
     * 店铺编号
     */
    val sysCode: String,
    /**
     * 视频封面
     */
    val vedioCover: String,
    /**
     * 视频
     */
    val vedio: String,
    /**
     *  房型特色
     */
    val specialty: String,
    /**
     * 全景
     */
    val wholeView: String,
    /**
     * 费用包含
     */
    val feeExclude: String,
    /**
     * 服务
     */
    val service: String,
    val money: BigDecimal,
    /**
     *  早餐是否收费
     */
    val isCharge: Boolean,
    /**
     * 早餐：0无，1含早，2含双早，3含三早
     */
    val breakfast: Int,
    /**
     * 可住人数
     */
    val num: Int,
    /**
     * 是否有窗
     */
    val window: String,
    /**
     *  床长宽
     */
    val bedLongWide: String,
    /**
     *  床型
     */
    val bedType: String,
    val acreage: String,
    /**
     * 房型名称
     */
    val roomName: String,
    /**
     * 供应商编号
     */
    val supplierSn: String,
    val hotelSn: String,
    /**
     *  产品编号
     */
    val productSn: String,
    /**
     * 房型编号
     */
    val sn: String,
    /**
     *  房型id
     */
    val id: String,
    val url: String
)

