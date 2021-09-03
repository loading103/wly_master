package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/7/1 9:38
 *@author: caihj
 *@des:志愿者操作状态实体
 **/
data class OperateStatusBean(
    val operateTime:String,
    val operateResult:String,
    val operateStatus:String //  4：待审核 8：撤回志愿者申请 79：志愿者申请不通过
)