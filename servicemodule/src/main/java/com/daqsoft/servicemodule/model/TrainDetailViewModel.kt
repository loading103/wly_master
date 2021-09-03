package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.bean.SeatList
import com.daqsoft.servicemodule.bean.StationBean
import com.daqsoft.servicemodule.bean.TrainDetailBean
import com.daqsoft.servicemodule.bean.TrainListBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :火车列表查询ViewModel
 * @author 江云仙
 * @date 2020/4/8 18:57
 */
class TrainDetailViewModel :BaseViewModel(){

    var time=""
    var trainListBean: TrainListBean?=null
    var trainCode=""
    /**
     *火车车次列表
     */
//    var  result= MutableLiveData<MutableList<TrainListBean>>()
    fun getStationLine() {
        val map = HashMap<String, String>()
        map["time"] = time
//        map["start"] = start
//        map["end"] = end
        ServiceRepository().service.getStationLine(map).excute(object : BaseObserver<TrainListBean>() {
            override fun onSuccess(response: BaseResponse<TrainListBean>) {
//                result.postValue(response.datas)
            }

        })
    }
    var  result= MutableLiveData<TrainDetailBean>()
    fun getStationLineInfo() {
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["date"] = time
        map["trainCode"] = trainCode
        ServiceRepository().service.getStationLineInfo(map).excute(object : BaseObserver<TrainDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<TrainDetailBean>) {
                result.postValue(response.data)
            }

        })
    }
}