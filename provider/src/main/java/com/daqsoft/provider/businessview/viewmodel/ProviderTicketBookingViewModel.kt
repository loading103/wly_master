package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.ScenicReservationBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut

/**
 * @Description
 * @ClassName   ProviderTicketBookingViewModel
 * @Author      luoyi
 * @Time        2020/11/5 15:08
 */
class ProviderTicketBookingViewModel : BaseViewModel() {



    /**
     * 景区须知
     */
    var scenicReservationLiveData = MutableLiveData<ScenicReservationBean>()

    /**
     * 获取景区预订须知
     * @param productSn 产品编号
     */
    fun getReservationInfo(productSn: String) {
        ElectronicRepository.electronicService
            .getScenicReservationInfo(productSn)
            .excut(object : ElectronicObserver<ScenicReservationBean>() {
                override fun onSuccess(response: BaseResponse<ScenicReservationBean>) {
                    scenicReservationLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ScenicReservationBean>) {
                    mError.postValue(response)
                }
            })
    }

}