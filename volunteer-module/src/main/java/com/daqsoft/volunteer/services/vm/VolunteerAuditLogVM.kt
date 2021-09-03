package com.daqsoft.volunteer.services.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.volunteer.bean.OperateStatusBean
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.services.vm
 *@date:2020/7/1 10:02
 *@author: caihj
 *@des:
 **/
class VolunteerAuditLogVM:BaseViewModel() {

    var auditLog = MutableLiveData<MutableList<OperateStatusBean>>()

    fun getVolunteerAuditLog(){
        VolunteerRepository.service.getVolunteerAuditLog().excute(object :BaseObserver<OperateStatusBean>(){
            override fun onSuccess(response: BaseResponse<OperateStatusBean>) {
                auditLog.postValue(response.datas)
            }

        })
    }

}