package com.daqsoft.provider.uiTemplate.titleBar.topic

import com.daqsoft.provider.R

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
    fun getTypeBgIcon(topicTypeName: String): Int {
        return when (topicTypeName) {
            "美食" -> R.mipmap.mode_topic_lable_food
            "民宿" -> R.mipmap.mode_topic_lable_house
            "酒店" -> R.mipmap.mode_topic_lable_hotel
            "游玩" -> R.mipmap.mode_topic_lable_play
            "探店" -> R.mipmap.mode_topic_lable_shop
            "文化" -> R.mipmap.mode_topic_lable_culture
            else -> R.mipmap.mode_topic_lable_food
        }

    }

    /**
     * 获取话题详情类型背景
     */
    fun getTopicDetailTypeBgIcon(topicTypeName: String): Int {
        return when (topicTypeName) {
            "美食" -> R.mipmap.mode_topic_lable_food
            "民宿" -> R.mipmap.mode_topic_lable_house
            "酒店" -> R.mipmap.mode_topic_lable_hotel
            "游玩" -> R.mipmap.mode_topic_lable_play
            "探店" -> R.mipmap.mode_topic_lable_shop
            "文化" -> R.mipmap.mode_topic_lable_culture
            else -> R.mipmap.mode_topic_lable_food
        }

    }
}