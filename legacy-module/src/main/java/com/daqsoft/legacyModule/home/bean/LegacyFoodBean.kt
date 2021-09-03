package com.daqsoft.legacyModule.home.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LegacyFoodBean(
//    val audio: Audio,
    val author: String,
    val channelName: String,
    val commentNum: Int,
    val contentType: String,
    val createCompany: String,
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
    val title: String,
    val top: Int
//    val video: Video
) : Parcelable {
    @Parcelize
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
        val url: String
    ) : Parcelable

    @Parcelize
    data class Audio(
        val imgUrl: String,
        val name: String,
        val url: String,
        val resourceType: String,
        val resourceId: String,
        val resourceName: String,
        val description: String,
        val tagIds: String,
        val address: String,
        val longitude: String,
        val latitude: String
    ) : Parcelable

    @Parcelize
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
        val url: String
    ) : Parcelable
}

data class TopMenuBean(val name: String, val imgRes: Int, val path: String)

data class DiscoverTypeBean(
    val name:String,
    val type:String,
    var select:Boolean
)