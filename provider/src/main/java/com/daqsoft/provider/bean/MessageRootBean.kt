package com.daqsoft.provider.bean

import android.text.TextUtils
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.getRealImages
import org.jetbrains.anko.db.TEXT

data class MessageRootBean(
    var ImageUrl: Int,
    var name: String,
    val classify: String,
    var messageNum: String,
    var title: String,
    val type: String="0",
    var createTime: String="",
    val id: String="",
    val jumpType: String="",
    val jumpUrl: String="",
    val msgType: String="",
    val relationId: String="",
    var selected: String="0",
    val applyUserName: String="",
    var beInvitedPeopleName: String="",
    val changeIntegral: String="",
    var handlerUserName: String="",
    var messageContent: String="",
    var messageTitle: String="",
    val invitePeopleName: String="",
    val makeUpIntegral: String="",
    val me: Boolean=false,
    var messageClassify: String="",
    val messageType: String="",
    val changeIntegralType: String="",
    var operatePlatform: String="",
    var operateUser: String="",
    val removeMemberName: MutableList<String>?= mutableListOf(),
    val resourceStatus: String="",
    val serviceContent: String="",
    val teamName: String="",
    val platformSign: String=""
){

    fun getVotMessage(): String {
        var name1 = ""
        var memberNuber=0
        if (me) {
            name1 = "你"
        } else {
            name1 = handlerUserName
        }
        if(removeMemberName!=null){
            memberNuber=removeMemberName.size
        }
        if (resourceStatus == "1") {
            when (messageType) {
                "ACTIVITY_SIGN"->{
                    if(!TextUtils.isEmpty(messageTitle)){
                        return messageTitle
                    }else{
                        return "签到提醒"
                    }
                }
                else -> return "你收到了一条志愿者服务信息"
            }
        }

        else if (resourceStatus == "4") {
            when (messageType) {
                "signApply" -> return "${name1}收到了1条志愿服务签到审批信息"
                "signInMakeUpApply" -> return "${name1}收到了1条补录申请审批信息"
                "joinTeamApply" -> return "${name1}收到了1条入团申请信息"
                "scanCodeJoinTeam" -> return "${name1}收到了1条入团申请信息"
                "inviteJoinTeam" -> return "${name1}收到了1条入团申请信息"
                "removeFromTeam" -> return "${name1}移除了${memberNuber}位志愿者"
                "ACTIVITY_SIGN"->{
                    if(!TextUtils.isEmpty(messageTitle)){
                        return messageTitle
                    }else{
                        return "签到提醒"
                    }
                }

                else -> return "你收到了一条志愿者服务信息"

            }
        } else if (resourceStatus == "6") {
            when (messageType) {
                "signApply" -> return "你的签到申请已通过"
                "signInMakeUpApply" -> return "你的补录申请已通过"
                "joinTeamApply" -> return "你的入团申请已通过"
                "scanCodeJoinTeam" -> return "你的入团申请已通过"
                "inviteJoinTeam" -> return "恭喜你受邀加入：${teamName}"
                "removeFromTeam" -> return "你已被管理员移除团队"
                "removeFromTeam" -> return "你已被管理员移除团队"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "7") {
            when (messageType) {
                "removeFromTeam" -> return "你已被管理员移除团队"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "79") {
            when (messageType) {
                "signApply" -> return "你的签到申请未通过"
                "signInMakeUpApply" -> return "你的补录申请未通过"
                "joinTeamApply" -> return "你的入团申请未通过"
                "scanCodeJoinTeam" -> return "你的入团申请未通过"
                "inviteJoinTeam" -> return "你的入团申请未通过"
                "removeFromTeam" -> return "${name1}移除了${memberNuber}位志愿者失败"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "10") {
            return "你有一条签到提醒"
        } else if (resourceStatus == "11") {
            return "你有一条签到提醒"
        }else{
            if (resourceStatus == "4") {
                when (messageType) {
                    "signApply" -> return "定点服务通知"
                    "signInMakeUpApply" -> return "补录申请通知"
                    "joinTeamApply" -> return "入团申请通知"
                    "scanCodeJoinTeam" -> return "入团申请通知"
                    "inviteJoinTeam" -> return "入团申请通知"
                    "removeFromTeam" -> return "入团信息"
                    "CONTENT_TYPE_ACTIVITY" -> return "志愿活动通知"
                    "CONTENT_TYPE_ORDER" -> return "志愿报名申请通知"
                    "ACTIVITY_SIGN" -> return "签到提醒"
                    "USER_ACTIVITY_SERVICE_AUDIT" -> return "服务记录申请通知"
                    "CONTENT_TYPE_ORDER_AUDIT" -> return "志愿报名申请通知"
                    "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return "活动补录申请通知"
                    else -> return "你收到了一条志愿者服务信息"
                }
            } else {
                when (messageType) {
                    "signApply" -> return "定点服务审核结果"
                    "signInMakeUpApply" -> return "补录申请结果通知"
                    "joinTeamApply" -> return "入团申请审核结果"
                    "scanCodeJoinTeam" -> return "入团申请审核结果"
                    "inviteJoinTeam" -> return "入团申请审核结果"
                    "removeFromTeam" -> return "入团信息"
                    "CONTENT_TYPE_ACTIVITY" -> return "志愿活动审核结果"
                    "CONTENT_TYPE_ORDER" -> return "志愿报名审核结果"
                    "ACTIVITY_SIGN" -> return "签到提醒"
                    "USER_ACTIVITY_SERVICE_AUDIT" -> return "服务记录审核结果"
                    "CONTENT_TYPE_ORDER_AUDIT" -> return "志愿报名审核结果"
                    "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return "活动补录审核结果"
                    "TEAM_ACTIVITY_SERVICE" -> return "服务记录提交结果"
                    "ACTIVITY_SIGN" -> return "签到提醒"
                    "ACTIVITY_COMMENT_TEAM" -> return "志愿者评价结果"
                    "signRemind" -> {
                        if (resourceStatus == "11") {
                            return "签退提醒"
                        } else {
                            return "签到提醒"
                        }
                    }
                    else -> return "你收到了一条志愿者服务信息"
                }
            }
            return "你收到了一条志愿者服务信息"
        }
    }
}
data class MessageTopBean(
    var ImageUrl: Int,
    var name: String,
    var messageNum: String,
    var choosed: Boolean,
    val id: String
)
data class MessageTopNumberBean(
    var num: String,
    var type: String,
    val classify: String
)
data class NoticeDetailBean(
    var content: String,
    var coverImage: String,
    var describes: String,
    var id: String,
    var msgType: String,
    var notifyType: String,
    var title: String,
    val createTime: String
){
    fun getTime():String{
        return if(createTime.isNullOrBlank()){
            ""
        }else{
            createTime.substring(0,createTime.length-3)+"  发布"
        }
    }
}
data class MessageListBean(
    var content: String="",
    var createTime: String="",
    var image: String="",
    var isHeritage: String="",
    var jumpType: String="",
    var jumpUrl: String="",
    var nickName: String="",
    var relationId: String="",
    var relationUserId: String="",
    var relationTime: String="",
    var relationTitle: String="",
    var resourceType: String="",

    var title: String="",
    var messageTitle: String="",
    var messageContent: String="",
    var id: String="",

    val applyUserName: String="",
    var beInvitedPeopleName: String="",
    val changeIntegral: String="",
    var handlerUserName: String="",
    val invitePeopleName: String="",
    val makeUpIntegral: String="",
    val me: Boolean=false,
    var messageClassify: String="",
    var messageType: String="",
    val changeIntegralType: String="",
    var operatePlatform: String="",
    var operateUser: String="",
    val removeMemberName: MutableList<String>?=mutableListOf(),
    var resourceStatus: String="",
    val serviceContent: String="",
    val teamName: String="",
    val teamId: String="",
    var iconType: String="",
    val platformSign: String=""
) {
    fun getCreatTimes(): String{
        if(TextUtils.isEmpty(createTime)){
            return ""
        }
        val split = createTime.split(" ")
        return split[0]
    }

    fun getImaUrl(): String{
        return image.getRealImages()
    }
    fun getVotHeadMessage(): String {
        if (resourceStatus == "4") {
            when (messageType) {
                "signApply" -> return "定点服务通知"
                "signInMakeUpApply" -> return "补录申请通知"
                "joinTeamApply" -> return "入团申请通知"
                "scanCodeJoinTeam" -> return "入团申请通知"
                "inviteJoinTeam" -> return "入团申请通知"
                "removeFromTeam" -> return "入团信息"
                "CONTENT_TYPE_ACTIVITY" -> return "志愿活动通知"
                "CONTENT_TYPE_ORDER" -> return "志愿报名申请通知"
                "ACTIVITY_SIGN" -> return "签到提醒"
                "USER_ACTIVITY_SERVICE_AUDIT" -> return "服务记录申请通知"
                "CONTENT_TYPE_ORDER_AUDIT" -> return "志愿报名申请通知"
                "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return "活动补录申请通知"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else {
            when (messageType) {
                "signApply" -> return "定点服务审核结果"
                "signInMakeUpApply" -> return "补录申请结果通知"
                "joinTeamApply" -> return "入团申请审核结果"
                "scanCodeJoinTeam" -> return "入团申请审核结果"
                "inviteJoinTeam" -> return "入团申请审核结果"
                "removeFromTeam" -> return "入团信息"
                "CONTENT_TYPE_ACTIVITY" -> return "志愿活动审核结果"
                "CONTENT_TYPE_ORDER" -> return "志愿报名审核结果"
                "ACTIVITY_SIGN" -> return "签到提醒"
                "USER_ACTIVITY_SERVICE_AUDIT" -> return "服务记录审核结果"
                "CONTENT_TYPE_ORDER_AUDIT" -> return "志愿报名审核结果"
                "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return "活动补录审核结果"
                "TEAM_ACTIVITY_SERVICE" -> return "服务记录提交结果"
                "ACTIVITY_SIGN" -> return "签到提醒"
                "ACTIVITY_COMMENT_TEAM" -> return "志愿者评价通知"
                "signRemind" -> {
                    if (resourceStatus == "11") {
                        return "签退提醒"
                    } else {
                        return "签到提醒"
                    }
                }
                else -> return "你收到了一条志愿者服务信息"
            }
        }
    }

    /**
     * 从来没见过让前端这么瓜的数据组装
     */
    fun getVotMessage(): String {
        var name1 = ""
        var memberNuber = 0
        if (me) {
            name1 = "你"
        } else {
//            name1 = handlerUserName
            name1 = "你"
        }
        if (removeMemberName != null) {
            memberNuber = removeMemberName.size
        }
        if (resourceStatus == "4") {
            when (messageType) {
                "signApply" -> return "${name1}收到了1条志愿服务签到审批信息"
                "signInMakeUpApply" -> return "${name1}收到了1条补录申请审批信息"
                "joinTeamApply" -> return "${name1}收到了1条入团申请信息"
                "scanCodeJoinTeam" -> return "${name1}收到了1条入团申请信息"
                "inviteJoinTeam" -> return "${name1}收到了1条入团申请信息"
                "removeFromTeam" -> return "${name1}移除了${memberNuber}位志愿者"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "6") {
            when (messageType) {
                "signApply" -> return "你的签到申请已通过"
                "signInMakeUpApply" -> return "你的补录申请已通过"
                "joinTeamApply" -> return "你的入团申请已通过"
                "scanCodeJoinTeam" -> return "你的入团申请已通过"
                "inviteJoinTeam" -> return "恭喜你受邀加入：${teamName}"
                "removeFromTeam" -> return "你已被管理员移除团队"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "7") {
            when (messageType) {
                "removeFromTeam" -> return "你已被管理员移除团队"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "79") {
            when (messageType) {
                "signApply" -> return "你的签到申请未通过"
                "signInMakeUpApply" -> return "你的补录申请未通过"
                "joinTeamApply" -> return "你的入团申请未通过"
                "scanCodeJoinTeam" -> return "你的入团申请未通过"
                "inviteJoinTeam" -> return "你的入团申请未通过"
                "removeFromTeam" -> return "${name1}移除了${memberNuber}位志愿者失败"
                else -> return "你收到了一条志愿者服务信息"
            }
        } else if (resourceStatus == "10") {
            return "你有一条签到提醒"
        } else if (resourceStatus == "11") {
            return "你有一条签退提醒"
        } else if (resourceStatus == "2") {
            when (messageType) {
                "ACTIVITY_COMMENT_TEAM" -> return "志愿活动审核结果"
                "TEAM_ACTIVITY_SERVICE" -> return "服务记录审核结果"
            }
        }

        return "志愿者服务消息"
    }

    fun getVotPathUrl(): String {
        when (messageType) {
            "signApply" -> return "${BaseApplication.webSiteUrl}message-sign-in-detail/${id}"
            "signInMakeUpApply" -> return "${BaseApplication.webSiteUrl}message-makeUp-detail/${id}"
            "joinTeamApply" -> return "${BaseApplication.webSiteUrl}message-joinTeam-detail/${id}/1"
            "scanCodeJoinTeam" -> return "${BaseApplication.webSiteUrl}message-joinTeam-detail/${id}/2"
            "inviteJoinTeam" -> return "${BaseApplication.webSiteUrl}message-joinTeam-detail/${id}/2"
            "removeFromTeam" -> return "${BaseApplication.webSiteUrl}message-remove-detail/${id}"
            "CONTENT_TYPE_ORDER" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/volunteer/message-${relationId}"
            "CONTENT_TYPE_ORDER_AUDIT" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/volunteer/message-${relationId}"
            "CONTENT_TYPE_ACTIVITY" -> {
                if (resourceStatus == "3") {
                    return "${BaseApplication.webSiteUrl}message-recruitment-completed/${relationId}"
                } else {
                    return "${BaseApplication.webSiteUrl}volunteer-team-desc/${relationId}"
                }
            }
            "USER_ACTIVITY_SERVICE" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/serviceRecord/message-${relationId}"
            "TEAM_ACTIVITY_SERVICE" -> return "${BaseApplication.webSiteUrl}service-record-write/${teamId}"
            "USER_ACTIVITY_SERVICE_AUDIT" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/serviceRecord/message-${relationId}"
            "VOLUNTEER_SIGN_REVAMP" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/activitRevamp/message-${relationId}"
            "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return "${BaseApplication.webSiteUrl}volunteer-review-detail/activitRevamp/message-/${relationId}"

            "ACTIVITY_SIGN" -> return "${BaseApplication.webSiteUrl}sign-in-activity-detail/${relationId}"
            "signRemind" -> return "${BaseApplication.webSiteUrl}volunteer-sign-in"
            "ACTIVITY_COMMENT_TEAM" -> return "${BaseApplication.webSiteUrl}volunteer-comment/${teamId}"
        }
        return ""
    }

    fun getTypeContent(): String {
        when (messageType) {
            "signApply" -> return ""
            "signInMakeUpApply" -> return ""
            "joinTeamApply" -> return messageContent
            "scanCodeJoinTeam" -> return messageContent
            "inviteJoinTeam" -> return messageContent
            "removeFromTeam" -> return messageContent
            "CONTENT_TYPE_ORDER" -> return messageContent
            "CONTENT_TYPE_ORDER_AUDIT" -> return messageContent
            "CONTENT_TYPE_ACTIVITY" -> return "活动名称：${messageContent}"
            "USER_ACTIVITY_SERVICE" -> return messageContent
            "TEAM_ACTIVITY_SERVICE" -> return messageContent
            "USER_ACTIVITY_SERVICE_AUDIT" -> return messageContent
            "VOLUNTEER_SIGN_REVAMP" -> return messageContent
            "VOLUNTEER_SIGN_REVAMP_AUDIT" -> return messageContent

            "ACTIVITY_SIGN" -> return messageContent
            "signRemind" -> return serviceContent

            "ACTIVITY_COMMENT_TEAM" -> return messageContent
            else -> return ""

        }
    }

    fun getApplyName(): String {
        when (messageType) {
            "signApply" -> return ""

            else -> {
                if (TextUtils.isEmpty(applyUserName)) {
                    return ""
                } else {
                    return "申请人：${applyUserName}"
                }
            }
        }

    }

    fun getType(): String {
        when (resourceType) {
            ResourceType.CONTENT_TYPE_PARKING ->  return  "停车场"
            ResourceType.CONTENT_TYPE_TOILET->  return   "厕所"
            ResourceType.CONTENT_TYPE_RESTAURANT->  return   "餐饮"
            ResourceType.CONTENT_TYPE_HOTEL_ROOM->  return   "酒店房间"
            ResourceType.CONTENT_TYPE_HOTEL->  return   "酒店"
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS->  return   "景点"
            ResourceType.CONTENT_TYPE_SCENERY->  return   "景区"
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU->  return   "活动室"
            ResourceType.CONTENT_TYPE_VENUE->  return   "场馆"
            ResourceType.CONTENT_TYPE_ACTIVITY->  return   "活动"
            ResourceType.CONTENT->  return   "内容"
            ResourceType.MEDIA->  return   "媒体"
            ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM->  return   "志愿者团队"
            ResourceType.CONTENT_TYPE_VOLUNTEER->  return   "志愿者"
            ResourceType.CONTENT_TYPE_ASSOCIATION_PERSON->  return   "社团成员"
            ResourceType.CONTENT_TYPE_ASSOCIATION->  return   "社团"
            ResourceType.CONTENT_TYPE_ACTIVITY_VOLUNT->  return   "志愿者活动"
            ResourceType.CONTENT_TYPE_TOPIC->  return   "话题"
            ResourceType.CONTENT_TYPE_STORY->  return   "故事"
            ResourceType.CONTENT_TYPE_BRAND->  return   "品牌"
            ResourceType.CONTENT_TYPE_STORY_TAG->  return   "标签"
            ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE->  return   "非遗保护基地"
            ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE->  return   "非遗传习基地"
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE->  return   "非遗体验基地"
            ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE->  return   "非遗传承人"
            ResourceType.CONTENT_TYPE_HERITAGE_ITEM->  return   "非遗项目"
            ResourceType.CONTENT_TYPE_SITE_INDEX->  return   "城市名片"
            ResourceType.CONTENT_TYPE_AGRITAINMENT->  return   "农家乐"
            ResourceType.CONTENT_TYPE_COURSE->  return   "大讲堂"
            ResourceType.CONTENT_TYPE_ACTIVITY_RESULT->  return   "活动成果"
            ResourceType.TEAM_ACTIVITY_SERVICE->  return   "志愿活动 团队服务"
            ResourceType.USER_ACTIVITY_SERVICE->  return   "志愿活动 个人服务"
            ResourceType.VOLUNTEER_SIGN_REVAMP->  return   "签到补录"
            ResourceType.CONTENT_TYPE_COUNTRY->  return   "乡村"
            ResourceType.CONTENT_TYPE_RURAL_SPOTS->  return   "景观点"
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT->  return   "娱乐场所"
        }
        return ""
    }
}
