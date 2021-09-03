package com.daqsoft.legacyModule.smriti.event

/**
 *@package:com.daqsoft.legacyModule.smriti.event
 *@date:2020/7/2 11:48
 *@author: caihj
 *@des:更新关注状态
 **/
class UpdateFocusStatusEvent(
    val id:String,
    val status:Boolean,
    val position:Int
)