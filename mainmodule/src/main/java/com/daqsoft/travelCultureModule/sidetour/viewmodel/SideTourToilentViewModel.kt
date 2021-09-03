package com.daqsoft.travelCultureModule.sidetour.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ToilentBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   SideTourToilentViewModel
 * @Author      luoyi
 * @Time        2020/3/19 11:07
 */
class SideTourToilentViewModel: BaseViewModel() {

    val param = HashMap<String, String>()

    /**
     * 厕所详情
     */
    var toilentInfo = MutableLiveData<ToilentBean>()

    /**
     * 获取厕所详情
     */
    fun getToilentDetail(p: HashMap<String, String>) {
        mPresenter?.value?.loading = true
//        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getToilentDetail(p)
            .excute(object : BaseObserver<ToilentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ToilentBean>) {
                    toilentInfo.postValue(response.data)
                }
            })
    }

}