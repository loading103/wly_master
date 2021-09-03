package com.daqsoft.volunteer.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 *@package:com.daqsoft.volunteer.utils
 *@date:2020/6/22 9:59
 *@author: caihj
 *@des:TODO
 **/
object CalendarUtils {

    /**
     * 获取距离今天之后几天的一周时间
     */
    fun getWeekDate(days:Int = 0):List<SignDaysBean>{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,days-1)
        val sf = SimpleDateFormat("yyyy-MM-dd")
//        Log.e("date","${sf.format(calendar.time)}")
//        val week = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, Locale.CHINA)
//        Log.e("date", week)
        val weeks = mutableListOf<SignDaysBean>()
        for (i in 0..6){
            calendar.add(Calendar.DAY_OF_MONTH,1)
            val days = SignDaysBean(
                sf.format(calendar.time),
                calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, Locale.CHINA),
                null
            )
            Log.e("date", days.toString())
            weeks.add(days)
        }
        return weeks
    }
}

data class SignDaysBean(
    val date:String,
    val week:String,
    var signTime:List<SignTimeBean>?,
    var selected:Boolean = false
){
    override fun toString(): String {
        return "date:${date},week:${week}"
    }
}
data class SignTimeBean(
    val key:String,
    val time:String,
    var selected:Boolean = false,
    var enable:Boolean = true
){
    override fun toString(): String {
        return "key:${key},time:${time}"
    }
}