package com.daqsoft.guidemodule.bean

import com.google.gson.annotations.SerializedName

data class GuideSearchBean(
    var list: List<GuideSearchBeanData> = listOf(),
    var resourceType: String = "" // CONTENT_TYPE_TOILET
) {
    data class GuideSearchBeanData(
        var address: String = "", // 11
        var audio: String = "",
        var audioTime: String = "",
        var distance: String = "",
        @SerializedName("images")
        var imageUrlList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1565231461987/bg4.jpg
        var latitude: String = "",
        var longitude: String = "",
        var name: String = "", // 厕所1
        var region: String = "", // 510100
        var resourceId: Int = 0, // 242
        var resourceType: String = "", // CONTENT_TYPE_TOILET
        @SerializedName("summary")
        var summaryStr: String = "",
        var tourId: String = ""
    ) {

        fun getSummary(): CharSequence {
            return summaryStr
                .replace("<p>", "")
                .replace("<p/>", "")
                .replace("<br/>", "")
                .replace("<br>", "")
                .toHtmlCharSequence()
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
}