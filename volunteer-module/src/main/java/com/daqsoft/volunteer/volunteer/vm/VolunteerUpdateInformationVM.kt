package com.daqsoft.volunteer.volunteer.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.UploadBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.net.VolunteerRepository
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder

/**
 *@package:com.daqsoft.volunteer.volunteer.vm
 *@date:2020/6/29 9:56
 *@author: caihj
 *@des:志愿者基本资料
 **/
open class VolunteerUpdateInformationVM :BaseViewModel(){

    companion object{
        const val headUrl = "head"
        const val SERVICEINTENTION = "serviceIntention" // 服务意向
        const val LANGUAGE = "language" // 语言
        const val SPECIALTY = "specialty"
        const val address = "address"
        const val region = "region"
        const val QQ = "qq"
        const val EMAIL = "email"
        const val SERVICETIMETYPE = "serviceTimeType"
        const val HEALTHSTATUS= "healthStatus"
        const val emergencyContactPhone = "emergencyContactPhone"
        const val emergencyContactName = "emergencyContactName"
        const val EMPLOYMENTSTATUS = "employmentStatus"
        const val discipline = "discipline"
        const val SCHOOL = "school"
        const val education = "education"
        const val ATTRIBUTION = "attribution"
        const val SERVICEREGION = "serviceRegion"
    }

    var updateStatus = MutableLiveData<Boolean>()

    private fun updateVolunteerInfo(params:HashMap<String,String>){
        val gson = Gson()
        val param = gson.toJson(params)
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),param)
        VolunteerRepository.service.updateVolunteerInfo(body).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                if(response.code == 0){
                    updateStatus.postValue(true)
                    ToastUtils.showMessage("修改成功!")
                }else{
                    updateStatus.postValue(false)
                    ToastUtils.showMessage(response.message)
                }
            }

            override fun onFailed(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.message)
                updateStatus.postValue(false)
            }

        })
    }

    fun updateVolunteerInfo(key:String,value:String,key1:String = "",value1:String = ""){
        val params = HashMap<String,String>()
            params[key] = value
        if(key1.isNotEmpty()){
            params[key1] = value1
        }
        updateVolunteerInfo(params)
    }
}