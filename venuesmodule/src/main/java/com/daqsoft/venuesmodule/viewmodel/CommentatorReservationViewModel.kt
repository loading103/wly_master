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
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.base.BusinessCode
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.model.VenueCttrOrderModel
import com.daqsoft.venuesmodule.model.VenueResOrderModel

/**
 * @Description 讲解员预约
 * @ClassName   CommentatorReservationViewModel
 * @Author      luoyi
 * @Time        2020/7/4 13:40
 */
class CommentatorReservationViewModel : BaseViewModel() {

    /**
     * 订单信息
     */
    var venueOderInfoLd: MutableLiveData<CommentaryOrderInfoBean> = MutableLiveData()
    /**
     * 生成订单
     */
    var generVenuOrderLiveData: MutableLiveData<OrderResultBean> = MutableLiveData()
    /**
     * 讲解
     */
    var guideOrderInfoLd: MutableLiveData<MutableList<GuideOrderInfo>> = MutableLiveData()

    var guideInfoLd: MutableLiveData<CommetaryInfoBean> = MutableLiveData()

    var activityFinishLd: MutableLiveData<Boolean> = MutableLiveData()
    /**
     * 是否只预约讲解员
     */
    var isOnlyCommtator: Boolean = false

    /**
     * @param venueId 场馆 ID
     * @param guideDate 日期
     */
    fun getGuiderOrderInfo(venueId: String, guideDate: String) {
        VenuesRepository.venuesService.getHavedGuideInfo(venueId, guideDate)
            .excute(object : BaseObserver<GuideOrderInfo>() {
                override fun onSuccess(response: BaseResponse<GuideOrderInfo>) {
                    guideOrderInfoLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<GuideOrderInfo>) {
                    guideOrderInfoLd.postValue(null)
                }

            })
    }

    /**
     * @param venueId 场馆Id
     * @param reservationType 预约类型
     * @param date 日期
     */
    fun getGuideInfo(venueId: String, reservationType: Int, date: String) {
        var type: String = ""
        if (reservationType == 1) {
            type = "PERSON"
        } else {
            type = "TEAM"
        }
        VenuesRepository.venuesService.getGuiderInfo(venueId, type, date)
            .excute(object : BaseObserver<CommetaryInfoBean>() {
                override fun onSuccess(response: BaseResponse<CommetaryInfoBean>) {
                    guideInfoLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<CommetaryInfoBean>) {
                    guideInfoLd.postValue(null)
                }
            })
    }

    fun getHavedOderInfo(orderId: String) {
        VenuesRepository.venuesService.getHavedOrderInfo(orderId)
            .excute(object : BaseObserver<CommentaryOrderInfoBean>() {
                override fun onSuccess(response: BaseResponse<CommentaryOrderInfoBean>) {
                    venueOderInfoLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<CommentaryOrderInfoBean>) {
                    venueOderInfoLd.postValue(null)
                }

            })
    }

    /**
     * 生成预订场馆订单
     */
    fun createVenueOrder(bean: VenueResOrderModel?, cttBean: VenueCttrOrderModel?) {
        mPresenter.value?.loading = false
        var param = HashMap<String, String>()

        // 场馆信息填写
        if (bean != null) {
            if (cttBean!!.existVenueRelationOrderCode) {
                param["venueOrderType"] = "2"
            } else {
                param["venueOrderType"] = "1"
            }
            if (!bean.smsCode.isNullOrEmpty()) {
                param["code"] = bean.smsCode!!
            }
            param["userName"] = bean.name!!
            param["userPhone"] = SM4Util.encryptByEcb(bean.phone!!)
            param["orderNum"] = "1"
            param["orderType"] = "CONTENT_TYPE_VENUE"
            param["channel"] = "app"
            param["idCard"] = SM4Util.encryptByEcb(bean.idCardNum!!)
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
            if (!bean.healthCodeRegion.isNullOrEmpty()) {
                param["healthCodeRegion"] = bean.healthCodeRegion!!
            }
        }
        // 预约讲解人信息
        if (cttBean != null) {
            param["venueId"] = cttBean.venueId!!
            if (cttBean!!.existVenueRelationOrderCode) {
                param["venueOrderType"] = "2"
                param["existVenueRelationOrderCode"] = cttBean!!.venueOrdeCode!!
            } else {
                param["venueOrderType"] = "1"
            }
            param["guideLanguage"] = cttBean.guideLanguage!!
            param["guideOrderTimeId"] = cttBean.guideOrderTimeId!!
            param["guideExhibitionIds"] = cttBean.guideExhibitionIds!!
            if (!cttBean!!.remark.isNullOrEmpty()) {
                param["remark"] = cttBean!!.remark!!
            }
            if (!cttBean!!.inExhallTime.isNullOrEmpty()) {
                param["expectedTime"] = cttBean!!.inExhallTime!!
            }


        }


        VenuesRepository.venuesService.generateOrder(param)
            .excute(
                object : BaseObserver<OrderResultBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<OrderResultBean>) {
                        if (bean != null) {
                            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_NAME, bean.name)
                            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_PHONE, bean.phone)
                            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).put(SPUtils.Config.APP_RESERATION_IDCARD, bean.idCardNum)
                            if (!bean.cardType.isNullOrEmpty()) {
                                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO)
                                    .put(SPUtils.Config.APP_RESERATION_CARD_TYPE, bean.cardType)
                            }
                        }
                        generVenuOrderLiveData.postValue(response.data)
                    }

                    override fun onFailed(response: BaseResponse<OrderResultBean>) {
                        mError.postValue(response)
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
                        .withString("orderType", ResourceType.CONTENT_TYPE_VENUE)
                        .withBoolean("isCommentator", true)
                        .withBoolean("isOnlyCommentator", isOnlyCommtator)
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
                        .withString("orderType", ResourceType.CONTENT_TYPE_VENUE)
                        .withString("faithAuditStatus", "")
                        .withBoolean("isCommentator", true)
                        .withBoolean("isOnlyCommentator", isOnlyCommtator)
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

    fun getSelfValidOrderList(resourceType: String, resourceId: String) {
        VenuesRepository.venuesService.getSelfValidOrderList(resourceType, resourceId)
    }
}


