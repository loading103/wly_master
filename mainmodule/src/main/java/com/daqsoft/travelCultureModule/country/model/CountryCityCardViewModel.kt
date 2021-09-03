package com.daqsoft.travelCultureModule.country.model

/**
 * desc :乡村游旅游名片viewModel
 * @author 江云仙
 * @date 2020/4/29 10:41
 */

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.*
import com.daqsoft.travelCultureModule.country.net.CountryRepository

class CountryCityCardViewModel : BaseViewModel() {
    /**
     * 获取城市列表
     */
    var mddCityList = MutableLiveData<MutableList<BrandMDD>>()
    var mddDQXList = MutableLiveData<MutableList<BrandMDD>>()
    fun getMDDCity(siteId: String, pagesize: String, type: String) {
        mPresenter.value?.loading = true
//        val map = HashMap<String, Any>()
//        map["pageSize"] = pagesize
//        map["type"] = type
//        map["siteId"] = siteId
//        map["cardType"] = "TOURISM"
        CountryRepository.service.getTOURISMCity(pagesize, type, siteId).excute(object : BaseObserver<BrandMDD>(mPresenter) {
            override fun onSuccess(response: BaseResponse<BrandMDD>) {
                if (type == "city") {
                    mddCityList.postValue(response.datas)
                } else {
                    mddDQXList.postValue(response.datas)
                }
            }
        })
    }

}