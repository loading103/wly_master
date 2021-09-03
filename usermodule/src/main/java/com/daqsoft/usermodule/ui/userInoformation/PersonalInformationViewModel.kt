package com.daqsoft.usermodule.ui.userInoformation

import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebStorage
import androidx.lifecycle.MutableLiveData
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.utils.JpushAliasUtils
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.google.gson.Gson
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.nereo.multi_image_selector.bean.ImgBean
import me.nereo.multi_image_selector.upload.UploadRequestBody
import me.nereo.multi_image_selector.utils.BitmapUtils
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.URLEncoder

/**
 * @Description
 * @ClassName   PersonalInformationViewModel
 * @Author      PuHua
 * @Time        2019/10/31 17:47
 */
class PersonalInformationViewModel : BaseViewModel() {
    /**
     * 头像
     */
    val head = MutableLiveData<String>()
    val headLocal = MutableLiveData<String>()

    /**
     * 昵称
     */
    val nickname = MutableLiveData<String>()

    /**
     * 性别
     */
    val genderCode = MutableLiveData<Int>()

    /**
     * 性别
     */
    val gender = MutableLiveData<String>()

    /**
     * 个人信息
     */
    var userBean = MutableLiveData<UserBean>()

    /**
     * 生日集合
     */
    var birthdays: Array<String>? = null

    var bean: UserBean? = null

    /**
     * 获取个人信息
     */
    fun getUserInformation() {
        mPresenter.value?.loading = true
        UserRepository().userService.getUserInformation()
            .excute(object : BaseObserver<UserBean>() {
                override fun onSuccess(response: BaseResponse<UserBean>) {
                    userBean.value = response.data
                    gender.value = userBean.value!!.sex
                    head.postValue(response.data?.headUrl)
                    refresh()
                }
            })
    }

    /**
     * 修改昵称
     */
    var updateNickName: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.nickName)
                .withString(
                    IntentConstant.TYPE,
                    UpdatePersonalInformationViewModel.nickName
                )
                .navigation()
        }
    }

    /**
     * 修改个性签名
     */
    var updateSign: ReplyCommand = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_INFORMATION)
                .withString(IntentConstant.OBJECT, userBean.value!!.signature)
                .withString(IntentConstant.TYPE, UpdatePersonalInformationViewModel.signature)
                .navigation()
        }
    }

    /**
     * 更多
     */
    var updateMore: ReplyCommand = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_MORE)
                .navigation()
        }
    }

    /**
     * 去到收货地址列表页
     */
    var gotoAddressList: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_RECEIVE_ADDRESS)
                .navigation()
        }
    }

    /**
     * 去到常用联系人列表页
     */
    var gotoContactList: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)

                .navigation()
        }
    }

    /**
     * 去到绑定手机页
     */
    var gotoBindPhone: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_BIND_PHONE)
                .navigation()
        }
    }

    /**
     * 去到修改密码列表页
     */
    var goToUpdatePassword: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_UPDATE_RESET_PASSWORD)
                .navigation()
        }
    }

    /**
     * 实名状态
     */
    val realNameStatus = MutableLiveData<Int>()

    fun refresh() {
        UserRepository().userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    realNameStatus.postValue(response.data?.realNameStatus)
                    SPUtils.getInstance()
                        .put(SPKey.USER_CENTER_TOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.UUID, response.data?.unid)
//                    userBean.postValue(response.data)
                    getSiteInfo()

                    SPUtils.getInstance().put(SPKey.SITEID, response.data?.siteId ?: -1)
                    SPUtils.getInstance().put(SPKey.ENCRYPTION, response.data?.encryption ?: "")
                    SPUtils.getInstance().put(SPKey.VIPID, response.data?.vipId ?: -1)
                    SPUtils.getInstance().put(SPKey.UID, response.data?.unid)
                    SPUtils.getInstance().put(SPKey.PHONE, response.data?.phone)
                    SPUtils.getInstance().put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                }
            })
    }

    /**
     * 退出登录
     */
    var logout: ReplyCommand = object : ReplyCommand {
        override fun run() {
            SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_RESERATION_INFO).clear()
            SPUtils.getInstance().put(SPKey.HERITAGE_PEOPLEID, "")
            SPUtils.getInstance().put(SPKey.NICK_NAME, "")
            SPUtils.getInstance().put(SPKey.NICK_NAME, "")
            SPUtils.getInstance().put(SPKey.ALLOW_KEY, false)
            SPUtils.getInstance().put(SPKey.REALNAMESTATUS, -1)
            EventBus.getDefault().post(LoginStatusEvent(LoginStatusEvent.EXIT_LOGIN))
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
            WebStorage.getInstance().deleteAllData()
            // 退出删除别名
            JpushAliasUtils.clearAlias(BaseApplication.context)
            finish.postValue(true)
        }
    }


    /**
     * 修改个人信息
     */
    fun updatePsersonalInformation(key: String, value: String) {
        mPresenter.value?.loading = true
        val map: HashMap<String, String> = HashMap()
        map[key] = value
        UserRepository().userService.updateUserInformation(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        getUserInformation()

                    }
                }
            })
    }

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

        mPresenter.postValue(mPresenter.value)
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
                    updatePsersonalInformation(
                        UpdatePersonalInformationViewModel.headUrl,
                        head.value!!
                    )
                }

                override fun onError(e: Throwable) {
                    Log.e("uploadFile:", "onError" + e)

                }

            })
    }

    /**
     * 小电商登录
     */
    fun loginElectronic(uuid: String, token: String) {
        mPresenter.value?.loading = true
        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
        ElectronicRepository.electronicService.login(uuid, token, encry)
            .excut(object : ElectronicObserver<ElectronicLogin>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicLogin>) {

                    var userLogin = response.data

                    SPUtils.getInstance().put(SPKey.SESSIONID, userLogin?.sessionId)

                }

            })
    }

    /**
     * 获取站点信息
     */
    fun getSiteInfo() {
        mPresenter.value?.loading = true
        UserRepository().userService.getSiteInfo()
            .excute(object : BaseObserver<SiteInfo>() {
                override fun onSuccess(response: BaseResponse<SiteInfo>) {
                    SPUtils.getInstance().put(SPKey.DOMAIN, response.data?.shopUrl)
                    SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)

                    loginElectronic(
                        SPUtils.getInstance().getString(SPKey.UUID),
                        SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                    )
                }

            })
    }


}