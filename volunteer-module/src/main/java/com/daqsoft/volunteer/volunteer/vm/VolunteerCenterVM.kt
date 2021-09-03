package com.daqsoft.volunteer.volunteer.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.volunteer.bean.VolunteerBasicBean
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.volunteer.vm
 *@date:2020/6/28 9:27
 *@author: caihj
 *@des:TODO
 **/
class VolunteerCenterVM:BaseViewModel() {

    var volunteer = MutableLiveData<VolunteerBasicBean>()
    var id = ""

    fun getVolunteer(){
        VolunteerRepository.service.getVolunteerBasicInfo().excute(object :BaseObserver<VolunteerBasicBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBasicBean>) {
                volunteer.postValue(response.data)
            }

        })
    }

}