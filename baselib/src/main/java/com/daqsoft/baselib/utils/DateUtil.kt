package com.daqsoft.baselib.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Description 时间格式化工具
 * @ClassName   DateUtil
 * @Author      luoyi
 * @Time        2020/3/23 10:38
 */
object DateUtil {
    private val ymdhms = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val mdhmLink = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

    const val YMDHMS_ONE = "yyyy.MM.dd HH:mm"

    /**
     * String to Date
     *
     * @param time
     * @return
     */
    fun getDateByString(time: String?): Date? {

        return formatDate("yyyy.MM.dd HH:mm:ss", time)
    }

    fun formatDate(fomat: String = "yyyy.MM.dd HH:mm:ss", time: String?): Date? {
        var date = Date()
        //注意format的格式要与日期String的格式相匹配
        val sdf: DateFormat = SimpleDateFormat(fomat)
        try {
            date = sdf.parse(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    /**
     *@param format 需要的格式
     * @param currentTimeFormatStr 当前时间的格式
     * @param time 时间
     */
    fun formatDateByString(
        format: String,
        currentTimeFormatStr: String = "yyyy-MM-dd",
        time: String?
    ): String {
        if (time.isNullOrEmpty()) {
            return ""
        }
        var result = ""
        val sdf: DateFormat = SimpleDateFormat(format)
        var baseFormat = SimpleDateFormat(currentTimeFormatStr)
        try {
            result = sdf.format(baseFormat.parse(time))
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 获取月份时间
     * @param time date 对象
     */
    fun getDateDayString(time: Date): String {
        return getDateDayString("MM月dd日", time)
    }

    fun getDateDayString(fomat: String, time: Date?): String {
        if (time == null) {
            return ""
        }
        var result = ""
        val sdf: DateFormat = SimpleDateFormat(fomat)
        try {
            result = sdf.format(time)
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 获取明天的时间
     * @param time date 对象
     */
    fun getNextDayString(time: Date): String {
        val cal = Calendar.getInstance()
        cal.time = time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val date = cal.time
        return getDateDayString(date)
    }

    /**
     * 获取就带你时间对象
     * @param date 时间
     */
    fun getFormatDateByString(format: String, time: String?): Date {
        var date = Date()
        if (!time.isNullOrEmpty()) {
            val sdf: DateFormat = SimpleDateFormat(format)
            try {
                date = sdf.parse(time)
            } catch (e: Exception) {

            }
        }
        return date
    }

    /**
     * 获取酒店 时间
     * @param date 时间
     */
    fun getHotelDateString(time: Date): String {
        var result = ""
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            result = sdf.format(time)
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 毫秒转日时分秒
     */
    fun formatTime(ms: Long): String {
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24

        val day = ms / dd
        val hour = (ms - day * dd) / hh
        val minute = (ms - day * dd - hour * hh) / mi
        var second = (ms - day * dd - hour * hh - minute * 60) / mi

        val sb = StringBuilder()
        if (day > 0) {
            sb.append("${day}天")
        }
        if (hour > 0) {
            sb.append("${hour}小时")
        }
        if (minute > 0) {
            sb.append("${minute}分钟")
        }

        return sb.toString()
    }

    /**
     * 毫秒转日时分秒
     */
    fun formatTime2(ms: Long): String {
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24

        val day = ms / dd
        val hour = (ms - day * dd) / hh
        val minute = (ms - day * dd - hour * hh) / mi
        var second = (ms - day * dd - hour * hh - minute * 60) / mi
        return if (day > 0) {
            "${day}天${hour}时${minute}分"
        } else if (hour > 0) {
            "${hour}时${minute}分"
        } else if (minute > 0) {
            "${minute}分钟";
        } else {
            "0分钟"
        }
    }

    /**
     * 毫秒格式化未时间单位
     * @param second 毫秒
     */
    fun getFormatedTime(second: Long): String {
        var days = second / (1000 * 60 * 60 * 24);
        var hours = (second % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        var minutes = (second % (1000 * 60 * 60)) / (1000 * 60);
        var seconds = (second % (1000 * 60)) / 1000;
        var result: StringBuilder = StringBuilder("")
        if (days > 0) {
            result.append("${days}天")
        }
        if (hours > 0) {
            result.append("${hours}时")
        }
        if (minutes > 0) {
            result.append("${minutes}分钟")
        }
        if (seconds > 0) {
            result.append("${seconds}秒")
        }

        return result.toString()
    }

    /**
     * @param ms
     */
    fun getTime(ms: Int): String {
        var s = (ms / 1000) % 60
        var m = ms / 1000 / 60
        var result = ""
        result = if (m < 10) {
            "0$m"
        } else {
            m.toString()
        }
        result = if (s < 10) {
            "$result:0$s"
        } else {
            "$result:$s"
        }
        return result
    }

    /**
     * 获取酒店 时间
     * @param date 时间
     */
    fun getDqDateString(time: Date): String {
        var result = ""
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            result = sdf.format(time)
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 获取酒店 时间
     * @param date 时间
     */
    fun getDqDateString(format: String, time: Date): String {
        var result = ""
        val sdf: DateFormat = SimpleDateFormat(format)
        try {
            result = sdf.format(time)
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 获取就带你时间对象
     * @param date 时间
     */
    fun getHotelDateByString(time: String?): Date {
        var date = Date()
        if (!time.isNullOrEmpty()) {
            val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            try {
                date = sdf.parse(time)
            } catch (e: Exception) {

            }
        }
        return date
    }

    /**
     * 获取明天的时间
     * @param time date 对象
     */
    fun getNextHotelDateString(time: Date): String {
        val cal = Calendar.getInstance()
        cal.time = time
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val date = cal.time
        return getHotelDateString(date)
    }

    /**
     * 获取明天的时间
     * @param time date 对象
     * @param amount 需要获取几天？？
     */
    fun getNextHotelDateString(time: Date, amount: Int = 1): String {
        val cal = Calendar.getInstance()
        cal.time = time
        cal.add(Calendar.DAY_OF_MONTH, amount)
        val date = cal.time
        return getHotelDateString(date)
    }

    /**
     * 获取明天的日期
     */
    fun getNextDate(): Date {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val date = cal.time
        return date
    }

    /**
     * 时间格式化
     * 如果活动的时间是一天里的不同时段，展示样式未：2019.05.08  08:00-24:00
     * *注意 默认传进来字符串都为空*
     * @param time1 时间1 start time
     * @param time2 时间2 end time
     */
    fun getTwoDateStrs(time1: String?, time2: String?): String {
        var result = ""
        if (time1.isNullOrEmpty() || time2.isNullOrEmpty()) {
            return ""
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val dateFormat1 = SimpleDateFormat("yyyy.MM.dd HH:mm")
        val dateFormat2 = SimpleDateFormat("HH:mm")
        try {
            val date1 = dateFormat.parse(time1)
            val date2 = dateFormat.parse(time2)
            result = "${dateFormat1.format(date1)}-"
            // 判断time1 time 2是否同一天
            if (isSameDate(date1, date2)) {
                result += dateFormat2.format(date2)
            } else {
                result += dateFormat1.format(date2)
            }
        } catch (e: Exception) {

        }

        return result
    }

    fun getTwoDateDayStrs(time1: String?, time2: String?): String {
        var result = ""

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val dateFormat1 = SimpleDateFormat("yyyy.MM.dd HH:mm")
        val dateFormat2 = SimpleDateFormat("HH:mm")
        try {
            val date1 = dateFormat.parse(time1)
            val date2 = dateFormat.parse(time2)
            result = "${dateFormat1.format(date1)}-"
            // 判断time1 time 2是否同一天
            if (isSameDate(date1, date2)) {
                result += dateFormat2.format(date2)
            } else {
                result += dateFormat1.format(date2)
            }
        } catch (e: Exception) {

        }

        return result
    }

    /**
     * 判断时间是不是在同一天
     */
    fun isSameDate(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        val isSameYear = cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
        val isSameMonth = (isSameYear
                && cal1[Calendar.MONTH] == cal2[Calendar.MONTH])
        return (isSameMonth
                && cal1[Calendar.DAY_OF_MONTH] == cal2[Calendar.DAY_OF_MONTH])
    }

    /**
     * 获取时间，在一周内那一天
     * @param date
     */
    fun getDayOfWeek(date: Date?): String {
        if (date == null) {
            return ""
        }
        val cal = Calendar.getInstance()
        var result = ""
        cal.time = date
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> {
                result = "星期日"
            }
            Calendar.MONDAY -> {
                result = "星期一"
            }
            Calendar.TUESDAY -> {
                result = "星期二"
            }
            Calendar.WEDNESDAY -> {
                result = "星期三"
            }
            Calendar.THURSDAY -> {
                result = "星期四"
            }
            Calendar.FRIDAY -> {
                result = "星期五"
            }
            Calendar.SATURDAY -> {
                result = "星期六"
            }
        }
        return result
    }

    /**
     * 获取时间，在一周内那一天
     * @param date
     */
    fun getDayOfWeekV2(date: Date?): String {
        if (date == null) {
            return ""
        }
        val cal = Calendar.getInstance()
        var result = ""
        cal.time = date
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> {
                result = "周日"
            }
            Calendar.MONDAY -> {
                result = "周一"
            }
            Calendar.TUESDAY -> {
                result = "周二"
            }
            Calendar.WEDNESDAY -> {
                result = "周三"
            }
            Calendar.THURSDAY -> {
                result = "周四"
            }
            Calendar.FRIDAY -> {
                result = "周五"
            }
            Calendar.SATURDAY -> {
                result = "周六"
            }
        }
        return result
    }

    /**
     * 获取周末信息
     */
    fun getWeek(week: Int): String {
        var result: String = ""
        when (week) {
            7 -> {
                result = "星期日"
            }
            1 -> {
                result = "星期一"
            }
            2 -> {
                result = "星期二"
            }
            3 -> {
                result = "星期三"
            }
            4 -> {
                result = "星期四"
            }
            5 -> {
                result = "星期五"
            }
            6 -> {
                result = "星期六"
            }
        }
        return result
    }

    /**
     * 判断给定的时间是否早于当前时间
     */
    fun isBeforeNow(time: String): Boolean {
        var currentTime = System.currentTimeMillis()
        var baseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var date = baseFormat.parse(time)
        return currentTime > date.time
    }

    /**
     * 判断当前时间，是否大于给定时间24小时
     */
    fun isBeforeOneDay(time: String): Boolean {
        try {
            var currentTime = System.currentTimeMillis()
            var baseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var date = baseFormat.parse(time)
            var diff = date.time - currentTime
            return diff > 24 * 60 * 60 * 1000
        } catch (e: java.lang.Exception) {

        }
        return false
    }

    fun getBeforDay(time: String?): String {
        if (time.isNullOrEmpty()) {
            return ""
        }
        try {
            var baseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var date = baseFormat.parse(time)
            var cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DATE, -1)
            return DateUtil.getDqDateString("yyyy.MM.dd HH:mm", cal.time)
        } catch (e: java.lang.Exception) {

        }
        return time
    }

    fun getBefoHour(time: String?, hourStr: String?): String {
        if (time.isNullOrEmpty()) {
            return ""
        }
        try {
            var baseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var date = baseFormat.parse(time)
            var cal = Calendar.getInstance()
            cal.time = date
            if (!hourStr.isNullOrEmpty()) {
                var hour = hourStr!!.toFloat()
                var monute = (hour * 60).toInt()
                if (monute > 0)
                    cal.add(Calendar.MINUTE, -monute)
            }
            return DateUtil.getDqDateString("yyyy.MM.dd HH:mm", cal.time)
        } catch (e: java.lang.Exception) {

        }
        return time
    }

    fun isBeforeNowV2(time: String): Boolean {
        try {
            var currdate = Date()
            var baseFormat = SimpleDateFormat("yyyy-MM-dd")
            var date = baseFormat.parse(time)
            var strDate = baseFormat.format(currdate)
            var fcurrdate = baseFormat.parse(strDate)
            var currentTime = fcurrdate.time
            return currentTime > date.time
        } catch (e: java.lang.Exception) {

        }
        return false
    }

    /**
     * 获取两个时间相差多少小时
     */
    fun getGapCount(startDate: Date?, endDate: Date?): Int {
        var diff = -1
        if (startDate == null || endDate == null) {
            return diff
        }
        try {
            val fromCalendar = Calendar.getInstance()
            fromCalendar.time = startDate
            fromCalendar.set(Calendar.SECOND, 0)
            fromCalendar.set(Calendar.MILLISECOND, 0)

            val toCalendar = Calendar.getInstance();
            toCalendar.time = endDate
            toCalendar.set(Calendar.SECOND, 0)
            toCalendar.set(Calendar.MILLISECOND, 0)
            var diffTime = toCalendar.timeInMillis - fromCalendar.timeInMillis
            diff = ((diffTime / (1000 * 60 * 60)).toInt())
        } catch (e: java.lang.Exception) {
        }


        return diff
    }

    /**
     * 获取传入时间，月份得第一天
     */
    fun getMothFirstDay(date: Date): Date? {
        if (date == null) {
            return null
        }

        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.time
    }

    /**
     * 获取当前是星期几
     */
    fun getDayOfWeekDate(date: Date): Int {
        var calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 获取上一个月的时间
     * @param date 传入的时间
     */
    fun getPreMonth(date: Date): Date? {
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

    fun getNexMonth(date: Date): Date? {
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        return calendar.time
    }

    fun getDayOffest(week: Int): Int {
        return when (week) {
            Calendar.SUNDAY -> {
                7
            }
            Calendar.MONDAY -> {
                1
            }
            Calendar.TUESDAY -> {
                2
            }
            Calendar.WEDNESDAY -> {
                3
            }
            Calendar.THURSDAY -> {
                4
            }
            Calendar.FRIDAY -> {
                5
            }
            Calendar.SATURDAY -> {
                6
            }
            else -> {
                0
            }
        }
    }

    /**
     * 获取当前时间
     *  00:00:00
     */
    fun getNowTimeString(): String {
        var result: StringBuilder = StringBuilder()
        var cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR_OF_DAY)
        var hourStr: String = if (hour < 10) {
            "0${hour}"
        } else {
            "$hour"
        }
        result.append(hourStr).append(":")
        var minute = cal.get(Calendar.MINUTE)
        var minuteStr: String = if (minute < 10) {
            "0${minute}"
        } else {
            "$minute"
        }
        result.append(minuteStr)
        var second = cal.get(Calendar.SECOND)
        var secondStr: String = if (second < 10) {
            "${second}"
        } else {
            "$second"
        }
        result.append(":")
        result.append(secondStr)
        return result.toString()
    }

    /**
     * 获取当前日期
     * yyyy-MM-dd 星期一
     */
    fun getNowWeekTimeString(): String {
        var result = StringBuilder()
        try {
            var dateStr = getDateDayString("yyyy-MM-dd", Date())
            result.append(dateStr)
            result.append("  ")
            result.append(getDayOfWeek(Date()))

        } catch (e: java.lang.Exception) {
        }

        return result.toString()
    }

    fun getFutureDay(inDate: String, dateNum: Int): String? {
        if (dateNum == 0) {
            return inDate
        }
        var date = formatDate("yyyy-MM-dd", inDate)
        if (date == null) {
            return inDate
        }
        var cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, dateNum)
        return getDateDayString("yyyy-MM-dd", cal.time)
    }

    /**
     * 获取0点的时间
     */
    fun getTodayZero(): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        val zero = calendar.time
        return getDqDateString("yyyy-MM-dd HH:mm", zero)
    }

    /**
     * 获取0点的时间
     */
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        val zero = calendar.time
        return getDqDateString("yyyy-MM-dd HH:mm", zero)
    }

    /**
     * 获取0点的时间
     */
    fun getCurrentTime(diffHour: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        var hour = calendar[Calendar.HOUR_OF_DAY] - diffHour
        calendar[Calendar.HOUR_OF_DAY] = if (hour > 0) {
            hour
        } else {
            0
        }
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        val zero = calendar.time
        return getDqDateString("yyyy-MM-dd HH:mm", zero)
    }


    /**
     * 获取23：59的时间
     */
    fun getTodayTwelve(): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        val zero = calendar.time
        return getDqDateString("yyyy-MM-dd HH:mm", zero)
    }
}