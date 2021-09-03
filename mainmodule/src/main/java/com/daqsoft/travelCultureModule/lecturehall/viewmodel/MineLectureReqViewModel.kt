package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.LectureRequestion
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 我的课程提问
 * @ClassName   MineLectureReqViewModel
 * @Author      luoyi
 * @Time        2020/6/18 11:43
 */
class MineLectureReqViewModel : BaseViewModel() {

    var lectureReqLd: MutableLiveData<BaseResponse<LectureRequestion>> = MutableLiveData()

    fun getMineLectureReqLs(currPage: Int) {
        mPresenter?.value?.loading = false
        MainRepository.service.getMineLectureReq("10", currPage.toString())
            .excute(object : BaseObserver<LectureRequestion>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LectureRequestion>) {
                    lectureReqLd.postValue(response)
                }

                override fun onFailed(response: BaseResponse<LectureRequestion>) {
                    lectureReqLd.postValue(null)
                }

            })
    }
}