package com.daqsoft.usermodule.ui.credit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.UserRepository
import java.util.HashMap

/**
 *@package:com.daqsoft.usermodule.ui.credit
 *@date:2020/4/10 11:08
 *@author: caihj
 *@des:TODO
 **/
class MineCreditViewModel : BaseViewModel() {

    // 身份认证状态
    var idStatus = MutableLiveData<String>()

    // 信用分
    var creditBean = MutableLiveData<CreditBean>()

    // 当前守约记录
    var currentCreditBean = MutableLiveData<CurrentCreditBean>()

    // 信用等级
    var creditLevels = MutableLiveData<MutableList<CreditLevelBean>>()

    // 预约活动
    var activities = MutableLiveData<MutableList<ActivityBean>>()

    // 预约活动室
    var activityRooms = MutableLiveData<MutableList<ActivityRoomBean>>()

    // 守约类型
    var recordType = "KEEP_PROMISE"

    // 守约时间
    var time = ""

    var creditRecords = MutableLiveData<MutableList<CreditScoreBean>>()

    var creditPreMonthBean = MutableLiveData<CreditPreMonthBean>()

    /**
     * 获取用户实名认证状态
     */
    fun getIdWd() {
        UserRepository().userService.getRealNameValidateStatus().excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                idStatus.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.msg)
            }

        })

    }

    fun getIdStatus() {
        UserRepository().userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    idStatus.postValue(response.data?.realNameStatus.toString())
                }
            })
    }

    /**
     * 获取用户信用分
     */
    fun getCreditScore(phone: String) {
        val siteCode = SPUtils.getInstance().getString(SPKey.SITE_CODE)
        UserRepository().userService.getCreditScore(phone, "cultural", siteCode).excute(object : BaseObserver<CreditBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CreditBean>) {
                creditBean.postValue(response.data)
            }
        })
    }

    /**
     * 获取当前守约次数
     */
    fun getCurrentCredit(phone: String) {
        val siteCode = SPUtils.getInstance().getString(SPKey.SITE_CODE)
        UserRepository().userService.getCurrentCredit(phone, siteCode).excute(object : BaseObserver<CurrentCreditBean>() {
            override fun onSuccess(response: BaseResponse<CurrentCreditBean>) {
                currentCreditBean.postValue(response.data)
            }

        })
    }

    /**
     * 获取信用等级
     */
    fun getCreditLevel() {
        UserRepository().userService.getCreditLevel("100").excute(object : BaseObserver<CreditLevelBean>() {
            override fun onSuccess(response: BaseResponse<CreditLevelBean>) {
                creditLevels.postValue(response.datas)
            }
        })
    }

    /**
     * 刷新
     */
    fun refresh() {
        UserRepository().userService.getUserInformation()
            .excute(object : BaseObserver<UserBean>() {
                override fun onSuccess(response: BaseResponse<UserBean>) {
                    if (response.code == 0) {
                        var user = response.data
                        if (user != null) {
                            user.phone?.let {
                                SPUtils.getInstance().put("PHONE_NUM", it)
                                getCreditScore(it)
                                getCurrentCredit(it)
                            }
                        }

                    }
                }
            })
    }

    /**
     * 获取活动
     */
    fun getActivityList() {
        val param = HashMap<String, String>()
        param["pageSize"] = "2"
        param["currPage"] = "1"
        param["methods"] = "CREDIT_USE,CREDIT_AUDIT"
        // 活动类型id
        mPresenter.value?.loading = false
        HomeRepository.service.getActivityList(param).excute(object : BaseObserver<ActivityBean>() {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {

                if (response.datas!!.size > 2) {
                    activities.postValue(response.datas!!.subList(0, 2))
                } else {
                    activities.postValue(response.datas)
                }
            }
        })
    }

    /**
     * 获取预约活动室列表
     */
    fun getActivityRoom() {
        HomeRepository.service.getActivityRoomList().excute(object : BaseObserver<ActivityRoomBean>() {
            override fun onSuccess(response: BaseResponse<ActivityRoomBean>) {
                if (response.datas!!.size > 2) {
                    activityRooms.postValue(response.datas!!.subList(0, 2))
                } else {
                    activityRooms.postValue(response.datas)
                }
            }

        })
    }

    var currPage = 1

    var pageSize = 10

    /**
     * 获取守约记录
     */
    fun getCreditRecords() {
        val siteCode = SPUtils.getInstance().getString(SPKey.SITE_CODE)
        var phone = SPUtils.getInstance().getString("PHONE_NUM")
        UserRepository().userService.getCreditRecord(
            phone,
            siteCode,
            recordType,
            time,
            currPage = currPage,
            pageSize = pageSize
        ).excute(object : BaseObserver<CreditScoreBean>() {
            override fun onSuccess(response: BaseResponse<CreditScoreBean>) {
                creditRecords.postValue(response.datas)
            }

        })
    }

    /**
     * 获取上个月的信用记录
     */
    fun getPreCreditRecord() {
        val siteCode = SPUtils.getInstance().getString(SPKey.SITE_CODE)
        var phone = SPUtils.getInstance().getString("PHONE_NUM")
        UserRepository().userService.getPreCreditRecord(
            phone,
            siteCode
        ).excute(object : BaseObserver<CreditPreMonthBean>() {
            override fun onSuccess(response: BaseResponse<CreditPreMonthBean>) {
                creditPreMonthBean.postValue(response.data)
            }

        })
    }

}