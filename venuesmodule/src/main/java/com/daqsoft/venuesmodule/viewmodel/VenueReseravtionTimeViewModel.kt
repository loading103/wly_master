package com.daqsoft.venuesmodule.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.VenueDateNumBean
import com.daqsoft.provider.bean.VenueReservationInfo
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * @Description
 * @ClassName   VenueReseravtionTimeViewModel
 * @Author      luoyi
 * @Time        2020/5/6 9:13
 */
class VenueReseravtionTimeViewModel : BaseViewModel() {


    var venueOrderDateListLiveData: MutableLiveData<VenueReservationInfo> = MutableLiveData()

    var venueOrderDateNumLiveData: MutableLiveData<MutableList<VenueDateNumBean>> = MutableLiveData()

    /**
     * 获取预约时间数目
     */
    fun getVenueOrderDateList(venueId: String, date: String, type: Int? = 1, number: Int = 0) {

        var param = HashMap<String, String>()
        param["venueId"] = venueId
        param["date"] = date
        if (number > 0) {
            param["number"] = number.toString()
        }
        if (type != null) {
            if (type == 1) {
                param["reservationType"] = "TEAM"
            } else {
                param["reservationType"] = "PERSON"
            }
        }
        VenuesRepository.venuesService.getOrderDateList(param)
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
        var param = HashMap<String, String>()
        param["venueId"] = venueId
        param["date"] = date
        if (number > 0) {
            param["number"] = number.toString()
        }
        VenuesRepository.venuesService.getOrderDateNum(param)
            .excute(object : BaseObserver<VenueDateNumBean>() {
                override fun onSuccess(response: BaseResponse<VenueDateNumBean>) {
                    venueOrderDateNumLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VenueDateNumBean>) {
                    mError.postValue(response)
                }

            })
    }
}