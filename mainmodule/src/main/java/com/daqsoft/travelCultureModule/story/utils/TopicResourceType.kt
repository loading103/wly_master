package com.daqsoft.travelCultureModule.story.utils

import com.daqsoft.mainmodule.R

/**
 *@package:com.daqsoft.travelCultureModule.story.utils
 *@date:2020/4/2 20:21
 *@author: caihj
 *@des:话题类型判断
 **/
object TopicResourceType {

    /**
     * 获取话题类型背景icon
     */
    fun getTypeBgIcon(topicTypeName:String):Int{
       return when(topicTypeName){
            "美食" -> R.mipmap.topic_list_ms
            "民宿" ->R.mipmap.topic_list_homestay
            "酒店" ->R.mipmap.topic_list_hotel
            "游玩" ->R.mipmap.topic_list_yw
            "探店" ->R.mipmap.topic_list_td
            "文化" ->R.mipmap.topic_list_wh
            else -> R.mipmap.topic_list_ms
        }

    }

    /**
     * 获取话题详情类型背景
     */
    fun getTopicDetailTypeBgIcon(topicTypeName:String):Int{
        return when(topicTypeName){
            "美食" -> R.mipmap.topic_detail_ms
            "民宿" ->R.mipmap.topic_detail_homestay
            "酒店" ->R.mipmap.topic_detail_hotel
            "游玩" ->R.mipmap.topic_detail_yw
            "探店" ->R.mipmap.topic_detail_td
            "文化" ->R.mipmap.topic_detail_wh
            else -> R.mipmap.topic_detail_ms
        }

    }
}