package com.daqsoft.provider.network.net

/**
 * User模块Api
 * @author PuHua
 * @version 1.0.0
 * @date 2019-6-13
 * @since JDK 1.8.0_191
 */
object UserApi {

    /**
     * 文件上传
     * http://file.geeker.com.cn/
     */
    const val FILE_UPLOAD = "https://file.geeker.com.cn/upload"
    /**
     * 登录
     */
    const val LOGIN = "user/api/userBind/login"
    /**
     * 短信验证码验证
     */
    const val CHECK_MSG = "res/api/sms/validateMsgCode"

    /**
     * 短信发送接口
     */
    const val SEND_MSG = "user/api/userBind/smsCode"
    /**
     * 绑定手机号发送验证码
     */
    const val BIND_SEND_MSG = "user/api/userBind/sendSmsCode"
    /**
     * 绑定手机号
     */
    const val BIND_PHONE = "user/api/userBind/bindPhone"
    /**
     * 注册接口
     */
    const val REGISTER = "user/api/userBind/register"

    /**
     * 获取个人信息
     */
    const val USER_INFO = "user/api/userInfo/getConsumUserInfo"
    /**
     * 个人信息更多
     */
    const val USER_INFO_MORE = "user/api/userInfo/getConsumUserInfoMore"
    /**
     * 修改个人资料
     */
    const val USER_UPDATE_USER_INFORMATION = "user/api/userInfo/updateUserInfo"
    /**
     * 获取省市区列表
     */
    const val LOCATIONS = "res/api/region/cityRegionModule"
    /**
     * 用户星座类型列表
     */
    const val CONSTELLATION_LIST = "user/api/userInfo/getConstellationTypeList"
    /**
     * 获取收货地址列表
     */
    const val RECEIVER_ADDRESS_LIST = "user/api/consumUserReceive/receiveList"
    /**
     * 添加收货地址
     */
    const val USER_ADD_ADDRESS = "user/api/consumUserReceive/saveReceive"

    /**通过订单获取健康*/
    const val HEALTH_INFO = "act/api/activityOrder/orderHealthInfo"

    /**自主核销*/
    const val AUTONOMY = "act/api/activityOrder/selfValidOrder"
    /**
     * 随行人列表
     */
    const val ATTACH_PERSON_LIST = "act/api/order/attached/getAttachedListMap"

    /**
     * 修改收货地址
     */
    const val USER_UPDATE_ADDRESS = "user/api/consumUserReceive/updateReceive"
    /**
     * 刪除收货地址
     */
    const val USER_DELETE_ADDRESSCT = "user/api/consumUserReceive/removeReceive"
    /**
     * 获取常用联系人列表
     */
    const val USER_CONTACT_MANAGEMENT_LIST = "user/api/consumUserContacts/contactsList2"
    /**
     * 添加联系人
     */
    const val USER_ADD_CONTACT = "user/api/consumUserContacts/saveContacts2"
    /**
     * 修改联系人
     */
    const val USER_UPDATE_CONTACT = "user/api/consumUserContacts/updateContactsSM4"
    /**
     * 证件类型列表
     */
    const val USER_CERT_TYPE_LIST = "user/api/userInfo/getCertTypeList"
    /**
     * 刪除联系人
     */
    const val USER_DELETE_CONTACT = "user/api/consumUserContacts/removeContacts"
    /**
     * 获取我的订单列表
     */
    const val USER_ORDER_LIST = "act/api/activityOrder/list"
    /**
     * 订单详情
     */
    const val USER_ORDER_DETAIL = "act/api/activityOrder/detail"

    /**
     * 核销详情
     */
    const val WRITE_OFF_DETAIL = "act/api/activityOrder/selfValidOrderInfo"

    /**
     * c端实名认证保存接口
     */
    const val SET_REAL_NAME_INFO = "res/api/realNameAuth/setRealNameInfoUseEbc"
    /**
     * 取消订单
     */
    const val ORDER_CANCEL_ORDER = "act/api/activityOrder/cancel"
    /**
     * 取消订单
     */
    const val CANCEL_ORDER = "userOrder/cancelOrder"
    /**
     * 取消酒店订单
     */
    const val CANCEL_HOTEL_ORDER = "userOrder/cancelHotelOrder"
    /**
     * 购物退款
     */
    const val MINE_ORDER_REFUND = "userOrder/orderRefundList"
    /**
     * 购物退款详情
     */
    const val MINE_ORDER_REFUND_DETAIL = "userOrder/orderRefundDetail"
    /**
     * 二维码
     */
    const val MINE_ORDER_TALENT_SUBSCRIBE = "user/getTalentSubscribe"
    /**
     * 取消线路订单
     */
    const val CANCEL_LINE_ORDER = "userOrder/cancelRouteOrder"
    /**
     * 刷新用户信息
     */
    const val USER_INFO_REFRESH = "user/api/userInfo/refresh"

    const val USER_FREE_INFO="user/free"
    /**
     * 撤回审核
     */
    const val WITH_DRAW_UPDATE = "res/api/realNameAuth/withdrawUpdate"
    /**
     * 审核信息查看
     */
    const val GET_REAL_NAME_INFO = "res/api/realNameAuth/getRealNameInfoUseEbc"
    /**
     * 获取消费码列表
     */
    const val CONSUME_LIST = "act/api/activityOrder/consumeList"
    /**
     * 消费码详情
     */
    const val CONSUME_DETAIL = "act/api/activityOrder/pubOrderInfo"
    /**
     * 站点信息
     */
    const val SITE_INOF = "user/api/site/siteInfo"
    /**
     * 查看个人诚信分
     */
    const val CREDIT_SCORE = "credit/creditUser/viewOne"

    /**
     * 查看身份验证状态
     */
    const val GET_REALNAME_VALIDATE_STATUS = "res/api/realNameAuth/getRealNameValidationStatus"

    /**
     * 获取当前信用守约次数
     */
    const val GET_USER_CURRENT_CREDIT = "credit/creditUser/currRecord"

    /**
     * 获取信用等级
     */
    const val GET_CREDIT_LEVEL = "credit/level/rule/list"

    /**
     * 获取信用记录
     */
    const val GET_CREDIT_RECORDS = "credit/score/list"

    /**
     * 获取上月诚信记录
     */
    const val GET_PRE_MONTH_RECORD = "credit/historyRecord/preMonthRecord"

    /**
     * 收藏的类型
     */
    const val COLLECTION_TYPE = "res/api/interactManage/vipCollectionType"
    /**
     * 收藏的列表
     */
    const val COLLECTION_LIST = "res/api/interactManage/vipCollectionList"
    /**
     * 个人中心互动故事、收藏等数据接口
     */
    const val MINE_CENTER_INFO = "user/api/userBind/personalCenterInfo"
    /**
     * 关注社团接口
     */
    const val MINE_FOCUS_MASS = "res/api/association/getFocusAssociationList"
    /**
     * 获取活动列表
     */
    const val ACTIVITY_LIST = "res/api/activity/getActivityList"

    /**
     * 关注社团动态接口
     */
    const val MINE_DYNAMIC = "res/api/es/dynamic"
    /**
     * 我的投诉列表
     */
    const val MINE_COMPLAINT_LIST = "res/api/complaints/list"
    /**
     * 我的投诉详情
     */
    const val MINE_COMPLAINT_DETAILS = "res/api/complaints/detail"
    /**
     * 满意度反馈
     */
    const val MINE_COMPLAINT_DETAILS_SATISFIED_BACK = "res/api/complaints/satisfiedFeedback"

    /**
     * 反馈次数
     */
    const val FEED_BACK_NUMS = "user/api/feedBack/feedBackNum"
    /**
     * 新增反馈
     */
    const val FEED_BACK_ADD = "user/api/feedBack/save"
    /**
     * 反馈记录
     */
    const val FEED_BACK_LS = "user/api/feedBack/list"
    /**
     * 我的预约列表
     */
    const val MINE_APPOINT_MENT_SC = "act/api/external/list"
    /**
     * 取消活动订单
     */
    const val CANCE_ACTIVITY_ORDER = "act/api/activityOrder/cancel"


    /**
     * 获取关注的非遗传承人
     */
    const val LEGACY_PEOPLE = "res/api/heritagePeople/fansPeopleList"
    /**
     * 我的邀请码
     */
    const val MINE_INVITE = "user/api/vipInvite/myInviteCode"
    /**
     * 使用邀请码
     */
    const val USER_INVITE_CODE = "user/api/vipInvite/bindInviteCode"
    /**
     * 邀请列表
     */
    const val MINE_INVITE_LS = "user/api/vipInvite/inviteList"
    /**
     * 邀请码详情
     */
    const val MINE_INVITE_CODE_INFO = "user/api/vipInvite/inviteInfo"
    /**
     * 消息列表
     */
    const val MINE_MESSAGE_LIST = "websocket/api/message/messageIndexInfo"
    /**
     * 各类型消息条数统计详情
     */
    const val MINE_MESSAGE_NUMBER = "websocket/api/message/messageNumInfo"
    /**
     * 全部已读
     */
    const val MINE_MESSAGE_ALL_READ = "websocket/api/message/readMessage"
    /**
     * 获取消息列表
     */
    const val MINE_MESSAGE_ALL_LIST = "websocket/api/message/messageList"
    /**
     * 获取志愿消息
     */
    const val MINE_MESSAGE_VOTE = "user/api/user/message/listByApi"
    /**
     * 获取通知详情
     */
    const val MINE_MESSAGE_NOTICE = "websocket/api/message/messageNotifyInfo"
    /**
     * 消息未读数非志愿者)
     */
    const val NO_READ_NUMBER = "user/api/user/message/getNotReadNum"

    /**
     * 消息未读数(志愿者)
     */
    const val NO_VOTE_NUMBER = "user/api/user/message/updateReadStatus"

}