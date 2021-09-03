package com.daqsoft.volunteer.volunteer.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.UploadBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerBrandBean
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.bean.VolunteerRegion
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.Constant
import com.daqsoft.volunteer.utils.JumpUtils
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
 *@date:2020/6/16 15:27
 *@author: caihj
 *@des:TODO
 **/
class VolunteerTeamRegisterVM : BaseViewModel() {
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
    var national = ""

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
     * 服务地区
     */
    var serviceRegion = ""

    /**
     * 志愿者团队归属
     */
    var attribution = ""

    /**
     * 地区
     */
    var region = ""

    /**
     * 服务类型
     */
    var serviceType = ""

    /**
     * 团队logo
     */
    var logo = ""

    /**
     * 团队地址
     */
    var teamRegion: String? = ""

    /**
     * 团队详细地址
     */
    var teamAddress = ""

    /**
     * 品牌集合
     */
    var brandItemIds = ""
    var brandSelected = mutableListOf<String>()
    var brand = ""

    var teamPhone = ""

    var teamIntroduce = ""

    var teamName = ""

    var establishTime = ""

    /**
     * 主管单位
     */
    var manageUnit = ""

    var longitude = ""
    var latitude = ""

    val headLocal = MutableLiveData<String>()

    /**
     * 民族
     */
    var nationals = MutableLiveData<MutableList<String>>()


    var volunteer = MutableLiveData<VolunteerBriefInfoBean>()

    /**
     * 获取志愿者信息
     */
    fun getVolunteer() {
        VolunteerRepository.service.getVolunteerInfo().excute(object : BaseObserver<VolunteerBriefInfoBean>() {
            override fun onSuccess(response: BaseResponse<VolunteerBriefInfoBean>) {
                volunteer.postValue(response.data)
            }

        })
    }

    fun register() {
        val params = HashMap<String, String>()
        if (!brandItemIds.isNullOrEmpty()) {
            params["brandItemIds"] = brandItemIds
        }

        params["teamPhone"] = teamPhone
        if (!teamIntroduce.isNullOrEmpty()) {
            params["teamIntroduce"] = teamIntroduce
        }
        params["serviceRegion"] = serviceRegion
        params["attribution"] = attribution
        params["teamName"] = teamName
        params["serviceType"] = serviceType
        params["establishTime"] = establishTime
        if (!manageUnit.isNullOrEmpty())
            params["manageUnit"] = manageUnit
        if (!logo.isNullOrEmpty())
            params["teamIcon"] = logo
        if (!teamRegion.isNullOrEmpty()) {
            params["teamRegion"] = teamRegion!!
        }
        params["teamAddress"] = teamAddress
        params["longitude"] = longitude
        params["latitude"] = latitude
        params["code"] = code
        VolunteerRepository.service.volunteerTeamRegister(params).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                if (response.code == 0) {
                    JumpUtils.gotoVolunteerRegSuc(1)
                    finish.postValue(true)
                } else {
                    ToastUtils.showMessage(response.message)
                }
            }

        })
    }


    fun updateVolunteerInfo() {
        val params = HashMap<String, String>()
        if (!idCard.isNullOrEmpty()) {
            params["idCard"] = idCard
        }
        val gson = Gson()
        val param = gson.toJson(params)
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param)
        VolunteerRepository.service.updateVolunteerInfo(body).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {

            }

        })
    }

    var validateStatus = MutableLiveData<Boolean>()

    fun validateCode() {
        if (phone.isNullOrEmpty()) {
            phone = SPUtils.getInstance().getString(SPKey.PHONESTR)
        }
        VolunteerRepository.service.validateCode(phone, code, Constant.VOLUNTEER_TEAM_CODE_TYPE).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                validateStatus.postValue(response.code == 0)
            }

            override fun onFailed(response: BaseResponse<String>) {
                validateStatus.postValue(false)
            }

        })
    }


    /**
     * 获取民族信息
     */
    fun getNations() {
        VolunteerRepository.service.getVolunteerType(Constant.NATION).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                nationals.postValue(response.datas)
            }
        })
    }


    /**
     * 发送验证码
     */
    fun sendCode() {
        mPresenter.value?.loading = true
        if (phone.isNullOrEmpty()) {
            phone = SPUtils.getInstance().getString(SPKey.PHONESTR)
        }
        VolunteerRepository.service
            .sendMsg(phone, Constant.VOLUNTEER_TEAM_CODE_TYPE)
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

    fun getAttributions() {
        VolunteerRepository.service.getVolunteerRegions(region).excute(object : BaseObserver<VolunteerRegion>() {
            override fun onSuccess(response: BaseResponse<VolunteerRegion>) {
                attributions.postValue(response.datas)
            }
        })
    }


    /**
     * 获取服务类型
     */
    var serviceTypes = MutableLiveData<MutableList<String>>()

    fun getServiceTypes() {
        VolunteerRepository.service.getVolunteerType(Constant.VOLUNTEER_SERVICE_TYPE).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                serviceTypes.postValue(response.datas)
            }
        })
    }

    var brands = MutableLiveData<MutableList<VolunteerBrandBean>>()
    var mCurrentPage = 1
    var pageSize = 20

    /**
     * 获取品牌列表
     */
    fun getBrandList() {
        VolunteerRepository.service.getVolunteerBrandList(pageSize, mCurrentPage).excute(object : BaseObserver<VolunteerBrandBean>() {
            override fun onSuccess(response: BaseResponse<VolunteerBrandBean>) {
                brands.postValue(response.datas)
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

    var uploadUrl = MutableLiveData<String>()

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
                URLEncoder.encode(file.name, "utf-8"),
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
                    Log.e("uploadFile:", "onNext  ${t.url}")
                    logo = t.url
                    uploadUrl.postValue(t.url)

                }

                override fun onError(e: Throwable) {
                    Log.e("uploadFile:", "onError" + e)
                    uploadUrl.postValue(null)


                }

            })
    }
}