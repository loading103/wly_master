package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.LectureHallChapterBean
import com.daqsoft.travelCultureModule.net.MainApi
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   LectureChapterViewModel
 * @Author      luoyi
 * @Time        2020/6/16 13:43
 */
class LectureChapterViewModel : BaseViewModel() {

    var lectureHalChapterLd: MutableLiveData<MutableList<LectureHallChapterBean>> = MutableLiveData()

    fun getLectureChapterLs(id: String) {
        mPresenter?.value?.loading = false
        MainRepository.service.getLectureHallChapterLs(id).excute(object : BaseObserver<LectureHallChapterBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LectureHallChapterBean>) {
                lectureHalChapterLd?.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LectureHallChapterBean>) {
                lectureHalChapterLd.postValue(null)
            }
        })
    }

}