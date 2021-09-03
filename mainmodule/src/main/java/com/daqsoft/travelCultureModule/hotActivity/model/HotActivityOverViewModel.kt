package com.daqsoft.travelCultureModule.hotActivity.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ActivityOverView
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   HotActivityOverViewModel
 * @Author      luoyi
 * @Time        2020/6/10 10:46
 */
class HotActivityOverViewModel: BaseViewModel() {
    /**
     * 活动概览数据
     */
    var activityOverViewLd: MutableLiveData<ActivityOverView> = MutableLiveData()
    /**
     * 活动概览
     */
    fun getActivitiesOverView(startime: String, endTime: String) {
        var params: HashMap<String, String> = HashMap()
        if (!startime.isNullOrEmpty()) {
            params["startTime"] = startime
        }
        if (!endTime.isNullOrEmpty()) {
            params["endTime"] = endTime
        }
        params["totalCountFlag"] = "true"
        MainRepository.service.getActivityCalendar(params).excute(object : BaseObserver<ActivityOverView>() {
            override fun onSuccess(response: BaseResponse<ActivityOverView>) {
                activityOverViewLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<ActivityOverView>) {
                activityOverViewLd.postValue(null)
            }

        })
    }
}