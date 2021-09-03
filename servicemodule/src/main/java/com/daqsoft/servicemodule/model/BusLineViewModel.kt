package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.net.ServiceRepository
import com.daqsoft.servicemodule.bean.BusLineBean

/**
 * desc :公交查询viewModel
 * @author 江云仙
 * @date 2020/4/3 15:37
 */
class BusLineViewModel:BaseViewModel() {
    var origin = ""
    var city = ""
    var destination = ""
    var cityd = ""
//    var start = ""
//    var end = ""
    /**
     *公交路线列表数据
     */
    var result = MutableLiveData<BusLineBean>()
    /**
     * 获取公交路线列表
     */
    fun getBusLineUrlList() {
        mPresenter?.value?.loading = true
        val map = HashMap<String, Any>()
        map["origin"] = origin
        map["city"] = city
        map["destination"] = destination
        map["cityd"] = cityd
        ServiceRepository().service.getBusLineUrl(map)
            .excute(object : BaseObserver<BusLineBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BusLineBean>) {
                    result.postValue(response.data)
                }

            })
    }
}