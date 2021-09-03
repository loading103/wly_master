package com.daqsoft.servicemodule.uitils

import android.annotation.SuppressLint
import com.daqsoft.servicemodule.bean.PlaneTimeBean
import com.daqsoft.venuesmodule.repository.bean.Week
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * desc :
 *
 * @author 江云仙
 * @version 1.0.0
 * @date 2020/4/7 11:06
 * @since JDK 1.8
 */
object TimeSwitch {
    const val datePattern = "yyyy-MM-dd HH:mm:ss"
    const val dateYMD = "yyyy-MM-dd"
    const val YMD = "yyyy年MM月dd日"
    const val MD = "MM月dd日"
    const val YM = "yyyy-MM"
    const val DD = "dd"
    /**
     * 将秒数转换为日时分秒，
     * @param second
     * @return
     */
    fun secondToTime(second: Long): String {
        if (second >= 3600) {
            if (second % 3600 == 0L) {
                return (second / 3600).toInt().toString() + "小时"
            } else {
                return Math.floor(second / 3600.toDouble()).toInt().toString() + "小时" + Math.ceil(((second % 3600) / 60.0).toDouble()).toInt().toString() + "分钟"
            }
        } else {
            return Math.ceil((second / 60.0).toDouble()).toInt().toString() + "分钟"
        }
    }

    /**
     *设置步行距离
     */
    fun setWalkDis(walkingDistance: Long): String {
        val walkingDis = MathUtil.div(walkingDistance.toDouble(), 1000.0, 1)
        return if (walkingDis >= 1) {
            "步行" + walkingDis + "公里"
        } else {
            "步行" + walkingDistance + "米"
        }

    }

    /**
     *获取今天日期
     */
    fun getToday(): String {
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val mYear = c.get(Calendar.YEAR).toString() // 获取当前年份
        val mMonth = (c.get(Calendar.MONTH) + 1).toString()// 获取当前月份
        val mDay = c.get(Calendar.DAY_OF_MONTH).toString()// 获取当前月份的日期号码
        var mWay = c.get(Calendar.DAY_OF_WEEK).toString()
        when (mWay) {
            "1" -> {
                mWay = "天"
            }
            "2" -> {
                mWay = "一"
            }
            "3" -> {
                mWay = "二"
            }
            "4" -> {
                mWay = "三"
            }
            "5" -> {
                mWay = "四"
            }
            "6" -> {
                mWay = "五"
            }
            "7" -> {
                mWay = "六"
            }
        }
        return mYear + "年" + mMonth + "月" + mDay + "日" + " 星期" + mWay
    }

    fun getMonthToday(dateStr: String): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(dateYMD)
        val date = sdf.parse(dateStr)
        c.time = date

//        val c = Calendar.getInstance()
//        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val mMonth = (c.get(Calendar.MONTH) + 1).toString()// 获取当前月份
        val mDay = c.get(Calendar.DAY_OF_MONTH).toString()// 获取当前月份的日期号码
        var mWay = c.get(Calendar.DAY_OF_WEEK).toString()
        when (mWay) {
            "1" -> {
                mWay = "天"
            }
            "2" -> {
                mWay = "一"
            }
            "3" -> {
                mWay = "二"
            }
            "4" -> {
                mWay = "三"
            }
            "5" -> {
                mWay = "四"
            }
            "6" -> {
                mWay = "五"
            }
            "7" -> {
                mWay = "六"
            }
        }
        return mMonth + "月" + mDay + "日" + " 周" + mWay
    }

    fun getChooseToday(): String {
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val mYear = c.get(Calendar.YEAR).toString() // 获取当前年份
        val mMonth = (c.get(Calendar.MONTH) + 1)// 获取当前月份
        val mDay = c.get(Calendar.DAY_OF_MONTH).toString()// 获取当前月份的日期号码
        return "$mYear-" + (if (mMonth < 10) {
            "0$mMonth"
        } else {
            mMonth
        }) + "-" + mDay
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     */
    fun getWeekDay(type: String, date: Date): String? {
//        val date = Date(seconds)

        val c = Calendar.getInstance()
        c.time = date
        val wek = c[Calendar.DAY_OF_WEEK]
        return getWeekStr(wek, type)
    }

    private fun getWeekStr(wek: Int, type: String): String {
        var Week = ""
        if (wek == 1) {
            Week += " " + type + "日"
        }
        if (wek == 2) {
            Week += " " + type + "一"
        }
        if (wek == 3) {
            Week += " " + type + "二"
        }
        if (wek == 4) {
            Week += " " + type + "三"
        }
        if (wek == 5) {
            Week += " " + type + "四"
        }
        if (wek == 6) {
            Week += " " + type + "五"
        }
        if (wek == 7) {
            Week += " " + type + "六"
        }
        return Week
    }

    /**
     * 获取未来几天内的日期和星期
     * @param intervals      intervals天内
     * @return              日期数组
     */
    fun getDayAndWeeks(intervals: Int, time: String): ArrayList<PlaneTimeBean> {
        val pastDaysList: ArrayList<PlaneTimeBean> = ArrayList()
        for (i in 0 until intervals) {
            if (time == getNextChooseDate(time, i, dateYMD)) {
                pastDaysList.add(PlaneTimeBean(getNextWeek(time, "周", i), getNextChooseDate(time, i, DD), getNextChooseDate(time, i, dateYMD), "--", true))
            } else {
                pastDaysList.add(PlaneTimeBean(getNextWeek(time, "周", i), getNextChooseDate(time, i, DD), getNextChooseDate(time, i, dateYMD), "--", false))
            }

        }
        return pastDaysList
    }

    /**
     * 获取未来几天内的日期数组
     * @param
     * intervals天内
     * @return              日期数组
     */
    fun getDays(intervals: Int): ArrayList<String>? {
        val pastDaysList: ArrayList<String> = ArrayList()
        for (i in 0 until intervals) {
//            pastDaysList.add(getNextChooseDate(time,i, DD))
        }
        return pastDaysList
    }

    /**
     * 获取未来几天内的星期数组
     * @param intervals      intervals天内
     * @return              日期数组
     */
    fun getWeeks(intervals: Int): ArrayList<String>? {
        val pastDaysList: ArrayList<String> = ArrayList()
        for (i in 0 until intervals) {
//            pastDaysList.add(getNextWeek("周",i))
        }
        return pastDaysList
    }

    /**
     * 获取未来几天内的星期
     * @param intervals      intervals天内
     * @return              日期
     */
    fun getNextWeek(dateStr: String, type: String, next: Int): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(dateYMD)
        val date = sdf.parse(dateStr)
        c.setTime(date)
        c[Calendar.DAY_OF_YEAR] = c[Calendar.DAY_OF_YEAR] + next
        val wek = c[Calendar.DAY_OF_WEEK]
        return getWeekStr(wek, type)
    }

    /**
     * 获取之前几天内的星期
     * @param intervals      intervals天内
     * @return              日期
     */
    fun getBeforeWeek(type: String, next: Int): String {
        val c = Calendar.getInstance()
        c[Calendar.DAY_OF_YEAR] = c[Calendar.DAY_OF_YEAR] - next
        val today = c.time
        val wek = c[Calendar.DAY_OF_WEEK]

        return getWeekStr(wek, type)
    }

    /**
     * 获取未来第几天的日期
     * @param past
     * dateStr:开始时间
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getNextChooseDate(dateStr: String, past: Int, type: String): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(dateYMD)
        val date = sdf.parse(dateStr)
        calendar.setTime(date)

        calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] + past
        val today = calendar.time
        val format = SimpleDateFormat(type)
        return format.format(today)
    }

    /**
     * 获取过去第几天的日期
     * @param past
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getPastChooseDate(past: Int, type: String): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] - past
        val today = calendar.time
        val format = SimpleDateFormat(type)
        return format.format(today)
    }

    /**
     * 计算传入时间距离当前时间多久
     *
     * @param date
     * @return
     */
    fun getTimeDiff(date: String?): Long? {
        return try {
            val parse: Date = SimpleDateFormat(dateYMD).parse(date)
            val now = Date()
            // 这样得到的差值是微秒级别
            var diff = parse.time - now.time
            // 只能精确到日 无法具细到年 月 不能确定一个月具体多少天 不能确定一年具体多少天
            // 获取日
            val day = diff / (1000 * 60 * 60 * 24)
            diff %= (1000 * 60 * 60 * 24)
            // 获取时
            val hour = diff / (1000 * 60 * 60)
            diff %= (1000 * 60 * 60)
            // 获取分
            val min = diff / (1000 * 60)
            diff %= (1000 * 60)
            // 获取秒
            val sec = diff / 1000
            if (hour.toInt() != 0 || min.toInt() != 0 || sec.toInt() != 0) {
                if (day > 0) {
                    day + 1
                } else {
                    day - 1
                }
            } else {
                day
            }

        } catch (e: ParseException) {
            0
        }
    }


}