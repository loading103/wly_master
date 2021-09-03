package com.daqsoft.travelCultureModule.redblack.bean

/**
 * 红黑榜数据
 */
data class RedBgBean(
    val backgroundImg: String,
    val channelCode: String,
    val content: String,
    val createTime: String,
    val createUser: Int,
    val english: String,
    val id: Int,
    val logo: String,
    val name: String,
    val pid: Int,
    val showChannel: Boolean,
    val siteId: Int,
    val sort: Int,
    val status: Int,
    val summary: String,
    val updateTime: Any,
    val updateUser: Any,
    val url: String
)
