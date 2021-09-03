package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * @des 菜单项
 * @author PuHua
 * @Date 2019/12/6 13:32
 */
@Parcelize
data class HomeMenu(
    val externalLink: String,
    val id: String,
    val jumpType: String,
    val jumpTypeId: String,
    val menuCode: String,
    val name: String,
    val selectIcon: String,
    val sort: String,
    val subList: MutableList<HomeMenu> ?= mutableListOf(),
    val unSelectIcon: String,
    var moduleId: String,
    var imgUrl: String,
    var gif:String?
): Parcelable

data class HomeTrip(
    var stroke: String,
    var height: String,
    var top: Int
)

/**
 * @des 首页模块
 * @author PuHua
 * @Date 2019/12/11 16:51
 */
data class HomeModule(
    val id: String,
    val imgNum: String,
    val moduleLocation: String,
    val resources: List<HomeMenu>,
    val siteId: String,
    val style: Int
)


/**
 * @des 服务子类
 * @author PuHua
 * @Date 2020/3/27
 */
data class ServiceSubType(
    var type: Int,
    var typeName: String,
    val imageId: Int
)

data class ServiceSubTypeXJ(
    var type: Int,
    var colorType: String,
    val typeName: String,
    val typeIntroce: String,
    val imageId: Int
)

/**
 * 服务分类
 */
data class ServiceType(
    val typeName: String
)