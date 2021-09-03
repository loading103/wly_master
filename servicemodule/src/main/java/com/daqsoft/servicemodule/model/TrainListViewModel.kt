package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.bean.StationBean
import com.daqsoft.servicemodule.bean.TrainListBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :火车列表查询ViewModel
 * @author 江云仙
 * @date 2020/4/8 18:57
 */
class TrainListViewModel :BaseViewModel(){
    /**
     *城市列表
     */
    var  result= MutableLiveData<MutableList<TrainListBean>>()
    var time=""
    var start=""
    var end=""
    fun getStationName() {
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["time"] = time
        map["start"] = start
        map["end"] = end
        ServiceRepository().service.getTrainList(map)
            .excute(object : BaseObserver<TrainListBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<TrainListBean>) {
                result.postValue(response.datas)
            }

        })
    }
}