package com.daqsoft.travelCultureModule.story.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@package:com.daqsoft.travelCultureModule.story.bean
 *@date:2020/4/3 10:43
 *@author: caihj
 *@des:话题数据
 **/
data class TopicBean(
    // 内容数量
    val contentNum: String,
    // 是否是热门数据
    val hot: Boolean,
    // 数据id
    val id: String,
    // 活动图片
    val image: String,
    // 活动名称
    val name: String,
    // 参与人数量
    val participateNum: String,
    // 浏览数量
    val showNum: String,
    // topicStatus        话题状态        number        话题状态 0：未开始 1：进行中 2：已结束
    val topicStatus: String,
    // 话题类型名称
    val topicTypeName: String,
    // 会员资源状态
    val vipResourceStatus: VipResourceStatus,
    //介绍
    val introduce:String,
    //规则
    val rule:String,
    //开始时间
    val startDate:String,
    //结束时间
    val endDate:String
)
/**
 * @des 用户资源状态
 * @author PuHua
 * @Date 2019/12/11 14:09
 */
@Parcelize
data class VipResourceStatus(
    var collectionStatus: Boolean,
    var likeStatus: Boolean
) : Parcelable