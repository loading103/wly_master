package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ContentVo
 * @Author      luoyi
 * @Time        2020/7/1 16:34
 */
data class ContentBean(
    val audio: Audio?,
    val author: String,
    val channelName: String,
    val commentNum: Int,
    val contentType: String,
    val createCompany: String,
    val sourceUrl: String,
    val createCompanyLogo: String,
    val id: Int,
    val imageUrls: List<ImageUrl>,
    val likeNum: Int,
    val publishTime: String,
    val recommendChannelHomePage: Int,
    val recommendHomePage: Int,
    val showNum: Int,
    val sort: Int,
    val status: Int,
    val summary: String,
    val tagName: List<Any>,
    val title: String,
    val top: Int,
    val video: Video?,
    var imgUrl: String
)

data class ImageUrl(
    val address: String,
    val description: String,
    val imgUrl: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val resourceId: String,
    val resourceName: String,
    val resourceType: String,
    val tagIds: String,
    val tagNames: List<Any>,
    val url: String
)

data class Audio(
    val imgUrl: String,
    val name: String,
    val url: String,
    val resourceType: String,
    val resourceId: String,
    val resourceName: String,
    val description: String,
    val tagNames: List<Any>,
    val tagIds: String,
    val address: String,
    val longitude: String,
    val latitude: String
)

data class Video(
    val address: String,
    val description: String,
    val imgUrl: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val resourceId: String,
    val resourceName: String,
    val resourceType: String,
    val tagIds: String,
    val tagNames: List<Any>,
    val url: String
)