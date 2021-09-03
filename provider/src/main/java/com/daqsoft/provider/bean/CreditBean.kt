package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description 个人诚信分
 * @ClassName   CreditBean
 * @Author      PuHua
 * @Time        2020/1/3 11:36
 */
data class CreditBean(
    // 诚信分数
    val creditScore: String,
    // 诚信状态(查看常量诚信状态)
    //  NORMAL			            正常
    //	SHORT_LOSE_CREDIT			短暂失信(到达失信有效期结束时间，恢复正常)
    //	LOSE_CREDIT			        失信
    val creditStatus: String,
    // 守约次数
    val keepPromiseCount: String,
    // 诚信等级
    val levelName: String,
    // 失信次数
    val loseCreditCount: String,
    // 失信有效期 - 结束时间
    val loseCreditEndTime: String,
    // 失信有效期- 开始时间
    val loseCreditStartTime: String,
    // 失约次数
    val losePromiseCount: String,
    // 最新失约时间
    val losePromiseTime: String,
    // 失约描述
    var loseDesc: String,
    // 手机号
    val phone: String
)

/**
 * 信用维度实体类
 */
data class CreditWdBean(
    val type: String,//维度类型：yyxw,预约行为,sftz,身份特征,syls,守约历史
    //维度图标
    val icon: Int,
    //维度标题
    val title: String,
    //维度内容
    val content: String,
    //维度左边文本
    val leftTxt: String,
    //维度右边文本
    val rightTxt: String,
    var realNameStatus: Boolean = false
)

/**
 * 当前守约次数
 */
data class CurrentCreditBean(
    //记录时间
    val recordTime: String,
    //当月守约次数
    val monthRecordNum: String,
    //总共守约次数
    val totalRecordNum: String,
    //信用分数
    val creditScore: String,
    //信用等级
    val level: String
)

/**
 * 信用等级
 */
@Parcelize
data class CreditLevelBean(
    //等级
    val level: String,
    //最小分数
    val minScore: String,
    //等级名称
    val name: String,
    //等级Id
    val id: String,
    //最大分数
    val maxScore: String,
    val creditRuleId: String,
    val description: String
) : Parcelable

/**
 * 上个月诚信记录
 */
data class CreditPreMonthBean(
    // 上月诚信分
    val creditScore: String,
    // 上月诚信状态
    val creditStatus: String,
    // 失约数
    val losePromiseCount: String,
    // 守约数
    val keepPromiseCount: String,
    // 统计时间类型
    val recordType: String,
    // 统计时间
    val recordTime: String,
    // 诚信用户ID
    val creditUserId: String,
    val id: String
)