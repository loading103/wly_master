package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.bean.BusWayListData
import com.daqsoft.servicemodule.bean.StationBean
import com.daqsoft.servicemodule.bean.TrainListBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :长途汽车viewModel
 * @author 江云仙
 * @date 2020/4/9 11:09
 */
class SubWayListViewModel :BaseViewModel(){
    /**
     *长途汽车列表
     */
    var  result= MutableLiveData<MutableList<BusWayListData>>()
    var startCity=""
    var endCity=""
    fun getSubWayListName() {
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["startCity"] = startCity
        map["endCity"] = endCity
        ServiceRepository().service.stationAndStationQuery(map).excute(object : BaseObserver<BusWayListData>(mPresenter) {
            override fun onSuccess(response: BaseResponse<BusWayListData>) {
                result.postValue(response.datas)
            }

        })
    }
}