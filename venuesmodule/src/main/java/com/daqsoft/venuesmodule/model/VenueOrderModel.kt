package com.daqsoft.venuesmodule.model

/**
 * @Description 场馆预订model
 * @ClassName   VenueOrderModel
 * @Author      luoyi
 * @Time        2020/5/13 17:29
 */
class VenueOrderModel {
    /**
     * 短信验证码
     * 第一次使用该手机号预定时【必填】
     */
    var code: String = ""
    /**
     * 用户名称
     * 【必填】
     */
    var userName: String = ""
    /**
     * 手机号
     * 【必填】
     */
    var userPhone: String = ""
    /**
     * 订单数量
     *  【必填】默认 1
     */
    var orderNum: Int = 1
    /**
     *  订单类型
     *  【必填】CONTENT_TYPE_ACTIVITY:预定活动, CONTENT_TYPE_ACTIVITY_SHIU:预定文化场馆
     */
    var orderType: String = ""
    /**
     *  备注
     */
    var remark: String = ""
    /**
     * 预定渠道
     * 【必填】查看常量接口
     */
    var channel: String = ""
    /**
     * 身份证号
     * 【必填】
     */
    var idCard: String = ""
    /**
     *  使用人数
     *   预订活动室、场馆时 【必填】
     */
    var useNum: Int = 0
    /**
     * 场馆预定时间
     * 场馆订单【必填】yyyy-MM-dd
     */
    var orderDate: String = ""
    /**
     *   场馆-单位名称
     *    场馆订单选填
     */
    var companyName: String = ""
    /**
     * 场馆-预定类型
     * 场馆订单【必填】TEAM：团队预定，PERSON：个人预定
     *
     */
    var reservationType: String = ""
    /**
     * 场馆-期望到达时间
     *  场馆订单选填 yyyy-MM-dd HH:mm
     */
    var expectedTime: String = ""
    /**
     * 场馆-成人数量
     *  场馆订单选填
     */
    var adultNum: Int = 0
    /**
     * 场馆-小孩数量
     * 场馆订单选填
     */
    var childNum: Int = 0
    /**
     *  场馆订单选填
     *  场馆-青年数量
     */
    var teenagerNum: Int = 0
    /**
     * 场馆-老人数量
     *  场馆订单选填
     */
    var oldManNum: Int = 0
    /**
     *   场馆预定时间ID    number    预定场馆【必填】
     */
    var venueRuleId: Int = 0
    /**
     * 场馆ID
     *  预定场馆【必填】
     */
    var venueId: Int = 0
    /**
     *  讲解语言
     *   string    【讲解员必填】EN:英语，CH：中文
     */
    var guideLanguage: String = "CH"
    /**
     *  讲解时间段 ID    number    【讲解员必填】
     */
    var guideOrderTimeId: Int = 0
    /**
     *  讲解员展厅 ID    number    【讲解员必填】多个，号隔开
     */
    var guideExhibitionIds: Int = 0
    /**
     *  场馆订单类型    number    0:场馆订单，1 场馆 + 讲解订单，2 讲解订单（不填默认场馆订单）
     */
    var venueOrderType: Int = 0
    /**
     *   已存在的场馆关联关系订单编码    string    单独预约讲解员【必填】、预约已存在讲解员订单的场馆订单【必填】
     */
    var existVenueRelationOrderCode: String = ""

    /**
     * 获取已经选择的入场人数
     */
    public fun getHaveSelectUserNum(): Int {
        return adultNum + childNum + teenagerNum + oldManNum
    }

    /**
     * 生成 生成订单的参数
     */
    public fun getVenueReservationParams(): HashMap<String, String> {
        var params: HashMap<String, String> = hashMapOf()
        params["venueId"] = venueId.toString()
        params["code"] = code
        params["userName"] = userName
        params["userPhone"] = userPhone
        params["orderNum"] = orderNum.toString()
        params["orderType"] = orderType
        if (!remark.isNullOrEmpty()) {
            params["remark"] = remark
        }
        params["idCard"] = idCard
        params["useNum"] = useNum.toString()
        params["orderDate"] = orderDate
        if (!companyName.isNullOrEmpty()) {
            params["companyName"] = companyName
        }
        params["reservationType"] = reservationType
        if (!expectedTime.isNullOrEmpty()) {
            params["expectedTime"] = expectedTime
        }
        params["adultNum"] = adultNum.toString()
        params["childNum"] = childNum.toString()
        params["teenagerNum"] = teenagerNum.toString()
        params["oldManNum"] = oldManNum.toString()
        params["venueRuleId"] = venueRuleId.toString()
        params["venueOrderType"] = "0"
        return params
    }
}