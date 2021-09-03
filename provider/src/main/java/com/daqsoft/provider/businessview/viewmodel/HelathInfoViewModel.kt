package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.HelathRegionBean
import com.daqsoft.provider.bean.HelathSetingBean
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * @Description
 * @ClassName   HelathInfoViewModel
 * @Author      luoyi
 * @Time        2020/7/9 11:15
 */
class HelathInfoViewModel : BaseViewModel() {

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
     *获取健康信息
     */
    fun getHealthInfo(phone: String, region: String, name: String, idCard: String) {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getUserHealthInfo(SM4Util.encryptByEcb(phone), region, name, SM4Util.encryptByEcb(idCard), siteId)
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
        VenuesRepository.venuesService.getUserHealthInfoAndRegister(SM4Util.encryptByEcb(phone), region, name, SM4Util.encryptByEcb(idCard), siteId)
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
    fun getHealthSetingInfo() {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthSetingInfo(siteId).excute(object : BaseObserver<HelathSetingBean>(mPresenter) {
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
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthTravlInfo(SM4Util.encryptByEcb(phone), name, SM4Util.encryptByEcb(idCard), siteId)
            .excute(object : BaseObserver<Boolean>(mPresenter) {
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
        VenuesRepository.venuesService.getHealthRegionInfo(siteId).excute(object : BaseObserver<HelathRegionBean>() {
            override fun onSuccess(response: BaseResponse<HelathRegionBean>) {
                healthRegionsLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<HelathRegionBean>) {
                healthRegionsLd.postValue(null)
            }
        })
    }
}