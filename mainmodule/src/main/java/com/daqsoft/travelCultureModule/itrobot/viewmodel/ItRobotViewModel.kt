package com.daqsoft.travelCultureModule.itrobot.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ItRobotBean
import com.daqsoft.provider.bean.ItRobotDataBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 智能机器人viewModel
 * @ClassName   ItRobotViewModel
 * @Author      luoyi
 * @Time        2020/5/18 9:39
 */
class ItRobotViewModel : BaseViewModel() {

    var lon: String? = ""
    var lat: String? = ""
    /**
     * 机器人回到问题
     */
    var itRobotAnswerLiveData: MutableLiveData<ItRobotDataBean> = MutableLiveData()

    /**
     * 机器人信息
     */
    var itRobotInfoLiveData: MutableLiveData<ItRobotBean> = MutableLiveData()

    /**
     * 获取机器人信息
     */
    fun getItRobotInfo() {
        mPresenter?.value?.loading = false
        MainRepository.service.getItRobotInfo()
            .excute(object : BaseObserver<ItRobotBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ItRobotBean>) {
                    itRobotInfoLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ItRobotBean>) {
                    mError.postValue(response)
                }

            })
    }

    /**
     *  @param question 问题描述
     */
    fun getItRobotAnswer(question: String) {
        var param: HashMap<String, String> = hashMapOf()
        if (!lon.isNullOrEmpty()) {
            param["lon"] = lon!!
        }
        if (!lat.isNullOrEmpty()) {
            param["lat"] = lat!!
        }
        param["question"] = question
        param["source"] = "app"
        mPresenter?.value?.loading = false
        MainRepository.service.getItRobotRequest(param)
            .excute(object : BaseObserver<ItRobotDataBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ItRobotDataBean>) {
                    itRobotAnswerLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ItRobotDataBean>) {
                    mError.postValue(response)
                }

            })
    }

}