package com.daqsoft.slowLiveModule.bean

import com.daqsoft.provider.bean.VipResourceStatus
import com.google.gson.annotations.SerializedName
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class LiveDetailBean(
    var collectionNum: Int = 0, // 0
    var collectionStatus: Int = 0, // 0
    var commentNum: Int = 0, // 0
//    @SerializedName("images")
//    var imagesUrlList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1564730407415/18609517_112216473140_2.jpg,http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1564730407354/7119492_114440620000_2.jpg
    var likeNum: Int = 0, // 0
    var likeStatus: Int = 0, // 0
    var liveUrl: String = "", // 123455
    var scenicId: Int = 0, // 406
    var scenicLevel: String = "", // 无等级
    var scenicSpotsId: Int = 0, // 113
    var scenicSpotsName: String = "", // 小景点
    var scenicTheme: String = "",
    var showNum: Int = 0, // 9
    var scenicName: String?,
    var summary: String = "" ,// 烦烦烦
    var images: String="",
    val liveList: List<Live>,
    val vipResourceStatus: VipResourceStatus ){
    fun getImagess(): String = images.getRealImages()
}

data class Live(
    val images: String,
    val liveEndTime: String,
    val liveName: String,
    val liveStartTime: String,
    val liveTimeType: String,
    val liveUrl: String,
    val replayCover: String,
    val replayUrl: String,
    val scenicId: Int,
    val scenicName: String,
    val scenicSpotsId: Int,
    val scenicSpotsName: String,
    val showNum: Int,
    val summary: String
){
    //1直播  2回放 0不显示状态
    fun getType(): String {
        if(!liveTimeType.isNullOrEmpty()){
            if(liveTimeType == "allDay"){
                return  "1"
            }

            val currentTimeMillis = System.currentTimeMillis();
            if(liveTimeType=="period"){
                if(liveStartTime.isNullOrEmpty() || liveEndTime.isNullOrEmpty()){
                    return "0"
                }
                return if( currentTimeMillis>getTime(liveStartTime) && currentTimeMillis<getTime(liveEndTime)) "1" else "2"
            }

            return "0";

        }else{
            return "0"
        }
    }

    fun getImagess(): String = images?.getRealImages()
    //获取起始时间时间戳9：00
    fun getTime(time :String): Long {
        try {
            val split = time.split(":")
            val time1 = split[0].toInt() * 3600 * 1000 + split[1].toInt() * 60 * 1000
            return time1+getTodayTime()
        }catch (e : Exception){
            return 0
        }
        return 0
    }
//获取当天0点的时间戳

    private fun getTodayTime(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        return calendar.timeInMillis
    }


}


