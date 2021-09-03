package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.bean.LectureType
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 上学堂 列表
 * @ClassName   LectureHallsViewModel
 * @Author      luoyi
 * @Time        2020/6/15 11:01
 */
class LectureHallsViewModel : BaseViewModel() {


    /**
     * 是否仅显示未学习
     */
    var onlyNotStudy: Boolean = false
    /**
     * 类型id
     */
    var typeId: Int = 0
    /**
     * 是否最新
     */
    var isAsc: Boolean = false
    /**
     * 名字
     */
    var name: String = ""

    /**
     *  课程类型
     */
    var lectureTypesLd: MutableLiveData<MutableList<LectureType>> = MutableLiveData()
    /**
     * 课程列表
     */
    var lectureHallLsLd: MutableLiveData<MutableList<LectureHall>> = MutableLiveData()

    /**
     * 获取课程列表
     */
    fun getLectureHallList(currPage: Int) {
        var param: HashMap<String, String> = hashMapOf()
        param["pageSize"] = "10"
        param["currPage"] = currPage.toString()
        param["isAsc"] = isAsc.toString()
        if (typeId != 0) {
            param["typeId"] = typeId.toString()
        } else {
            param["typeId"] = ""
        }
        if (!name.isNullOrEmpty()) {
            param["name"] = name
        }
        param["onlyNotStudy"] = onlyNotStudy.toString()
        mPresenter?.value?.loading = false
        MainRepository.service.getLectureHallList(param)
            .excute(object : BaseObserver<LectureHall>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LectureHall>) {
                    lectureHallLsLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<LectureHall>) {
                    lectureHallLsLd.postValue(null)
                }

            })
    }

    /**
     * 获取课程类型
     */
    fun getLectureHallType() {
        mPresenter?.value?.loading = false
        MainRepository.service.getLectureHallType().excute(object : BaseObserver<LectureType>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LectureType>) {
                lectureTypesLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LectureType>) {
                lectureTypesLd.postValue(null)
            }

        })
    }
}