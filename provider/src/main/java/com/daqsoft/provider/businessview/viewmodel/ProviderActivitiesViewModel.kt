package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 * @Description
 * @ClassName   ProviderActivitiesViewModel
 * @Author      luoyi
 * @Time        2020/5/8 14:51
 */
class ProviderActivitiesViewModel : BaseViewModel() {

    var pageSize = 10

    /**
     * 活动列表
     */
    var activitiesLiveData: MutableLiveData<MutableList<ActivityBean>> = MutableLiveData()

    /**
     * 获取活动列表
     */
    fun getProviderActivities(pageIndex: Int, id: String, type: String) {
        mPresenter.value?.isNeedRecyleView = false
        mPresenter.value?.loading = false
        var params = HashMap<String, String>()
        params["resourceId"] = id
        params["resourceType"] = type
        params["currPage"] = pageIndex.toString()
        params["pageSize"] = pageSize.toString()

        HomeRepository.service.getActivityList(params)
            .excute(object : BaseObserver<ActivityBean>() {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activitiesLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    mError.postValue(response)
                }
            })
    }
}