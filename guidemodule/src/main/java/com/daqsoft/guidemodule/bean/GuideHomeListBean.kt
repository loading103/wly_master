package com.daqsoft.guidemodule.bean

import com.google.gson.annotations.SerializedName

data class GuideHomeListBean(
    var description: String = "",
    var distance: String = "", // 8390.455078125
    var id: String = "", // 5
    @SerializedName("images")
    var imageUrlList: String = "", // http://file.geeker.com.cn/cultural-tourism-cloud/1584085919187/2015040109334776394.jpg
    var latitude: String = "", // 1
    var longitude: String = "", // 1
    var name: String = "", // 导游导览5
    var recommendChannelHomePage: Int = 0, // 0
    var sort: Int = 0,// 999
    val scenicId: String
) {

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

