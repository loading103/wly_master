package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description 厕所详情bean
 * @ClassName   ToilentBean
 * @Author      luoyi
 * @Time        2020/3/19 9:28
 */
@Parcelize
data class ToilentBean(
    /**
     *经度
     */
    var longitude: String,
    /**
     * 残疾人坑位
     */
    var disableNum: String = "0",
    /**
     * 距离
     */
    var disnum: String = "0",
    /**
     * 母婴室
     */
    var infantNum: String = "0",
    /**
     * 名称
     */
    var name: String,
    /**
     * 电话
     */
    var phone: String,
    /**
     * 收费标准
     */
    var charges: String,
    /**
     * 女士坑位
     */
    var womanNum: String ="0",
    /**
     * 类型
     */
    var type: String,
    /**
     * 规模大小
     */
    var scale: String,
    /**
     * 纬度
     */
    var latitude: String,
    /**
     * 男坑位
     */
    var manNum: String ="0",
    /**
     * 地址
     */
    var address: String,
    /**
     * 图片
     */
    var images: String
) : Parcelable


/**
 * @Description 地图资源厕所bean
 * @ClassName   ToilentBean
 * @Author      luoyi
 * @Time        2020/3/19 9:50
 */
@Parcelize
data class ToilentMapResBean(
    var id: Int,
    var name: String,
    var region: String,
    var address: String,
    var tag: String,
    var tagName: String,
    var images: String,
    var regionName: String,
    var orderRoomInfo: String,
    var activityInfo: String,
    var longitude: String,
    var latitude: String,
    var path: String,
    var resourceLevel: String,
    var resourceType: String,
    var likeNum: Int,
    /**
     * 详情bean
     */
    var infoBean: ToilentBean? = null
) : Parcelable