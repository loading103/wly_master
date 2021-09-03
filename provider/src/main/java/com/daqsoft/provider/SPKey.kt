package com.daqsoft.provider

/**
 * SharePreferences Key
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-18
 * @since JDK 1.8.0_191
 */
object SPKey {
    /**
     * 实名认证状态 0:没有实名认证 1:已经实名认证 4:待审核 79：申请未通过 8:申请已撤
     */
    const val REALNAMESTATUS = "realNameStatus"

    /**
     * 用户中心token
     */
    const val USER_CENTER_TOKEN = "userCenterToken"

    /**
     * 用户id
     */
    const   val UUID = "uuid"

    /**
     * domain
     */
    const val DOMAIN = "domain"

    /**
     * sessionId: 29aa947c2db0a2c22f5bc35a3611a11d
     */
    const val SESSIONID = "sessionId"
    const val USERCENTERTOKEN = "USERCENTERTOKEN"
    const val UID = "uid"
    const val SITEID = "siteId"
    const val VIPID = "vipId"

    /**
     * 小电商免登录加密
     */
    const val ENCRYPTION = "ecryption"

    /**
     * 电话
     */
    const val PHONE = "phone"

    const val MOBILE = "moblie"

    /**
     * 电话
     */
    const val PHONESTR = "phone_str"

    /**
     * 站点编号
     */
    const val SITE_CODE = "site_code"


    const val ALLOW_KEY = "permissions"
    /**
     * 站点id
     */
    const val SITE_ID = "site_id"

    /**
     * h5网页
     */
    const val H5_DOMAIN = "h5_domain"

    /**
     * 站点region
     */
    const val SITE_REGION = "site_region"

    /**
     * 机器人是否开启
     */
    const val SITE_IT_ROBOT = "site_it_robot"


    /**
     * 水ying
     */
    const val SITE_WATERMARK = "site_watermark"

    /**
     * 昵称
     */
    const val NICK_NAME = "nickName"

    /**
     * 头像
     */
    const val HEAD_URL = "headUrl"

    /**
     * 省市区地区数据存储
     */
    const val LOCATION = "location"

    /**
     * 非遗传承人标志
     */
    const val HERITAGE_PEOPLEID = "heritagePeopleId"

    /**
     * 身份证标识
     */
    const val ID_CARD = "ID_CARD"

    /**
     * 商城地址（小电商）
     */
    const val SHOP_URL = "shopUrl"
    const val SHOP_CODE = "shopCode"


    /**
     * 地区
     */
    const val REGION = "region"

    /**
     * 地区名称
     */
    const val REGION_NAME = "region_name"

    /**
     * 志愿者和志愿者团队
     */
    const val VOLUNTEER = "volunteer"
    const val VOLUNTEER_TEAM = "volunteer_team"
}