package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.SiteInfo
import com.daqsoft.provider.network.net.UserRepository

class ServiceSosModel : BaseViewModel() {
    var contact_phone = MutableLiveData<String>()
    /**
     * 获取站点信息
     */
    fun getSiteInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        UserRepository().userService.getSiteInfo()
            .excute(object : BaseObserver<SiteInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfo>) {
                    contact_phone.postValue(response.data?.emergencyPhone?:"")
                }

            })
    }
}