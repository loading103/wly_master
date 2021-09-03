package com.daqsoft.integralmodule.ui.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.DetailBean
import com.daqsoft.integralmodule.repository.bean.PointCountBean

/**
 * 积分明细Vm
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-22
 * @since JDK 1.8.0_191
 */
class IntegralDetailActivityVm : BaseViewModel() {


    val repository = IntegralRepository()

    val data = MutableLiveData<MutableList<DetailBean>>()

    val integralData = MutableLiveData<PointCountBean>()
    fun pointCount() {
        repository.pointCount()
            .excute(object : BaseObserver<PointCountBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<PointCountBean>) {
                    integralData.postValue(response.data)
                }
            })
    }


    fun detail(time: String, type: Int) {
        repository.detail(time, type)
            .excute(object : BaseObserver<DetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<DetailBean>) {
                    data.postValue(response.datas)
                }
            })
    }
}