package com.daqsoft.volunteer.volunteer.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.bean.VolunteerRegion
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.Constant
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.volunteer.vm
 *@date:2020/6/3 14:02
 *@author: caihj
 *@des:TODO
 **/
class VolunteerRegisterVM:BaseViewModel() {
    /**
     * 性别
     */
    var gender = ""

    /**
     * 姓名
     */
    var name = ""

    /**
     * 民族
     */
    var  national = ""

    /**
     * 身份证
     */
    var idCard = ""

    /**
     * 手机号
     */
    var phone = ""

    /**
     * 验证码
     */
    var code = ""

    /**
     * 地区
     */
    var region = ""

    /**
     * 志愿者归属
     */
    var attribution = ""

    /**
     * 学历
     */
    var education = ""

    /**
     * 就业状况
     */
    var employmentStatus = ""

    /**
     * 个人特长
     */
    var specialty = ""

    /**
     * 健康状况
     */
    var healthStatus = ""

    /**
     * 语言
     */
    var language = ""

    /**
     * 服务时间
     */
    var serviceTimeType = ""

    /**
     * 服务意向
     */
    var serviceIntention = ""

    /**
     * 详细地址
     */
    var address = ""

    /**
     * 服务地区
     */
    var serviceRegion = ""

    /**
     * 紧急联系人电话
     */
    var emergencyContactPhone = ""

    /**
     *紧急联系人名称
     */
    var emergencyContactName = ""

    /**
     * 学校
     */
    var school = ""

    var email = ""

    var qq = ""


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
     * 民族
     */
    var nationals = MutableLiveData<MutableList<String>>()

    /**
     * 获取民族信息
     */
    fun getNations(){
        VolunteerRepository.service.getVolunteerType(Constant.NATION).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                nationals.postValue(response.datas)
            }
        })
    }


    /**
     * 发送验证码
     */
    fun senCode() {
        VolunteerRepository.service
            .sendMsg(phone, Constant.VOLUNTEER_CODE_TYPE)
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    if (response.code == 0) {
                        viewListener?.onCountDown()
                    }
                }

            })

    }

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
    var serviceIntentions = MutableLiveData<MutableList<String>>()

    /**
     * 服务时间
     */
    fun getServiceIntentions(){
        VolunteerRepository.service.getVolunteerType(Constant.SERVICE_INTENTION).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                serviceIntentions.postValue(response.datas)
            }
        })
    }


    /**
     * 正面
     */
    var idCardUp: String? = null
    /**
     * 反面
     */
    var idCardDown: String? = null

    /**
     * 本地正面图片地址
     */
    val idCardUpLocal = MutableLiveData<String>()
    /**
     * 本地反面图片地址
     */
    val idCardDownLocal = MutableLiveData<String>()

    /**
     * 是否申请完成
     */
    var doFinish = MutableLiveData<Boolean>()

    /**
     * 申请志愿者
     */
    fun applyVolunteer(){
        val params = HashMap<String,String>()
            params["serviceIntention"] = serviceIntention
            if(language.isNotEmpty())
            params["language"] = language
            params["specialty"] = specialty
            if(address.isNotEmpty())
            params["address"] = address
            params["region"] = region
            if(qq.isNotEmpty()){
               params["qq"] = qq
            }
            if(email.isNotEmpty()){
                params["email"] = email
            }
            params["serviceTimeType"] = serviceTimeType
            if(healthStatus.isNotEmpty()){
                params["healthStatus"] = healthStatus
            }
            params["emergencyContactName"] = emergencyContactName
            params["emergencyContactPhone"] = emergencyContactPhone
            params["employmentStatus"] = employmentStatus
            if(school.isNotEmpty()){
                params["school"] = school
            }
            if(education.isNotEmpty()){
                params["education"] = education
            }
            params["attribution"] = attribution
            params["serviceRegion"] = serviceRegion
            params["phone"] = phone
            params["code"] = code
            params["idCardNationalEmblem"] = idCardDown!!
            params["idCardPortrait"] = idCardUp!!
            params["idCard"] = idCard
            params["nation"] = national
            params["name"] = name
        VolunteerRepository.service.volunteerRegister(params).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                if(response.code == 0){
                    doFinish.postValue(true)
                    JumpUtils.gotoVolunteerRegSuc(0)
                    finish.postValue(true)
                }else{
                    ToastUtils.showMessage(response.message)
                }
            }

            override fun onFailed(response: BaseResponse<String>) {
                ToastUtils.showMessage(response.msg)
                doFinish.postValue(false)
            }

        })
    }
    var validateStatus = MutableLiveData<Boolean>()

    fun validateCode(){
        VolunteerRepository.service.validateCode(phone,code,Constant.VOLUNTEER_CODE_TYPE).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                validateStatus.postValue(response.code == 0)
                if(response.code !=0){
                    ToastUtils.showMessage(response.message)
                }
            }

            override fun onFailed(response: BaseResponse<String>) {
                validateStatus.postValue(false)
                ToastUtils.showMessage(response.message)
            }

        })
    }


    var viewListener: ViewListener? = null
        get() = field
        set(value) {
            field = value
        }

    public interface ViewListener {
        fun onCountDown()
    }
}