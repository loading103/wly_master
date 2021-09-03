package com.daqsoft.provider.bean

/**
 *@package:com.daqsoft.usermodule.bean
 *@date:2020/4/10 13:40
 *@author: caihj
 *@des:诚信分实体
 **/
data class CreditScoreBean(
    // 业务平台资源值
    val resourceValue:String,
    // 业务平台资源类型
    val resourceType:String,
    // 业务平台资源名称
    val resourceName:String,
    // 变更时间
    val modifyTime:String,
    // 变更分数
    val modifyScore:String,
    // 行为 icon
    val icon:String,
    // 行为code
    val creditRuleCode:String,
    // 行为名称
    val ruleName:String,
    // 守约操作描述
    val keepPromiseDesc:String,
    // 失约操作描述
    val losePromiseDesc:String,
    // 平台名称
    val platformName:String,
    // 操作记录Id
    val  id:String,
    // 是否为本平台记录：1：本平台，0：非本平台
    val  isThisPlatform:Int
    )