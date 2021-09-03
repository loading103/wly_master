package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.bean.LectureRecord
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 我的课程列表
 * @ClassName   MineLectureViewModel
 * @Author      luoyi
 * @Time        2020/6/18 11:43
 */
class MineLectureViewModel : BaseViewModel() {


    var mineLectureLd: MutableLiveData<MutableList<LectureHall>> = MutableLiveData()

    var mineLectureRecordLd: MutableLiveData<LectureRecord> = MutableLiveData()

    /**
     * 我的学习列表
     * @param currPage 页码
     *
     */
    fun getMineLectureLs(currPage: Int) {
        mPresenter?.value?.loading = false

        MainRepository.service.getMineLectureList("10", currPage.toString())
            .excute(object : BaseObserver<LectureHall>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LectureHall>) {
                    mineLectureLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<LectureHall>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 我的学习记录
     */
    fun getMineLectureRecorder() {
        MainRepository.service.getMineLectureRecoder().excute(object : BaseObserver<LectureRecord>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LectureRecord>) {
                mineLectureRecordLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<LectureRecord>) {
                mineLectureRecordLd.postValue(null)
            }

        }
        )
    }
}