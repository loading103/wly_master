package com.daqsoft.integralmodule.repository.bean

/**
 * 免登陆实体列
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-22
 * @since JDK 1.8.0_191
 */
data class FreeLoginBean(
    val sessionId: String?,
    val userInfo: UserInfo?
){
    data class UserInfo(
        val appletOpenid: Any?,
        val headImageUrl: Any?,
        val icon: String?,
        val integral: Int?,
        val memberId: Int?,
        val memberStatus: Boolean?,
        val mobile: String?,
        val name: String?,
        val nickname: Any?,
        val openid: Any?,
        val platformOpenid: Any?,
        val siteCode: String?,
        val supplierId: Any?,
        val supplierSn: Any?,
        val sysCode: String?,
        val talentGrade: Any?,
        val talentId: Any?,
        val token: String?,
        val type: Int?,
        val unid: String?,
        val userId: Int?
    )
}

