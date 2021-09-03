package com.daqsoft.volunteer.services.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.UserLogin
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.bean.OperateStatusBean
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.services.vm
 *@date:2020/6/30 15:46
 *@author: caihj
 *@des:TODO
 **/
class VolunteerServicesVM:BaseViewModel() {

    val userInfo = MutableLiveData<UserLogin>()

    /**
     * 刷新用户信息
     */
    fun refreshToken(){
        UserRepository.userService.refreshToken().excute(object :BaseObserver<UserLogin>(){
            override fun onSuccess(response: BaseResponse<UserLogin>) {
                userInfo.postValue(response.data)
            }
        })
    }

    var volunteerStatus = MutableLiveData<OperateStatusBean>()

    fun getVolunteerStatus(){
        VolunteerRepository.service.getVolunteerOperateStatus().excute(object :BaseObserver<OperateStatusBean>(){
            override fun onSuccess(response: BaseResponse<OperateStatusBean>) {
                volunteerStatus.postValue(response.data)
            }

        })
    }

    var volunteerTeamStatus = MutableLiveData<OperateStatusBean>()

    fun getVolunteerTeamStatus(){
        VolunteerRepository.service.getVolunteerTeamOperateStatus().excute(object :BaseObserver<OperateStatusBean>(){
            override fun onSuccess(response: BaseResponse<OperateStatusBean>) {
                volunteerTeamStatus.postValue(response.data)
            }

        })
    }

    var cancelStatus = MutableLiveData<Boolean>()

    fun cancelApplyVolunteer(){
        VolunteerRepository.service.volunteerCancel().excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.message)
                if(response.code == 0){
                    cancelStatus.postValue(true)
                }else{
                    cancelStatus.postValue(false)
                }
            }

        })
    }

    fun cancelApplyVolunteerTeam(){
        VolunteerRepository.service.volunteerTeamCancel().excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.message)
                if(response.code == 0){
                    cancelStatus.postValue(true)
                }else{
                    cancelStatus.postValue(false)
                }
            }

        })
    }
}