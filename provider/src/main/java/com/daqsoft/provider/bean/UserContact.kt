package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   UserContact
 * @Author      luoyi
 * @Time        2020/8/3 16:25
 */
data class UserContact(
    var certNumber: String,
    var certType: String,
    var contactSn: String,
    var id: Int,
    var name: String,
    var phone: String,
    var siteId: Int,
    val userId: Int,
    var type: Int,
    var helathInfoBean: HelathInfoBean?
)
