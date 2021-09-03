package com.daqsoft.provider.bean

/**
 *@package:com.daqsoft.usermodule.bean
 *@date:2020/5/28 17:20
 *@author: caihj
 *@des:关注非遗传承人实体类
 **/
data class LegacyPeopleFansBean(
    val fans: Int,
    val fansStatus: Int,
    val fansTime: String,
    val headUrl: String,
    val heritagePeopleId: String,
    val heritagePeopleType: String,
    val nickName:String,
    val showNum: Int,
    val storyNum: Int
)