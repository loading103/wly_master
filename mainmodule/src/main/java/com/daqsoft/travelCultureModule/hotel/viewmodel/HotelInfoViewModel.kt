package com.daqsoft.travelCultureModule.hotel.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HotelDetailBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   HotelInfoViewModel
 * @Author      luoyi
 * @Time        2020/4/7 19:51
 */
class HotelInfoViewModel : BaseViewModel() {


    var hotelInfoLiveData: MutableLiveData<HotelDetailBean> = MutableLiveData()

    /**
     * 获取酒店更多信息
     */
    fun getHotelInfo(id: String) {
        MainRepository.service.getHotelDetail(id).excute(object : BaseObserver<HotelDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HotelDetailBean>) {
                hotelInfoLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<HotelDetailBean>) {
                mError.postValue(response)
            }

        })
    }
}