package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/18 11:16
 *@author: caihj
 *@des:团队详情
 **/
data class VolunteerTeamDetailBean(
    val id:Int,
    val teamIcon:String,
    val teamName:String,
    val level:Int,
    val teamRegionName:String,
    val teamRegion:String,
    val ranking:Int,
    val teamCumulativeIntegral:Int,// 团队总积分
    val teamMemberNum:Int,// 团队成员数量
    val teamServiceTime:Int,// 团队服务时长
    val teamServiceNum:Int,// 团队服务数量
    val manageUnit:String,// 主管单位
    val teamPrincipalList:List<TeamPrincipal>,// 负责人
    val teamPhone:String,
    val teamAddress:String,
    val longitude:String,
    val latitude:String,
    val teamIntroduce:String,
    val serviceRegion:String,
    val signInMorningStarTime:String,// 上午开始签到时间
    val signInMorningEndTime:String,// 上午结束签到时间
    val signInAfternoonStarTime:String,// 下午开始签到时间
    val signInAfternoonEndTime:String,// 下午结束签到时间
    val signInNightStarTime:String,// 晚上开始签到时间
    val signInNightEndTime:String,// 晚上结束签到时间
    val applyServiceAdvance:Int,// a申请服务提前时间(单位:天)
    val volunteerJoinStatus:Int,// 会员加入状态 0:未加入 6:已加入 4:加入申请待审核 79:加入申请审核不通过
    val signInDateList:SignInDateList,
    val vipResourceStatus: VipResourceStatus,
    var likeNum:Int,
    var collectionNum:Int,
    var commentNum:Int,
    val openSignIn:Boolean

)

data class TeamPrincipal(
    val phone:String,
    val name:String,
    val head:String,
    val id:Int
)