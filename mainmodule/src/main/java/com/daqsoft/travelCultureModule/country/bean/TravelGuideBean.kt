package com.daqsoft.travelCultureModule.country.bean

/**
 * desc :乡村游记攻略
 * @author 江云仙
 * @date 2020/4/18 13:44
 */
data class TravelGuideBean(
    val audio: Audio,
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
    val tagName: List<String>,
    val title: String,
    val top: Int,
    val video: Video
) {
    /**
     * 获取封面
     */
    public fun getContentCoverImageUrl(): String {
        var imageUrl1 = ""
        if (!contentType.isNullOrEmpty()) {
            when (contentType) {
                "CONTENT", "IMAGE"
                -> {
                    if (!imageUrls.isNullOrEmpty()) {
                        var bean = imageUrls[0]
                        if (bean != null) {
                            imageUrl1 = bean.url
                        }
                    }
                }
                "AUDIO" -> {
                    if (audio != null) {
                        if (!audio.imgUrl.isNullOrEmpty()) {
                            imageUrl1 = audio.imgUrl
                        }
                    }
                }
                "VIDEO" -> {
                    if (video != null) {
                        if (!video.imgUrl.isNullOrEmpty()) {
                            imageUrl1 = video.imgUrl
                        }
                    }
                }
            }
        }
        return imageUrl1
    }
}

data class ImageUrls(
    val address: String,
    val description: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val resourceId: String,
    val resourceName: String,
    val resourceType: String,
    val tagIds: String,
//    val tagNames: Array<String>,
    val url: String
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
//    val tagNames: List<String>,
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
    val tagIds: String,
    val address: String
)