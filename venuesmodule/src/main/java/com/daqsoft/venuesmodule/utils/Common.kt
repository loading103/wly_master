package com.daqsoft.venuesmodule.utils

/**
 * 共用参数
 * @author 黄熙
 * @date 2019/12/25 0025
 * @version 1.0.0
 * @since JDK 1.8
 */
object CommonUtils {
    /**
     * 每页条数
     */
    const val PAGE_SIZE: String = "10"

    /**
     * 通过传入的数字返回对应每周的天
     */
    fun dayStrFormat(weekIndex:String):String = when(weekIndex){
            "1" -> "周日"
            "2" -> "周一"
            "3" -> "周二"
            "4" -> "周三"
            "5" -> "周四"
            "6" -> "周五"
            "7" -> "周六"
        else -> "周一"
    }

}