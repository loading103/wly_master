package com.daqsoft.provider.utils

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import java.text.DecimalFormat

/**
 * @Description
 * @ClassName   AddressUtil
 * @Author      luoyi
 * @Time        2020/3/23 11:20
 */
object AddressUtil {


    /**
     * 获取两点之间的距离
     * @param start 起点经纬度
     *
     * @param target 终点经纬度
     */
    fun getLocationDistanceEn(start: LatLng, target: LatLng): String {
        var result: String = ""
        var dis = AMapUtils.calculateLineDistance(start, target)
        result = if (dis > 1000) {
            val df = DecimalFormat("0.00")
            df.format(dis / 1000) + "KM"
        } else {
            dis.toInt().toString() + "M"

        }
        return result
    }

    /**
     * 获取两点之间的距离git config --global user.name "qiuql"
     * git config --global user.email "951686695@qq.com"

     * @param start 起点经纬度
     * @param target 终点经纬度
     */
    fun getLocationDistanceCh(start: LatLng, target: LatLng): String {
        var result: String = ""
        var dis = AMapUtils.calculateLineDistance(start, target)
        result = if (dis > 1000) {
            val df = DecimalFormat("0.00")
            df.format(dis / 1000) + "KM"
        } else {
            dis.toInt().toString() + "M"

        }
        return result
    }
}