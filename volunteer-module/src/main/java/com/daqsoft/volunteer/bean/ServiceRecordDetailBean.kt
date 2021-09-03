package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/8 13:58
 *@author: caihj
 *@des:志愿服务记录详情
 **/
data class ServiceRecordDetailBean(
    val audit:ServiceAudit,
    val resourceNum:ResourceNum,
    val content:String,
    val coverVideos:String,
    val createTime:String,
    val id:String,
    val images:String,
    val userLevel:String,// 用户星级 0是无星级 1-5
    val operateType:String,
    val vipResourceStatus: VipResourceStatus,
    val servicePeopleNum:Int,
    val serviceTarget:String,// 服务对象
    val serviceTime:Int,
    val userHead:String,
    val userName:String,
    val videos:String,
    val submitRelation:String,
    val status:Int,
    val showNum:Int,
    var likeNum:Int,
    var commentNum:Int,
    var collectionNum:Int,
    val activity:ServiceRecordActivity
)

data class ServiceAudit(
    val handleUserName:String,// 处理人名称
    val handleTime:String,// 处理时间
    val auditStatus:String,// 处理状态
    val auditResult:String,
    val resourceType:String,
    val resourceId:String,
    val createTime:String
)

data class ServiceRecordActivity(
    val useStartTime:String,
    val useEndTime:String,
    val serviceTime:Int,
    val servicePeople:Int,
    val name:String,
    val id:String,
    val address:String
)