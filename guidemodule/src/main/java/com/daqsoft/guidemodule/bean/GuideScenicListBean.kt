package com.daqsoft.guidemodule.bean

import com.google.gson.annotations.SerializedName

data class GuideScenicListBean(
    var showTypeForDev: String = "",
    var address: String? = "",
    @SerializedName("audio")
    var audioList: String = "",
    var audioTime: String = "",
    var distance: String = "",
    @SerializedName("images")
    var imageUrlList: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var name: String = "", // 进店1
    var region: String = "",
    var resourceId: String = "", // 148
    var resourceType: String = "", // CONTENT_TYPE_SCENIC_SPOTS
    @SerializedName("summary")
    var summaryStr: String = "",
    var tourId: String = ""
) {


    fun getSummary(): CharSequence {
        if (summaryStr.isNullOrEmpty()) {
            return ""
        }
        return summaryStr
            .replace("<p>", "")
            .replace("<p/>", "")
            .replace("<br/>", "")
            .replace("<br>", "")
            .toHtmlCharSequence()
    }


    fun getAudio(): String {
        if (!audioList.isNullOrEmpty()) {

            if (!audioList.contains(","))
                return audioList

            val imgList = audioList.split(",")
            if (!imgList.isNullOrEmpty()) {
                return imgList[0]
            }
        }
        return ""
    }

    fun getImages(): String {
        if (!imageUrlList.isNullOrEmpty()) {

            if (!imageUrlList.contains(","))
                return imageUrlList

            val imgList = imageUrlList.split(",")
            if (!imgList.isNullOrEmpty()) {
                return imgList[0]
            }
        }
        return ""
    }

}
