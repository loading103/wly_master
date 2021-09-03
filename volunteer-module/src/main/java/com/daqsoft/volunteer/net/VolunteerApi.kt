package com.daqsoft.volunteer.net

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/1 14:38
 *@author: caihj
 *@des:志愿者api
 **/
internal object VolunteerApi {

    /**
     * 志愿者统计接口
     * get
     */
    const val VOLUNTEER_COUNT = "res/api/volunteer/volunteerAndTeamNum"

    /**
     * 我的关注
     */
    const val VOLUNTEER_FOCUS = "res/api/activity/getFansTeamList"

    /**
     * 志愿者和志愿者团队排行榜
     */
    const val VOLUNTEER_RANK_LIST = "res/api/volunteer/getRankingList"

    /**
     * 个人排行榜
     */
    const val VOLUNTEER_RANK = "res/api/volunteer/getRanking"

    /**
     * 志愿活动列表（志愿招募）
     */
    const val VOLUNTEER_ACTIVITY_LIST = "res/api/activity/getActivityList"

    /**
     * 志愿者列表
     */
    const val VOLUNTEER_LIST = "res/api/volunteer/list"

    /**
     * 志愿者团队列表
     */
    const val VOLUNTEER_TEAM_LIST = "res/api/volunteerTeam/list"

    /**
     * 团队详情
     */
    const val VOLUNTEER_TEAM_DETAIL = "res/api/volunteerTeam/view"

    /**
     * 获取志愿者类型信息
     */
    const val VOLUNTEER_TYPE = "res/api/volunteer/getDictValue"

    /**
     * 志愿者归属列表
     */
    const val VOLUNTEER_REGIONS = "res/api/volunteer/attributionList"

    /**
     * 志愿者注册申请
     */
    const val VOLUNTEER_REGISTER = "res/api/volunteer/apply"

    /**
     * 更新志愿者信息
     */
    const val VOLUNTEER_UPDATE_INFO = "res/api/volunteer/updateVolunteerInfo"

    /**
     * 志愿者团队注册申请
     */
    const val VOLUNTEER_TEAM_REGISTER = "res/api/volunteerTeam/registeredVolunteerTeam"

    /**
     * 取消志愿者申请
     */
    const val VOLUNTEER_CANCEL_APPLY = "res/api/volunteer/withdrawApply"

    /**
     * 志愿团队注册撤销
     */
    const val VOLUNTEER_TEAM_CANCEL_APPLY = "res/api/volunteerTeam/cancelRegistered"

    /**
     * 志愿者详情
     */
    const val VOLUNTEER_DETAIL = "res/api/volunteer/detail"

    /**
     * 志愿者所属团队列表
     */
    const val VOLUNTEER_DETAIL_TEAM_LIST = "res/api/volunteerTeam/apiVolunteerTeamList"

    /**
     * 个人志愿服务
     */
    const val VOLUNTEER_SERVICE_LIST = "res/api/activity/service/userListByApi"

    /**
     * 志愿者团队签到列表
     */
    const val VOLUNTEER_TEAM_SIGN_LIST = "res/api/volunteerTeam/apiSignInTeamList"

    /**
     * 团队志愿者服务记录列表
     */
    const val VOLUNTEER_SERVICE_RECORD_LIST = "res/api/activity/service/listByApi"

    /**
     * 志愿服务记录详情
     */
    const val VOLUNTEER_SERVICE_RECORD_DETAIL = "res/api/activity/service/infoByApi"

    /**
     * 志愿品牌列表
     */
    const val VOLUNTEER_BRAND_LIST = "res/api/volunteerItemBrand/apiList"

    /**
     * 志愿品牌详情
     */
    const val VOLUNTEER_BRAND_DETAIL = "res/api/volunteerItemBrand/apiDetail"

    /**
     * 团队成员
     */
    const val VOLUNTEER_TEAM_MEMBER_LIST = "res/api/volunteerTeam/teamMemberList"

    /**
     * 志愿者签到月历
     */
    const val VOLUNTEER_SIGN_DATE = "res/api/volunteerSignInService/signInMonthlyCalendar"

    /**
     * 签到申请
     */
    const val VOLUNTEER_SIGN_APPLY = "res/api/volunteerSignInService/apply"

    /**
     * 站点下级区域(两层)
     */
    const val SITE_CHILD_REGION = "res/api/region/siteChildRegion"

    /**
     * 发送验证码
     */
    const val SEND_CODE = "res/api/sms/sendMsgCode"

    /**
     * 校验验证码
     */
    const val VALIDATE_CODE = "res/api/sms/validateMsgCode"

    /**
     * 获取志愿者信息
     */
    const val VOLUNTEER_BRIEF_INFO = "res/api/volunteer/personalCompleteInfo"

    /**
     * 获取志愿者团队完整信息
     */
    const val VOLUNTEER_TEAM_INFO = "res/api/volunteerTeam/personalTeamCompleteInfo"

    /**
     * 获取志愿者基本信息
     */
    const val VOLUNTEER_BASIC_INFO = "res/api/volunteer/personalBasicInfo"

    /**
     * 获取最新志愿者操作状态
     */
    const val VOLUNTEER_OPERATE_STATUS = "res/api/volunteer/getLatestOperateStatus"
    /**
     * 获取最新志愿者团队操作状态
     */
    const val VOLUNTEER_TEAM_OPERATE_STATUS = "res/api/volunteerTeam/getLatestOperateStatus"

    /**
     * 获取审核记录
     */
    const val VOLUNTEER_AUDIT_LOG = "res/api/volunteer/auditLog"

}