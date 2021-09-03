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
import com.daqsoft.volunteer.bean.VolunteerRegion
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.Constant
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
class VolunteerInformationVM :VolunteerUpdateInformationVM(){

    var volunteer = MutableLiveData<VolunteerBriefInfoBean>()

    /**
     * 获取志愿者完整信息
     */
    fun getVolunteerInfo(){
        VolunteerRepository.service.getVolunteerInfo().excute(object :BaseObserver<VolunteerBriefInfoBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBriefInfoBean>) {
                volunteer.postValue(response.data)
            }
        })
    }


    /**
     * 头像
     */
    val head = MutableLiveData<String>()
    val headLocal = MutableLiveData<String>()

    /**
     * 上传图片
     * @param file 图片文件
     * @param index 上传图片的张数
     */
    fun upLoadFile(file: File) {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "Filedata",
                URLEncoder.encode(file.name,"utf-8"),
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )
            .addFormDataPart("key", SPUtils.getInstance().getString(SPUtils.Config.OSS_KEY))
            .build()

        UserRepository().userService
            .upLoad(builder.parts())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UploadBean> {
                override fun onComplete() {
                    Log.e("uploadFile:", "onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("uploadFile:", "onSubscribe")

                }

                override fun onNext(t: UploadBean) {
                    Log.e("uploadFile:", "onNext  ${t.fileUrl}")
                    head.value = t.url
                    updateVolunteerInfo(
                        headUrl,
                        head.value!!
                    )
                }

                override fun onError(e: Throwable) {
                    Log.e("uploadFile:", "onError" + e)

                }

            })
    }

    /**
     * 服务地区
     */
    var serviceRegion = ""

    var attribution = ""
    /**
     * 归属列表
     */
    var attributions = MutableLiveData<MutableList<VolunteerRegion>>()

    fun getAttributions(){
        VolunteerRepository.service.getVolunteerRegions(serviceRegion).excute(object :BaseObserver<VolunteerRegion>(){
            override fun onSuccess(response: BaseResponse<VolunteerRegion>) {
                attributions.postValue(response.datas)
            }
        })
    }

    /**
     * 更新服务地区和志愿归属
     */
    fun updateServiceRegionAndAttribution(){
        updateVolunteerInfo(ATTRIBUTION,attribution,SERVICEREGION,serviceRegion)
    }

    /**
     * 学历
     */
    var educations = MutableLiveData<MutableList<String>>()

    /**
     * 获取学历信息
     */
    fun getEducations(){
        VolunteerRepository.service.getVolunteerType(Constant.EDUCATION).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                educations.postValue(response.datas)
            }
        })
    }


    /**
     * 就业状况
     */
    var jobStatus = MutableLiveData<MutableList<String>>()

    /**
     * 获取就业状况
     */
    fun getJobStatus(){
        VolunteerRepository.service.getVolunteerType(Constant.PROFESSION).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                jobStatus.postValue(response.datas)
            }
        })
    }

    /**
     * 个人特长
     */
    var skills = MutableLiveData<MutableList<String>>()


    /**
     * 个人特长
     */
    fun getSkills(){
        VolunteerRepository.service.getVolunteerType(Constant.SPECIALTY).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                skills.postValue(response.datas)
            }
        })
    }

    /**
     * 健康状况
     */
    var healths = MutableLiveData<MutableList<String>>()

    /**
     * 健康状况
     */
    fun getHealths(){
        VolunteerRepository.service.getVolunteerType(Constant.HEALTH_STATUS).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                healths.postValue(response.datas)
            }
        })
    }

    /**
     * 语言
     */
    var languages = MutableLiveData<MutableList<String>>()

    /**
     * 语言
     */
    fun getLanguages(){
        VolunteerRepository.service.getVolunteerType(Constant.LANGUAGE).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                languages.postValue(response.datas)
            }
        })
    }

    /**
     * 服务时间
     */
    var serviceTimes = mutableListOf<String>(
        "不限",
        "休息日",
        "工作日"
    )
    val serviceType = mutableListOf<String>(
        "Unlimited",
        "RestDay",
        "WorkingDay"
    )

    /**
     * 服务意向
     */
    var serviceIntentions = MutableLiveData<MutableList<String>>()

    /**
     * 服务意向
     */
    fun getServiceIntentions(){
        VolunteerRepository.service.getVolunteerType(Constant.SERVICE_INTENTION).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                serviceIntentions.postValue(response.datas)
            }
        })
    }

}