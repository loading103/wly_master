package com.daqsoft.volunteer.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan

/**
 *@package:com.daqsoft.volunteer.utils
 *@date:2020/6/2 14:51
 *@author: caihj
 *@des:TODO
 **/
object StringUtils {
    fun getVolunteerTypeName(type:String):String{
        return when(type){
            "ACTIVITY_MODE_VOLUNT_SERVICE" -> "志愿服务"
            "ACTIVITY_MODE_VOLUNT" -> "志愿招募"
            "TEAM_ACTIVITY_SERVICE" -> "团队服务记录"
            else -> "未知"
        }
    }

    /**
     * 志愿者团队等级中文
     */
    fun getVolunteerTeamLevelStr(level:String):String = when(level){
        "1" -> "一星团队"
        "2" -> "二星团队"
        "3" -> "三星团队"
        "4" -> "四星团队"
        else -> "五星团队"

    }

    /**
     * 志愿者等级中文
     */
    fun getVolunteerLevelStr(level:String):String = when(level){
        "1" -> "一星志愿者"
        "2" -> "二星志愿者"
        "3" -> "三星志愿者"
        "4" -> "四星志愿者"
        else -> "五星志愿者"

    }

    /**
     * 服务时间
     */
    var serviceTimes = mutableListOf<String>(
        "不限",
        "休息日",
        "工作日"
    )
    val serviceType = mutableListOf<String>(
        "Unlimited",
        "RestDay",
        "WorkingDay"
    )

    fun getServiceTypeName(key:String):String {
        if(key.isNullOrEmpty()){
            return key
        }
        return serviceTimes[serviceType.indexOf(key)]
    }

    /**
     * 设置一个字符串中部分字体颜色
     *
     * @param content    内容
     * @param startIndex 从第几个开始
     * @param endIndex   到第几个结束
     * @return
     */
    fun setTextColor(content: String?,  colorId: Int, startIndex: Int, endIndex: Int): SpannableString? {
        val result = SpannableString(content)
        result.setSpan(ForegroundColorSpan(colorId), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return result
    }
}