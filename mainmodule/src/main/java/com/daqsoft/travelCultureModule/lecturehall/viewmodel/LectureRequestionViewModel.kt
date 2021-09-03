package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.LectureRequestion
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   LectureRequestionViewModel
 * @Author      luoyi
 * @Time        2020/6/16 13:42
 */
class LectureRequestionViewModel : BaseViewModel() {

    var lectureHallRequestLsLd: MutableLiveData<MutableList<LectureRequestion>> = MutableLiveData()

    fun getLectureRequestion(id: String, currPage: Int) {

        MainRepository.service.getLectureHallRequestionls(id, currPage.toString(), "10")
            .excute(object : BaseObserver<LectureRequestion>() {
                override fun onSuccess(response: BaseResponse<LectureRequestion>) {
                    lectureHallRequestLsLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<LectureRequestion>) {
                    lectureHallRequestLsLd.postValue(null)
                }

            })
    }
}