package com.daqsoft.guidemodule.bean

import com.google.gson.annotations.SerializedName

data class GuideLineBean(
    var isChecked: Boolean = false, //是否选中，adapter 布局用
    var details: List<Detail> = listOf(),
    var id: Int = 0, // 25
    @SerializedName("images")
    var imageUrlList: String = "",
    var name: String = "", // 线路1
    var totalDistance: Double = 0.0, // 2.0621503355572782E7
    var totalNums: Int = 0, // 2
    var totalTime: Long = 0, // 20
    var tourWay: String = "" // 坐东风快递
) {
    data class Detail(
        var oriInPointsIndex: Int = 0,
        @SerializedName("audio")
        var audioList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1565231160656/董贞 - 朱砂泪.mp3
        var distance: String = "", // null
        var id: Int = 0, // 108
        @SerializedName("images")
        var imageUrlList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1565231461987/bg4.jpg
        var latitude: Double = 0.0, // 30.608947
        var longitude: Double = 0.0, // 103.968231
        var name: String = "", // 小景点
        var nextDistance: Double = 0.0, // 2.0621503355572782E7
        var playTime: Long = 0, // null
        var recent: Boolean = false, // false
        var resourceId: String = "", // null
        var resourceType: String = "", // CONTENT_TYPE_SCENIC_SPOTS
        @SerializedName("summary")
        var summaryStr: String = "", // 今天的天气不适合人类居住
        var tourId: String = "" // null
    ) {

        fun getSummary(): CharSequence {

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
