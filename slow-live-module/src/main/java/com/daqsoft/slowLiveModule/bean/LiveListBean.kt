package com.daqsoft.slowLiveModule.bean

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

internal data class LiveListBean(
    @SerializedName("images")
    var imagesUrlList: String = "", // https://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1577631774710/松鼠.png
    var liveUrl: String = "", //  http://117.190.104.26:18080/movies/ssttkznm/ssttkznm.m3u8
    var scenicSpotsId: Int = 0, // 56  景点id
    var scenicSpotsName: String = "", // 松树头停靠站
    var showNum: Int = 0, // 0  	浏览量
    var summary: String = "",  //简介
    var scenicName: String = "", //景区名称

    val liveEndTime: String= "", //直播结束时间
    val liveName: String= "",   //直播名称
    val liveStartTime: String= "",
    val liveTimeType: String= "",   //直 播时间类型
    var replayCover: String= "", //重播封面
    val replayUrl: String= "",  //重播地址
    val scenicId: Int= 0

) {
    fun getImages(): String = imagesUrlList.getRealImages()
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
    //获取起始时间时间戳9：00
    fun getTime(time :String): Long {
        try {
            val split = time.split(":")
            val time1 = split[0].toInt() * 3600 * 1000 + split[1].toInt() * 60 * 1000
            return time1+getTodayTime()
        }catch (e :Exception){
            return 0
        }
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
