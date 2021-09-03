package com.daqsoft.provider.bean

/**
 * 栏目简介
 */
data class SubChannelBean(
    val backgroundImg: String,
    val name: String,
    val english: String,
    val logo: String,
    val id: String,
    val subset: List<SubChanelChildBean>
)

/**
 * 子栏目列表
 */
data class SubChanelChildBean(
    val id: String,
    val name: String,
    val pid: String,
    val url: String,
    val summary: String,
    val content: String,
    val logo: String,
    val backgroundImg: String,
    val status: String,
    val siteId: String,
    val createTime: String,
    val updateTime: String,
    val showChannel: Boolean,
    val channelCode: String
)