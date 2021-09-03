package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.bean.NearBusBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :附近公交站viewModel
 * @author 江云仙
 * @date 2020/4/3 11:40
 * @version 1.0.0
 * @since JDK 1.8
 */
class NearBusViewModel:BaseViewModel() {
    /**
     * 当前经纬度
     */
    var currentLat = ""

    var currentLon = ""
    /**
     *附近公交站列表数据
     */
    var result = MutableLiveData<MutableList<NearBusBean>>()
    /**
     * 获取旅行社列表
     */
    fun getTravelAgencyList() {
        mPresenter?.value?.loading = true
        val map = HashMap<String, Any>()
        // 经纬度
        map["radius"] = 500
        map["location"] = "$currentLon,$currentLat"
        ServiceRepository().service.getBusAround(map)
            .excute(object : BaseObserver<NearBusBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<NearBusBean>) {
                    result.postValue(response.datas)
                }

            })
    }
}