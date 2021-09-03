package com.daqsoft.provider.utils

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description 时间格式工具
 * @ClassName   TimeUtils
 * @Author      PuHua
 * @Time        2019/11/22 14:29
 */
object TimeUtils {
    /**
     * 将时间戳转化为固定格式
     */
    @SuppressLint("SimpleDateFormat")
    fun timeStamp2Date(seconds:String, format:String = "yyyy-MM-dd HH:mm" ):String {
        if (seconds==null){
            return ""
        }

        if(seconds.isNullOrEmpty() || seconds == "null"){
            return ""
        }
        val sdf =  SimpleDateFormat(format)
        return sdf.format( Date(seconds.toLongOrNull()!!))
    }
    fun timeStamp2Date(seconds:String? ):String {
       val format:String = "yyyy-MM-dd HH:mm"

        if(seconds!!.isNullOrEmpty() || seconds == "null"){
            return ""
        }
        val sdf =  SimpleDateFormat(format)
        return sdf.format( Date(seconds.toLongOrNull()!!))
    }

    fun timeString2MD(time:String):Date{
        val format:SimpleDateFormat  = SimpleDateFormat ("yyyy-MM-dd HH:mm")

        if(time!!.isNullOrEmpty() || time == "null"){
            return Date()
        }
        var sdf:Date? = null
        try {
           sdf = format.parse(time)
        }catch (e:Exception){
            return Date()
        }
        return sdf
    }
}