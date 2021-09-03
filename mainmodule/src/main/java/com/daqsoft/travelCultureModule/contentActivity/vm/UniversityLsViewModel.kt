package com.daqsoft.travelCultureModule.contentActivity.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.contentActivity.bean.WatchShowBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description da
 * @ClassName   UniversityLsViewModel
 * @Author      luoyi
 * @Time        2020/6/1 11:39
 */
class UniversityLsViewModel : BaseViewModel() {
    //获取咨询列表
    var zixunList = MutableLiveData<WatchShowBean>()
    var channelCode: String? = ""

    fun getZixunList() {
        mPresenter.value?.loading = true
        MainRepository.service.getUniversityLs(
        ).excute(object : BaseObserver<WatchShowBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<WatchShowBean>) {
                zixunList.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<WatchShowBean>) {
                zixunList.postValue(response.data)
            }

        })
    }
}