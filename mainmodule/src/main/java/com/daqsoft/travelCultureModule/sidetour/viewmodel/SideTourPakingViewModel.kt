package com.daqsoft.travelCultureModule.sidetour.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ParkingBean
import com.daqsoft.provider.bean.ToilentBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 找停车位适配器
 * @ClassName   SideTourPakingViewModel
 * @Author     luoyi
 * @Time        2020/3/19 11:07
 */
class SideTourPakingViewModel : BaseViewModel() {

    val param = HashMap<String, String>()

    /**
     * 厕所详情
     */
    var parkingInfo = MutableLiveData<ParkingBean>()

    /**
     * 获取停车位详情
     */
    fun getParkingDetail(p: HashMap<String, String>) {
        mPresenter?.value?.loading = true
//        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getParkingDetail(p)
            .excute(object : BaseObserver<ParkingBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ParkingBean>) {
                    parkingInfo.postValue(response.data)
                }
            })
    }

}