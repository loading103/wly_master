package com.daqsoft.usermodule.ui.userInoformation

import androidx.databinding.ObservableField
import cn.jpush.android.api.JPushInterface
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ElectronicLogin
import com.daqsoft.provider.bean.RegisterData
import com.daqsoft.provider.bean.SiteInfo
import com.daqsoft.provider.bean.UserLogin
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.event.UpdateWebViewEvent
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.utils.JpushAliasUtils
import org.greenrobot.eventbus.EventBus


open class LoginRegisterViewModel : BaseViewModel() {
    /**
     * 验证码发送类型
     */
    var codeType = "PHONE_LOGIN"
    var phone = ObservableField<String>("")
    var code = ObservableField<String>("")
    var userLogin: UserLogin? = null
    var registPhone: String? = ""

    /**
     * 登录
     */
    open fun login(webView: Boolean) {
        mPresenter.value?.loading = true
        UserRepository().userService.login(code.get()!!, phone.get()!!)
            .excute(object : BaseObserver<UserLogin>(mPresenter) {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    registPhone=""
                    userLogin = response.data
                    // 重置token
                    SPUtils.getInstance().put(SPUtils.Config.TOKEN, userLogin?.userToken)
                    SPUtils.getInstance().put(SPKey.REALNAMESTATUS, userLogin?.realNameStatus ?: 0)
                    SPUtils.getInstance().put(SPKey.UID, userLogin?.unid)
                    SPUtils.getInstance().put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                    if(webView){
                        EventBus.getDefault().post(UpdateWebViewEvent(userLogin?.userToken))
                    }
                    SPUtils.getInstance().put(SPKey.USER_CENTER_TOKEN, userLogin?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.UUID, userLogin?.unid)
                    if (!userLogin?.mobile.isNullOrEmpty()) {
                        SPUtils.getInstance().put(SPKey.MOBILE, userLogin?.mobile)
                    }
                    if (userLogin?.siteId != null) {
                        SPUtils.getInstance().put(SPKey.SITEID, userLogin?.siteId!!)
                    }
                    SPUtils.getInstance().put(SPKey.PHONE, response.data?.phone)
                    SPUtils.getInstance().put(SPKey.PHONESTR, phone.get())
                    SPUtils.getInstance().put(SPKey.NICK_NAME, response.data?.nickName)
                    SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                    SPUtils.getInstance().put(SPKey.ENCRYPTION, response.data?.encryption)
                    SPUtils.getInstance().put(SPKey.VOLUNTEER,response.data?.volunteerStatus.toString())
                    SPUtils.getInstance().put(SPKey.VOLUNTEER_TEAM,response.data?.volunteerTeamStatus.toString())
                    if (userLogin != null){
                        SPUtils.getInstance().put(SPKey.VIPID, userLogin!!.vipId)
                        //设置推送别名
                        JpushAliasUtils.setAlias(BaseApplication.context)
                    }
                    EventBus.getDefault().post(LoginStatusEvent(LoginStatusEvent.LOGIN))
                    // 走小电商登录
                    getSiteInfo()
//                    ARouter.getInstance()
//                        .build(ARouterPath.MainModudle.MAIN_ACTIVITY)
//                        .navigation()

                    finish.postValue(true)
                }


                override fun onFailed(response: BaseResponse<UserLogin>) {
                    // 为了处理测试得奇葩bug
                    if(response.message!=null && response.message!!.contains("先进行注册")){
                        if (!phone?.get().isNullOrEmpty()) {
                            registPhone=phone?.get()
                        }
                    }else{
                        registPhone=""
                    }
                }

            })
    }

    fun getSiteInfo() {
        mPresenter.value?.loading = true
        UserRepository().userService.getSiteInfo()
            .excute(object : BaseObserver<SiteInfo>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfo>) {
                    SPUtils.getInstance().put(SPKey.DOMAIN, response.data?.shopUrl)
                    SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
                    loginElectronic(userLogin!!.unid!!, userLogin!!.userCenterToken)
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
     * 发送验证码
     */
    fun senCode() {
        mPresenter.value?.loading = true
        UserRepository().userService
            .sendMsg(phone.get()!!, codeType)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        viewListener?.onCountDown()

                    }
                }

            })

    }

    /**
     * 验证验证码
     */
    fun checkMsg() {
        UserRepository().userService.checkMsg(phone.get()!!, codeType!!, code.get()!!)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        login(false)
                    } else {
                        toast.postValue(response.message)
                    }

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

/**
 * @des 注册model
 * @author PuHua
 * @Date 2019/12/25 9:42
 */
class RegisterViewModel : LoginRegisterViewModel() {
    override fun login(webView: Boolean) {
        /**
         * 注册
         */
        UserRepository().userService.register(code.get()!!, phone.get()!!)
            .excute(object : BaseObserver<RegisterData>(mPresenter) {
                override fun onSuccess(response: BaseResponse<RegisterData>) {
                    if (response.code == 0) {

                        var registerData = response.data
                        SPUtils.getInstance().put(SPUtils.Config.TOKEN, registerData!!.userToken)
                        SPUtils.getInstance().put(SPKey.SITEID, registerData!!.siteId)
                        SPUtils.getInstance().put(SPKey.PHONESTR, phone.get())
                        toast.postValue("注册成功!")
                        finish.postValue(true)
//                        ARouter.getInstance()
//                            .build(ARouterPath.MainModudle.MAIN_ACTIVITY)
//                            .navigation()
                    }
                }

            })

    }
}