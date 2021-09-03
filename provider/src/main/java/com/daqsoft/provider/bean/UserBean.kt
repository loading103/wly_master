package com.daqsoft.provider.bean

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description 用户信息实体(包括个人信息更多)
 * @ClassName   UserBean
 * @Author      PuHua
 * @Time        2019/11/1 16:09
 */
@Parcelize
data class UserBean(
    val headUrl: String,
    /**
     * 	实名认证状态 0:没有实名认证 1:已经实名认证 4:待审核 79：申请未通过 8:申请已撤
     */
    val realNameStatus: Int?,
    val nickName: String?,
    val phone: String?,
    val placeLocation: String?,
    val sex: String,
    val signature: String,
    val address: String,
    val birthday: String,
    val constellation: String,
    val email: String,
    val idCard: String,
    val school: String,
    val workplace: String,
    val unid: String?,
    val userCenterToken: String?
) : Parcelable

/**
 * 星座/证件类型
 */
data class ConstellationBean(
    val name: String,
    val value: String


) {
    /**
     * 此处重写是方便选择器显示
     */
    override fun toString(): String {
        return name
    }
}

/**
 * 登录用户信息实体
 */
data class UserLogin(
    val headUrl: String,
    val nickName: String,
    val openId: String,
    val phone: String,
    var mobile: String,
    val registerAgreement: Boolean,
    val sex: String,
    val siteId: Int,
    val storyReleaseAgreement: Boolean,
    val unid: String,
    val userCenterToken: String,
    val userToken: String,
    /**
     * 	实名认证状态 0:没有实名认证 1:已经实名认证 4:待审核 79：申请未通过 8:申请已撤
     */
    val realNameStatus: Int,
    val vipId: Int = -1,
    // 志愿者
    val volunteerStatus: Int,
    val encryption: String,
    val volunteerTeamStatus: Int
)

/**
 * 收货地址列表数据
 */
@Parcelize
data class ReceiveAddressBean(
    val address: String,
    val area: String,
    val consignee: String,
    val id: Int,
    val isDefault: Boolean,
    val phone: String,
    val receivingSn: String,
    val siteId: Int,
    val userId: Int
) : Parcelable

/**
 * 常用联系人实体
 */
@Parcelize
data class Contact(
    var certNumber: String?,
    var certType: String?,
    var contactSn: String?,
    val id: Int,
    var name: String?,
    var phone: String?,
    val siteId: Int,
    var type: Int,
    val userId: Int,
    var certTypeName: String?,
    var healthInfoBean: HelathInfoBean?,
    var companyName: String?
) : Parcelable

@Parcelize
data class ReseartionContact(
    var userCardType: String?,
    var userPhone: String?,
    var userName: String?,
    var userCardNumber: String?,
    var leader: Int,
    var healthCodeRegion: String?,
    var companyName: String?
) : Parcelable

class ReseartionContactExo(
    var userCardType: String?,
    var userPhone: String?,
    var userName: String?,
    var userCardNumber: String?,
    var leader: Int,
    var healthRegion: String?,
    var smsCode: String?,
    var userNum: String?,
    var companyName: String?
) {
    fun getReserationContact(): ReseartionContact {
        return ReseartionContact(
            userCardType,
            userPhone,
            userName,
            userCardNumber,
            leader,
            healthRegion,
            companyName
        )
    }
}

/**
 * 注册返回实体
 */
data class RegisterData(
    /**
     * 开通状态 0 未开通 1 已开通
     */
    val creditStatus: String,
    val headUrl: String,
    val idCard: String,
    val nickName: String,
    val openId: String,
    val phone: String,
    val realNameStatus: String,
    val registerAgreement: Boolean,
    val sex: String,
    val siteId: Int,
    val storyReleaseAgreement: Boolean,
    val unid: String,
    val userCenterToken: String,
    val userToken: String,
    val vipId: Int,
    // 志愿者
    val volunteerStatus: Int
)

/**
 * 站点信息
 */
data class SiteInfo(
    val auditNum: Int,
    val companyCount: Int,
    val companyDomain: String,
    val companyName: String,
    val contact: String,
    val contactPhone: String,
    val createTime: String,
    val creditStatus: Int,
    val email: String,
    val h5Domain: String,
    val id: Int,
    val name: String,
    val programDomain: String,
    val qrCode: String,
    val region: String,
    val regionName: String,
    val remark: String,
    val shopCode: String,
    val shopName: String,
    val shopUrl: String,
    val siteCode: String,
    val siteIntroduce: String,
    val siteLogo: String,
    val sitePhone: String,
    val siteType: String,
    val smsNum: Int,
    val status: Int,
    val totalAuditNum: Int,
    val totalNum: Int,
    val updateTime: String,
    val vipPlayStatus: Int,
    val emergencyPhone: String,
    val mergencyStatus: String?
)

/**
 * 电商免登之后返回的实体
 */
data class ElectronicLogin(
    val sessionId: String,
    val userInfo: UserInfo
)

/**
 * 小电商免登之后返回的用户实体
 */
data class UserInfo(
    val appletOpenid: Any,
    val headImageUrl: Any,
    val icon: String,
    val integral: Int,
    val memberId: Int,
    val memberStatus: Boolean,
    val mobile: String,
    val name: String,
    val nickname: Any,
    val openid: Any,
    val platformOpenid: Any,
    val siteCode: String,
    val supplierId: Any,
    val supplierSn: Any,
    val sysCode: String,
    val talentGrade: Any,
    val talentId: Any,
    val token: String,
    val type: Int,
    val unid: String,
    val userId: Int
)
