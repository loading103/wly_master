package com.daqsoft.venuesmodule.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.base.BusinessCode
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.model.VenueResOrderModel

/**
 * @Description
 * @ClassName   ScenicReseravtionViewModel
 * @Author      luoyi
 * @Time        2020/7/13 16:09
 */
class ScenicReseravtionViewModel : BaseViewModel() {

    /**
     * 下单详情数据
     */
    var venueOrderViewInfoLiveData: MutableLiveData<VenueOrderViewInfo> = MutableLiveData()
    /**
     * 生成订单
     */
    var generVenuOrderLiveData: MutableLiveData<OrderResultBean> = MutableLiveData()
    /**
     * 场馆预约日期数据
     */
    var venueOrderDateListLiveData: MutableLiveData<VenueReservationInfo> = MutableLiveData()
    /**
     * 场馆预约数量
     */
    var venueOrderDateNumLiveData: MutableLiveData<MutableList<VenueDateNumBean>> = MutableLiveData()
    /**
     * 场馆预约时间段
     */
    var venueOrderTimesLiveData: MutableLiveData<MutableList<VenueOrderTime>> = MutableLiveData()
    /**
     * 该手机存在的订单数
     */
    val isNeedCode = MutableLiveData<Boolean>(true)
    /**
     * 发送手机验证码
     */
    val sendPhoneCodeLd = MutableLiveData<Boolean>()

    val vipInfold = MutableLiveData<VipInfoBean>()

    val activityFinishLd = MutableLiveData<Boolean>()

    fun getVenueOrderView(venueId: String, date: String) {
        mPresenter.value?.loading = false
        VenuesRepository.venuesService.getScenicOrderView(venueId)
            .excute(object : BaseObserver<VenueOrderViewInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VenueOrderViewInfo>) {
                    venueOrderViewInfoLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VenueOrderViewInfo>) {
                    mError.postValue(response)
                }

            })
    }

    /**
     * 生成预订场馆订单
     */
    fun createVenueOrder(bean: VenueResOrderModel?) {
        mPresenter.value?.loading = false
        var param = HashMap<String, String>()
        if (bean != null) {
            if (!bean.smsCode.isNullOrEmpty()) {
                param["code"] = bean.smsCode!!
            }
            param["userName"] = bean.name!!
            param["userPhone"] =SM4Util.encryptByEcb(bean.phone!!)
            param["orderNum"] = "1"
            param["orderType"] = "CONTENT_TYPE_SCENERY"
            param["channel"] = "app"
            param["idCard"] =SM4Util.encryptByEcb(bean.idCardNum!!)
            param["useNum"] = bean.userNum!!
            param["orderDate"] = bean.date!!
            if (!bean.cardType.isNullOrEmpty()) {
                param["cardType"] = bean.cardType!!
            }
            if (!bean.attachedJsonStr.isNullOrEmpty()) {
                param["attachedJsonStr"] = bean.attachedJsonStr!!
            }
            if (!bean.companyName.isNullOrEmpty()) {
                param["companyName"] = bean.companyName!!
            }
            if (bean.type == 1) {
                param["reservationType"] = "PERSON"
            } else {
                param["reservationType"] = "TEAM"
            }
            param["venueRuleId"] = bean.timesId!!
            param["venueId"] = bean.venueId!!
            param["venueOrderType"] = "0"
            if (!bean.healthCodeRegion.isNullOrEmpty()) {
                param["healthCodeRegion"] = bean.healthCodeRegion!!
            }

            VenuesRepository.venuesService.generateOrder(param)
                .excute(object : BaseObserver<OrderResultBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<OrderResultBean>) {
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_NAME, bean.name)
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_PHONE, bean.phone)
                        SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_IDCARD, bean.idCardNum)
                        if (!bean.cardType.isNullOrEmpty()) {
                            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                                .put(SPUtils.Config.APP_RESERATION_CARD_TYPE, bean.cardType)
                        }
                        generVenuOrderLiveData.postValue(response.data)
                    }

                    override fun onFailed(response: BaseResponse<OrderResultBean>) {
                        mError.postValue(response)
                    }
                })
        }
    }

    /**
     * 查询当前手机号存在的订单数
     */
    fun checkExistNumber(phoneNumber: String) {
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.getCheckExistOrderNumbers(phoneNumber, "CONTENT_TYPE_SCENERY")
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
     * 支付订单
     */
    fun payOrder(orderCode: String) {
        mPresenter?.value?.loading = false
        HomeRepository.service.payOrder(orderCode)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    saveOrder(orderCode)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_SUCCESS)
                        .withString("orderCode", orderCode)
                        .withString("faithAuditStatus", "")
                        .withString("orderType", ResourceType.CONTENT_TYPE_SCENERY)
                        .navigation()
                    response.requestFlag = BusinessCode.PAY_ERROR_CODE
                    mError.postValue(response)
                    activityFinishLd.postValue(true)
                }
            })
    }

    /**
     * 保存订单
     */
    var orderSuccess = MutableLiveData<Boolean>()

    fun saveOrder(orderCode: String) {
        mPresenter?.value?.loading = false
        HomeRepository.service.saveOrder(orderCode)
            .excute(object : BaseObserver<OrderSaveBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderSaveBean>) {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_SUCCESS)
                        .withString("orderCode", orderCode)
                        .withString("orderType", ResourceType.CONTENT_TYPE_SCENERY)
                        .withString("faithAuditStatus", "")
                        .navigation()
                    orderSuccess.postValue(true)
                    activityFinishLd.postValue(true)

                }

                override fun onFailed(response: BaseResponse<OrderSaveBean>) {
                    response.requestFlag = BusinessCode.ORDER_CALLBACK_ERRO_CODE
                    mError.postValue(response)
                }
            })
    }


    /**
     * 获取预约时间数目
     */
    fun getVenueOrderDateList(venueId: String, date: String, type: Int? = 1, number: Int = 0) {

        var param = HashMap<String, String>()
        param["scenicId"] = venueId
        param["date"] = date
        if (number >= 0) {
            param["number"] = number.toString()
        }
        if (type != null) {
            if (type == 2) {
                param["reservationType"] = "TEAM"
            } else {
                param["reservationType"] = "PERSON"
            }
        }
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.getScenicOrderDateList(param)
            .excute(object : BaseObserver<VenueReservationInfo>() {
                override fun onSuccess(response: BaseResponse<VenueReservationInfo>) {
                    venueOrderDateListLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VenueReservationInfo>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取时间预约状态
     */
    fun getVenueOrderDateNum(venueId: String, date: String, number: Int = 0) {
        mPresenter?.value?.loading = false
        var param = HashMap<String, String>()
        param["scenicId"] = venueId
        param["date"] = date
        if (number >= 0) {
            param["number"] = number.toString()
        }
        VenuesRepository.venuesService.getScenicOrderDateNum(param)
            .excute(object : BaseObserver<VenueDateNumBean>() {
                override fun onSuccess(response: BaseResponse<VenueDateNumBean>) {
                    venueOrderDateNumLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VenueDateNumBean>) {
                    mError.postValue(response)
                }

            })
    }

    /**
     * 订单时间段
     */
    fun getVenueOrderTimes(venueId: String, date: String, type: Int = 1) {
        mPresenter?.value?.loading = false
        var typeStr = if (type == 2) {
            "TEAM"
        } else {
            "PERSON"
        }
        VenuesRepository.venuesService.getScenicOrderTimes(venueId, date, typeStr)
            .excute(object : BaseObserver<VenueOrderTime>() {
                override fun onSuccess(response: BaseResponse<VenueOrderTime>) {
                    venueOrderTimesLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VenueOrderTime>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取用户信息
     */
    fun getVipInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        VenuesRepository.venuesService.getVipInfo().excute(object : BaseObserver<VipInfoBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<VipInfoBean>) {
                if (response.data != null) {
                    if (!response.data!!.idCard.isNullOrEmpty()) {
                        response.data!!.idCard = SM4Util.decryptByEcb(response.data!!.idCard)
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