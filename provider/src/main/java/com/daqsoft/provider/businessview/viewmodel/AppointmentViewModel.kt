package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ResourceOrderBean
import com.daqsoft.provider.bean.TimeAppointBean
import com.daqsoft.provider.network.provider.ProviderRepository

/**
 * @Description
 * @ClassName   AppointmentFrament
 * @Author      luoyi
 * @Time        2020/5/26 13:48
 */
class AppointmentViewModel : BaseViewModel() {
    /**
     * 资源订单列表
     */
    var resourcesOrderLiveData = MutableLiveData<MutableList<ResourceOrderBean>>()
    /**
     * 资源加密id
     */
    var resourceIdLiveData = MutableLiveData<String>()
    /**
     * 分时预约
     */
    var timeAppointLiveData = MutableLiveData<MutableList<TimeAppointBean>>()

    /**
     * 获取预约列表
     * @param resourceId 资源id
     * @param resourceType 资源类型
     */
    fun getScenicAppointmentList(resourceId: String, resourceType: String) {
        mPresenter?.value?.loading = false
        ProviderRepository.service.getResourceOrderList(resourceId, resourceType)
            .excute(object : BaseObserver<ResourceOrderBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ResourceOrderBean>) {
                    resourcesOrderLiveData.postValue(response.datas)
                }

            })
    }

    /**
     * 获取资源加密信息
     */
    fun getResourceIdentity() {
        mPresenter?.value?.loading = false
        ProviderRepository.service.getResourceIdentity().excute(object : BaseObserver<String>(mPresenter) {
            override fun onSuccess(response: BaseResponse<String>) {
                resourceIdLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<String>) {
                resourceIdLiveData.postValue(null)
            }

        })
    }

    fun getResourceTimeList(resourceType: String, startDate: String, dayNum: String, resourceId: String) {
        mPresenter?.value?.loading = false
        ProviderRepository.service.getResourceTimeList(resourceType, startDate, dayNum, resourceId)
            .excute(object : BaseObserver<TimeAppointBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<TimeAppointBean>) {
                    timeAppointLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<TimeAppointBean>) {
                    timeAppointLiveData.postValue(null)
                }

            })
    }
}