package com.daqsoft.provider

import androidx.collection.ArrayMap

/**
 * @Author：      邓益千
 * @Create by：   2020/6/1 17:54
 * @Description： 以下常量都是和服务器对应好的
 */
object Constants {

    /***********************************************出行方式******************************************************/

    const val JOURNEY_TRAVEL_SELF = "JOURNEY_TRAVEL_SELF"
    const val JOURNEY_TRAVEL_TRAFFIC = "JOURNEY_TRAVEL_TRAFFIC"

    private val TRAVEL_TYPE = ArrayMap<String, String>().apply {
        put(JOURNEY_TRAVEL_SELF, "自驾")
        put(JOURNEY_TRAVEL_TRAFFIC, "公共交通")
    }

    /**get出行方式*/
    fun getTravelType(key: String): String? {
        return try {
            TRAVEL_TYPE[key]
        } catch (ex: Exception){
            "this $key = null"
        }
    }


    /**************************************交通类型***************************************************************/
    const val selfDrive = "selfDrive"
    const val train = "train"
    const val bus = "bus"
    const val aviation = "aviation"
    const val autoBus = "autoBus"

    private val TRANSPORT = ArrayMap<String, String>().apply {
        put(selfDrive, "自驾")
        put(train, "火车")
        put(bus, "长途公交")
        put(aviation, "航空")
        put(autoBus, "公共交通")
    }

    /**交通类型顺序定死*/
    private var ORDER_TRANSPORT = ArrayMap<String,Int>().apply {
        put(autoBus, 0)
        put(bus, 1)
        put(train, 2)
        put(aviation, 3)
        put(selfDrive, 4)
    }

    /**get交通类型*/
    fun getTransport(key: String): String? {
        return try {
            TRANSPORT[key]
        } catch (ex: Exception){
            "this $key = null"
        }
    }

    /**get交通类型的顺序*/
    fun getOrderBy(key: String): Int? {
        return try {
            ORDER_TRANSPORT[key]
        } catch (ex: Exception){
            -1
        }
    }


    /**************************************其他类型 ***************************************************************
     * 常量地址：http://rap.daqsoft.com/workspace/myWorkspace.do?projectId=1140#18656
     */

    /**餐饮*/
    const val TYPE_RESTAURANT = "CONTENT_TYPE_RESTAURANT"

    /**场馆*/
    const val TYPE_VENUE = "CONTENT_TYPE_VENUE"

    /**酒店*/
    const val TYPE_HOTEL = "CONTENT_TYPE_HOTEL"

    /**景区*/
    const val TYPE_SCENERY = "CONTENT_TYPE_SCENERY"
}