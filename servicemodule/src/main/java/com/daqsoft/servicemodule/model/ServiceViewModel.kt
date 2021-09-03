package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.servicemodule.net.ServiceRepository
import com.daqsoft.provider.bean.HomeAd

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/17 11:36
 */
class ServiceViewModel  :BaseViewModel(){
    /**
     * 广告
     */
    var homeAd = MutableLiveData<HomeAd>()
    /**
     * 获取站点信息
     */
    fun getServiceAd() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val map = HashMap<String, String>()
        map["publishChannel"] = "APP"
        map["adCode"] = "app_service_adv"
        ServiceRepository().service.getServiceAd(map)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    homeAd.postValue(response.data)
                }
            })
    }
}