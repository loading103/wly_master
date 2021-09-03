package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   IdCardIdentifyBean
 * @Author      luoyi
 * @Time        2020/8/13 11:16
 */
data class IdCardIdentifyBean(
    /**
     * 签发机构
     */
    var org: String?,
    /**
     * 信息是否一致
     */
    var res: String?,
    /**
     * 状态
     */
    var status: String?,
    /**
     * 性别
     */
    var sex: String?,
    /**
     * 民族
     */
    var nation: String?,
    /**
     * 生日
     */
    var birthday: String?,
    /**
     * 地址
     */
    var address: String?,
    /**
     * 身份证号码
     */
    var idCard: String?,
    /**
     * 姓名
     */
    var name: String?,
    var positon:Int
)