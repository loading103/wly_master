package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * @Description 停车位详情
 * @ClassName   ParkingBean
 * @Author      luoyi
 * @Time        2020/3/19 9:28
 */
@Parcelize
data class ParkingBean(
    /**
     * 经度
     */
    var longitude: String,
    /**
     * 	开放时间
     */
    var openTime: String,
    /**
     * 	数据id
     */
    var id: String,
    /**
     * 距离
     */
    var disnum: String,
    /**
     * 名称
     */
    var name: String,
    /**
     * 纬度
     */
    var latitude: String,
    /**
     * 电话
     */
    var phone: String,
//    surplusNumInterface	剩余车位数（暂不可用）	string
    /**
     * 地址
     */
    var address: String,
    /**
     * 图片
     */
    var images: String,
    /**
     * 收费标准
     */
    var charges: String,
    /**
     * 总数
     */
    var total: String
    ,
    /**
     *
     */
    var scaleSize: String

) : Parcelable

/**
 * @Description 地图资源列表
 * @ClassName   MapRecBean
 * @Author      luoyi
 * @Time        2020/3/19 9:28
 */
@Parcelize
data class MapResBean(
    var id: Int,
    var name: String?,
    var region: String?,
    var address: String?,
    var tag: String?,
    var tagName: String?,
    var images: String?,
    var regionName: String?,
    var orderRoomInfo: String?,
//var activityInfo: 0
    var longitude: String?,
    var latitude: String?,
    var path: String?,
    var resourceLevel: String?,
    var resourceType: String?,
    var likeNum: String?
) : Parcelable

/**
 * @Description 地图资源列表
 * @ClassName   MapRecBean
 * @Author      luoyi
 * @Time        2020/3/19 9:28
 */
data class CaservacBean(
    /**
     * 经度
     */
    var longitude: Double?,
    /**
     * 纬度
     */
    var latitude: Double?,
    /**
     * 类型
     */
    var type: String?,
    /**
     * 数据Id
     */
    var id: Int,
    /**
     * 距离
     */
    var disnum: Int,
    /**
     * 名称
     */
    var name: String,
    /**
     * 手机号
     */
    var phone: String?,
    /**
     * 等级
     */
    var level: String?,
    /**
     * 地址
     */
    var address: String?,
    /**
     * 图片
     */
    var images: String?

)

data class BusShopInfoBean(
    /**
     * 经度
     */
    var longitude: Double?,
    /**
     * 纬度
     */
    var latitude: Double?,
    /**
     * 开放时间
     */
    var openTime: String?,
    var id: Int,
    var disnum: Int,
    /**
     * 名称
     */
    var name: String?,
    /**
     * 地址
     */
    var address: String?,
    /**
     * 图片
     */
    var images: String?,
    /**
     * 收费标桩
     */
    var charges: String?
)

data class ShopMailInfoBean(
    /**
     * 经度
     */
    var longitude: Double?,
    /**
     * 纬度
     */
    var latitude: Double?,
    /**
     * 开发时间
     */
    var openTime: String?,
    /**
     * 数据Id
     */
    var id: Int,
    /**
     * 距离
     */
    var disnum: Int,
    /**
     * 规模
     */
    var scale: String?,
    /**
     * 购物类型
     */
    var shopeType: String?,
    /**
     * 名称
     */
    var name: String?,
    /**
     * 电话
     */
    var phone: String?,
    /**
     * 地址
     */
    var address: String?,
    /**
     *
     */
    var images: String?
)



