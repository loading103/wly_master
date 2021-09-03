package com.daqsoft.provider.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.daqsoft.venuesmodule.repository.bean.OpenWeek
import com.google.gson.Gson

/**
 * 用一句话来描述功能
 * @author 黄熙
 * @date 2020/1/22 0022
 * @version 1.0.0
 * @since JDK 1.8
 */
class Utils {

    /**
     * 计算开放时间的工具类
     * @param openWeek 开放时间
     * @return 回调计算好的开放时间
     */
    fun OpenWeekUtils(openWeek: String): String {
        var result: String = ""
        if (!openWeek.isNullOrEmpty()) {
            var gson = Gson()
            var openWeek = gson.fromJson(openWeek, OpenWeek::class.java)
            // 通用规则
            if (openWeek.setType == 0) {
                if (!openWeek.week.isNullOrEmpty()) {
                    if (!openWeek.week.get(0).time.isNullOrEmpty() && openWeek.week.get(0).time.size == 2) {
                        result = openWeek.week.get(0).time.get(0) + "-" + openWeek.week.get(0).time.get(1) + " " +
                                "" + openWeek.week.get(0).remarks
                    }
                }
            } else {
                // 不通用规则
                for (week in openWeek.week) {
                    if (!week.time.isNullOrEmpty() && week.time.size == 2) {
                        if (!result.isNullOrEmpty()) {
                            result = result + ","
                        }
                        result = result +
                                week.name + " " + week.time.get(0) + "-" + week.time.get(1) + " " + week.remarks
                    }
                }
            }
        }
        return result
    }


}