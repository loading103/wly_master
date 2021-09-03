package com.daqsoft.provider.businessview.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * @Description
 * @ClassName   EditUserContactViewModel
 * @Author      luoyi
 * @Time        2020/8/12 15:53
 */
class EditUserContactViewModel : BaseViewModel() {
    /**
     * 证件类型数据
     */
    val typeList = MutableLiveData<MutableList<ConstellationBean>>()
    /**
     * 常用联系人信息
     */
    val contacts = MediatorLiveData<MutableList<Contact>>()
    /**
     * 健康信息
     */
    val healthInfoLd: MutableLiveData<HelathInfoBean> = MutableLiveData()
    /**
     * 健康信息和注册信息
     */
    val healthInfoAndRegisterLd: MutableLiveData<HelathInfoBean> = MutableLiveData()
    /**
     * 健康码设置信息
     */
    val healthSetingLd: MutableLiveData<HelathSetingBean> = MutableLiveData()
    /**
     * 旅游码信息
     */
    val travelCodeInfoLd: MutableLiveData<Boolean> = MutableLiveData()
    /**
     * 健康出行地址信息
     */
    val healthRegionsLd: MutableLiveData<MutableList<HelathRegionBean>> = MutableLiveData()
    /**
     * 该手机存在的订单数
     */
    val isNeedCode = MutableLiveData<Boolean>(true)
    /**
     * 发送手机验证码
     */
    val sendPhoneCodeLd = MutableLiveData<Boolean>()
    /**
     * 实名认证信息
     */
    val vipInfold = MutableLiveData<VipInfoBean>()

    var currentCrdentType: String? = ""
    var currentCrdentName:String?=""

    /**
     * 获取证件类型
     */
    fun getCertTypeList() {
        mPresenter?.value?.loading = false
        UserRepository().userService
            .getCertTypeList()
            .excute(object : BaseObserver<ConstellationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConstellationBean>) {
                    typeList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取常用联系人列表
     */
    fun getContactList() {
        mPresenter.value?.loading = false
        UserRepository().userService.getContactList()
            .excute(object : BaseObserver<Contact>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Contact>) {
                    contacts.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<Contact>) {
                    contacts.postValue(null)
                }
            })
    }


    /**
     *获取健康信息
     */
    fun getHealthInfo(phone: String, region: String, name: String, idCard: String) {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getUserHealthInfo(
           phone,
            region,
            name,
           idCard,
            siteId
        )
            .excute(object : BaseObserver<HelathInfoBean>() {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    healthInfoLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathInfoBean>) {
                    healthInfoLd.postValue(null)
                }

            })
    }

    /**
     * 获取健康信息并注册旅游码
     */
    fun getHelathInfoAndReister(name: String, idCard: String, phone: String, region: String) {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getUserHealthInfoAndRegister(
          phone, region, name,
          idCard, siteId
        )
            .excute(object : BaseObserver<HelathInfoBean>() {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    healthInfoAndRegisterLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathInfoBean>) {
                    healthInfoAndRegisterLd.postValue(null)
                }

            })
    }

    /**
     * 获取健康码配置信息
     */
    /**
     * 获取健康码配置信息
     */
    fun getHealthSetingInfo(orgId: Int) {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        var orgIdStr: String = ""
        if (orgId > 0) {
            orgIdStr = orgId.toString()
        }
        VenuesRepository.venuesService.getHealthSetingInfo(siteId, orgIdStr)
            .excute(object : BaseObserver<HelathSetingBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(null)
                }
            })
    }

    /**
     * 获取旅游码详情
     */
    fun getTravelInfo(phone: String, name: String, idCard: String) {
        Log.e("idcard",idCard)
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthTravlInfo(
            phone,
            name,
           idCard,
            siteId
        ).excute(object : BaseObserver<Boolean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Boolean>) {
                travelCodeInfoLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<Boolean>) {
                travelCodeInfoLd.postValue(false)
            }
        })
    }

    /**
     * 获取健康码注册地
     */
    fun getHealthRegion() {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthRegionInfo(siteId)
            .excute(object : BaseObserver<HelathRegionBean>() {
                override fun onSuccess(response: BaseResponse<HelathRegionBean>) {
                    healthRegionsLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<HelathRegionBean>) {
                    healthRegionsLd.postValue(null)
                }
            })
    }

    /**
     * 查询当前手机号存在的订单数
     */
    fun checkExistNumber(phoneNumber: String,orderType:String) {
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.getCheckExistOrderNumbers(phoneNumber, orderType)
            .excute(object : BaseObserver<OrderNumberBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderNumberBean>) {
                    if (response.data!!.existNum == 0) {
                        isNeedCode.postValue(true)
                    } else {
                        isNeedCode.postValue(false)
                    }
                }
            })
    }

    /**
     * 发送手机验证码
     * @param phoneNumber 手机验证码
     */
    fun sendPhoneCode(phoneNumber: String) {
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.sendCode(phoneNumber)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        sendPhoneCodeLd.postValue(true)
                    } else {
                        toast.postValue(response.message)
                        sendPhoneCodeLd.postValue(false)
                    }

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    sendPhoneCodeLd.postValue(false)
                }
            })
    }

    /**
     * 获取用户信息
     */
    fun getVipInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        VenuesRepository.venuesService.getVipInfo()
            .excute(object : BaseObserver<VipInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VipInfoBean>) {
                    if (response.data != null) {
                        if (!response.data!!.idCard.isNullOrEmpty()) {
                            response.data!!.idCard =
                                SM4Util.decryptByEcb(response.data!!.idCard)
                        }
                    }
                    vipInfold?.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VipInfoBean>) {
                    vipInfold?.postValue(null)
                }
            })
    }
}