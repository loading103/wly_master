package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   LectureHallReqViewModel
 * @Author      luoyi
 * @Time        2020/6/17 17:54
 */
class LectureHallReqViewModel : BaseViewModel() {

    var postLectureReqLd: MutableLiveData<Boolean> = MutableLiveData()

    fun potLectureHallReq(id: String, content: String) {
        mPresenter?.value?.loading = false
        MainRepository.service.postLectureHallReqeust(id, content).excute(object : BaseObserver<Any>(mPresenter) {

            override fun onSuccess(response: BaseResponse<Any>) {
                if (response.code == 0) {
                    postLectureReqLd.postValue(true)
                } else {
                    postLectureReqLd.postValue(false)
                }
            }

            override fun onFailed(response: BaseResponse<Any>) {
                postLectureReqLd.postValue(false)
            }

        })
    }
}