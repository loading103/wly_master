package com.daqsoft.travelCultureModule.clubActivity.vm
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubPersonInfoBean
import com.daqsoft.travelCultureModule.net.MainRepository


class ClubPersonInfoViewModel:BaseViewModel(){
    var personInfo = MutableLiveData<ClubPersonInfoBean>()

    fun getPersonInfo(id:String){
        MainRepository.service.getClubPersonInfo(id).excute(object :
            BaseObserver<ClubPersonInfoBean>(){
            override fun onSuccess(response: BaseResponse<ClubPersonInfoBean>) {
                personInfo.postValue(response.data)
            }
        })
    }
}