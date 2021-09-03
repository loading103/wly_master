package com.daqsoft.provider.bean

import com.google.gson.Gson
import com.google.gson.JsonElement

/**
 * desc :资讯列表sh
 * @author 江云仙
 * @date 2020/4/23 15:03
 */
data class InformationBean(
    val audio: JsonElement,
    val author: String,
    val channelName: String,
    val commentNum: Int,
    val contentType: String,
    val createCompany: String,
    val createCompanyLogo: String,
    val id: Int,
    val imageUrls: List<ImageUrls>,
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
    val video: JsonElement
){
    fun getAudioInfo(): Audio {
        return try {
            Gson().fromJson(audio, Audio::class.java)
        } catch (e: Exception) {
            Audio()
        }
    }
    fun getVideoInfo(): Video {
        return try {
            Gson().fromJson(audio, Video::class.java)
        } catch (e: Exception) {
            Video()
        }
    }

    data class ImageUrls(
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
        val imgUrl:String="",
        val name:String="",
        val url:String="",
        val resourceType:String="",
        val resourceId:String="",
        val resourceName:String="",
        val description:String="",
//        val tagNames:List<Any>,
        val tagIds:String="",
        val address:String="",
        val longitude:String="",
        val latitude:String=""
    )
    data class Video(
        val address: String="",
        val description: String="",
        val imgUrl: String="",
        val latitude: String="",
        val longitude: String="",
        val name: String="",
        val resourceId: String="",
        val resourceName: String="",
        val resourceType: String="",
        val tagIds: String="",
//        val tagNames: List<Any>,
        val url: String=""
    )
}

