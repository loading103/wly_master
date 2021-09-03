package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/1 16:07
 *@author: caihj
 *@des:志愿者排行榜实体类
 **/
data class VolunteerRankBean(
    val upper:Int,// 上一个榜单时间 为0没有上一个
    val next:Int,// 下一个榜单时间 为0没有下一个
    val totalCount:Int,
    val pageSize:Int,
    val totalPage:Int,
    val currPage:Int,
    val nextCycleCountdown:Long,
    val data:List<RankData>
)

data class RankData(
    val head:String, // 头像
    val name:String, // 名称
    val id:String,
    val rank:Int, // 名次
    val serviceRegionName:String,
    val total:String // 积分/时长
)