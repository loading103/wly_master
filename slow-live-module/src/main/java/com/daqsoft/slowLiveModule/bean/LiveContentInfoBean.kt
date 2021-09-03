package com.daqsoft.slowLiveModule.bean


internal data class LiveContentInfoBean(
    val annex: List<Any>,
    val audioInfo: Any,
    val author: String,
    val channelName: String,
    var collectionNum: Int,
    val commentNum: Int,
    val content: String,
    val contentExtendInfo: List<Any>,
    val contentType: String,
    val createCompanyLogo: String,
    val createCompanyName: String,
    val id: Int,
    val imageUrls: ArrayList<ImageUrl>,
    var likeNum: Int,
    val publishTime: String,
    val resourceLinksInfo: List<Any>,
    val showNum: Int,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val tagName: List<Any>,
    val title: String,
    val videoInfo: Any,
    val vipResourceStatus: VipResourceStatus
) {
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

    data class VipResourceStatus(
        var collectionStatus: Boolean,
        var likeStatus: Boolean
    )
}

