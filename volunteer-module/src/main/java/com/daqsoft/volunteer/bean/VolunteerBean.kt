package com.daqsoft.volunteer.bean

import com.daqsoft.provider.bean.VipResourceStatus
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/3 10:56
 *@author: caihj
 *@des:志愿者实体类
 **/
data class VolunteerBean(
    val volunteerTeam:List<VolunteerTeam>,
    val serviceTime:Int,
    val serviceRegionName:String,// 服务地区名称
    val serviceRegion:String,
    val serviceNum:Int,
    val name:String,
    val level:Int,
    val id:String,
    val head:String,
    var likeNum:Int,
    var collectionNum:Int,
    val ranking:Int,
    val vipResourceStatus:VipResourceStatus,
    val cumulativeIntegral:Int,// 累积志愿积分
    val availableIntegral:Int // 可用志愿积分
){
    fun getTeamName():String{
        var str = StringBuilder()
        val size = volunteerTeam.size
      for (index in volunteerTeam.indices){
          str.append(volunteerTeam[index].teamName)
          if(size != index +1 ){
              str.append("、")
          }
      }
        return str.toString()
    }
}

data class VolunteerTeam(
    val teamName:String,
    val id:String
)


/**
 * @des 用户资源状态
 * @author PuHua
 * @Date 2019/12/11 14:09
 */
data class VipResourceStatus(
    var collectionStatus: Boolean,
    var likeStatus: Boolean,
    var fansStatus:Boolean
)

/**
 * 志愿者基本资料
 */
data class VolunteerBriefInfoBean(
    val sex:String,
    val nation:String, // 民族
    val name:String,
    val idCard:String,
    val phone:String,
    val serviceRegion:String,
    val serviceRegionName:String,// 服务地区
    val attributionName:String, //志愿者归属
    val attribution:String,
    val education:String,
    val school:String,
    val discipline:String,// 专业
    val employmentStatus:String, // 就业状态
    val idCardPortrait:String,
    val idCardNationalEmblem:String,
    val emergencyContactName:String,
    val emergencyContactPhone:String,
    val specialty:List<String>,
    val status:Int,
    val healthStatus:String,
    val language:List<String>,
    val serviceTimeType:String, // 服务时间类型
    val serviceIntention:List<String>,// 服务意向
    val email:String,
    val qq:String,
    val regionName:String,
    val address:String,
    val head:String,
    val id:String,
    val level:Int, // 等级
    val operateTime:String, // 操作时间
    val politicalAffiliation:String, //政治面貌
    val sn:String // 编号
)
/**
 * 志愿者基础资料
 */
data class VolunteerBasicBean(
    val availableIntegral:Int,// 可用积分
    val completeInfo:Boolean, // 信息是否完整
    val cumulativeIntegral:Int, // 累积积分
    val head:String,
    val id:String,
    val level:Int,
    val name:String,
    val ranking:Int,
    val serviceNum:Int,
    val serviceRegion:String,
    val serviceRegionName:String,
    val serviceTime:Int
)