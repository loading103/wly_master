package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/5 10:38
 *@author: caihj
 *@des:志愿服务器记录(风采)实体类
 **/
data class ServiceRecordBean(
  val volunteerName:String,// 志愿名称
  val volunteerHead:String,// 志愿头像
  val videos:String,// 视频链接
  val resourceNum:ResourceNum,
  val operateType:String,// 记录类型
  val images:String,
  val id:String,
  val createTime:String,
  val coverVideos:String,
  val content:String,
  val audit:Audit,
  val activity:ServiceActivity,
  val volunteerLevel:Int
)

data class ResourceNum(
    val showNum:Int,
    val likeNum:Int,
    val commentNum:Int,
    val collectNum:Int
)

data class Audit(
    val handleUserName:String,
    val handleTime:String,
    val createTime:String,
    val auditStatus:String,
    val auditResult:String
)
data class ServiceActivity(
    val regionNameStr:String,
    val regionNameCityStr:String,
    val name:String,
    val id:String
)