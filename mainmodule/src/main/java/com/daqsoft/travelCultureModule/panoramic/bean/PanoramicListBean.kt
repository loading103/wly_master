package com.daqsoft.travelCultureModule.panoramic.bean

data class PanoramicListBean(
    var id: Int = 0, // 1326
    var images: String = "", // https://file.geeker.com.cn/cultural-tourism-cloud/1585734626055/冰冰.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585734629685/冰冰五.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585734634318/《塞湖冬日》.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585734631320/冰雪莲花-王维全.JPG,https://file.geeker.com.cn/cultural-tourism-cloud/1585734632409/赛里木湖晚霞.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585734639003/王维全-雪景.JPG,https://file.geeker.com.cn/cultural-tourism-cloud/1585734639965/雪山牛羊三等奖.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585734642555/西海草原—.jpg
    var name: String = "", // 赛里木湖景区
    var scenicSpots: List<ScenicSpot> = listOf(),
    var url: String = "", // https://720yun.com/t/93vkn9fyrde?scene_id=29710098
    var panoramaCover: String? = ""
) {
    data class ScenicSpot(
        var id: Int = 0, // 173
        var images: String = "", // https://file.geeker.com.cn/cultural-tourism-cloud/1585975914620/景点照片3.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585975920601/景点照片4.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585975910161/景点照片5.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585975917008/景点照片6.jpg,https://file.geeker.com.cn/cultural-tourism-cloud/1585975902436/温泉县森林公园一景.jpg
        var name: String = "", // 博格达尔森林公园
        var scenicSpots: String = "",
        var url: String = "" // https://720yun.com/t/890jkpew5u1?pano_id=3903416
    )

    fun getImagess(): String {
        if (!images.isNullOrEmpty()) {

            if (!images.contains(","))
                return images

            val imgList = images.split(",")
            if (!imgList.isNullOrEmpty()) {
                return imgList[0]
            }
        }
        return ""
    }
}

