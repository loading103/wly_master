package com.daqsoft.guidemodule.bean

import com.google.gson.annotations.SerializedName

data class GuideScenicDetailBean(
    var code: String = "", // 1338136991333
    var createTime: String = "", // 2020-03-25 16:32:34
    var createUser: Int = 0, // 133
    var description: String = "",
    var detailMap: String = "",
    var handPaintedMap: String = "",
    var id: Int = 0, // 5
    @SerializedName("images")
    var imageUrlList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1565231461987/bg4.jpg
    var latitude: String = "", // 30
    var latitudeBottomLeft: String = "", // 70
    var latitudeTopRight: String = "", // 50
    var longitude: String = "", // 20
    var longitudeBottomLeft: String = "", // 60
    var longitudeTopRight: String = "", // 40
    var mapType: Int = 0, // 1
    var name: String = "", // 导游导览5
    var region: String = "", // 510100
    var regionPath: String = "",
    var scenicId: String = "", // 501
    var siteId: Int = 0, // 91
    var tilesMap: String = "",
    var type: Int = 0, // 2
    var updateTime: String = "",
    var updateUser: String = "",
    var zoom: Int = 0, // 8
    var zoomsMax: Int = 0, // 18
    var zoomsMin: Int = 0 // 0
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
