package com.daqsoft.provider.zxing

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.WriteOffsBean
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * @Description
 * @ClassName   CaptureViewModel
 * @Author      luoyi
 * @Time        2020/7/24 17:39
 */
class CaptureViewModel : BaseViewModel() {

    /**核销列表*/
    val writeOffs = MutableLiveData<MutableList<WriteOffsBean>>()

    fun getWriteOffsList(resourceType: String, resourceId: String) {
        VenuesRepository.venuesService.getSelfValidOrderList(resourceType, resourceId.toString())
            .excute(object : BaseObserver<WriteOffsBean>() {
                override fun onSuccess(response: BaseResponse<WriteOffsBean>) {
                    writeOffs.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<WriteOffsBean>) {
                    mError.postValue(response)
                }

            })


    }
}