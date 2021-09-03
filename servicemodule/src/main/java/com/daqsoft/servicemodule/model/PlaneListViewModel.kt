package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.bean.*
import com.daqsoft.servicemodule.net.ServiceRepository
import com.daqsoft.servicemodule.uitils.TimeSwitch.getDayAndWeeks

/**
 * desc :机票viewModel
 * @author 江云仙
 * @date 2020/4/9 14:23
 */
class PlaneListViewModel :BaseViewModel(){
    /**
     *航班列表
     */
    var  result= MutableLiveData<MutableList<PlaneLists>>()
    var date=""
    var city=""
    var endcity=""
    fun getPlaneList() {
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["date"] = date
        map["city"] = city
        map["endcity"] = endcity
        ServiceRepository().service.aviationFlight(map).excute(object : BaseObserver<PlaneListBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<PlaneListBean>) {
                result.postValue(response.data!!.list)
            }
            override fun onFailed(response: BaseResponse<PlaneListBean>) {
                super.onFailed(response)
                mError.postValue(response)
            }

        })
    }

    /**
     * 日期集合数据
     */
    var  planeTimeBeans= MutableLiveData<MutableList<PlaneTimeBean>>()
    fun getDates(time: String) {
        planeTimeBeans.postValue(getDayAndWeeks(5,time))
    }
}