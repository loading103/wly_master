package com.daqsoft.slowLiveModule.bean

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

internal data class LiveAroundBean(
    @SerializedName("images")
    var imagesUrlList: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1564730407415/18609517_112216473140_2.jpg,http://file.geeker.com
    // .cn/uploadfile/cultural-tourism-cloud/1564730407354/7119492_114440620000_2.jpg
    var scenicSpotsId: Int = 0, // 113
    var scenicSpotsName: String = "", // 小景点
    var showNum: Int = 0, // 10

    var summary: String = "", // 烦烦烦,
    var scenicName: String = "",
    val liveEndTime: String= "", //直播结束时间
    val liveName: String= "",   //直播名称
    val liveStartTime: String= "",
    val liveTimeType: String= "",   //直 播时间类型
    val replayCover: String= "", //重播封面
    val replayUrl: String= "",  //重播地址
    val scenicId: Int= 0,
    val liveUrl: String= ""
){

    fun getImages():String = imagesUrlList.getRealImages()


    fun getImageBg():String {
        if(getType()=="2"){
            return replayCover?:getImages()
        }
      return  imagesUrlList.getRealImages()

    }


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
        }catch (e : Exception){
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
