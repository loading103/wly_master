package com.daqsoft.usermodule.ui.userInoformation

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.view.ClearEditText
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityLoginBinding
import com.daqsoft.usermodule.uitls.RegexUtils
import com.daqsoft.usermodule.uitls.ResourceUtil
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

@Route(path = ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
open class LoginActivity<T : LoginRegisterViewModel> :
    TitleBarActivity<ActivityLoginBinding, LoginRegisterViewModel>() {
    /**
     * 保存的手机号
     */
    var phone: String = ""
    /**
     * 是否同意用户协议
     */
    var isAgreement = true
    /**
     * 验证码是否填写
     */
    var isCodeFixed = false
    @JvmField
    @Autowired
    var isBackHome: Boolean = false

    @JvmField
    @Autowired
    var isWebView: Boolean = false

    val private_xj: String="http://project.daqsoft.com/privacy/yxj.html"
    val private_ws: String="http://project.daqsoft.com/privacy/xj.html"
    val private_sc: String="http://project.daqsoft.com/privacy/zytf.html"

    override fun getLayout(): Int = R.layout.activity_login

    override fun initData() {

    }

    override fun initView() {
        mBinding.vm = mModel
        submit.text = ResourceUtil.getStringResource(this, R.string.user_login)
        goRegister.text = ResourceUtil.getStringResource(this, R.string.user_str_go_register)

        if (TextUtils.isEmpty(phone)) {
            mModel.phone.set(phone)
        }
        // 验证输入的是否是正确的手机号
        et_phone.afterTextChangedListenr = ClearEditText.AfterTextChangedListenr {

            bind_phone_code_avail.isEnabled = RegexUtils.isMobileExact(it)
//            mModel.phone.set(it)
            submit.isEnabled = isCodeFixed && isAgreement && et_phone.text?.length==11
        }
        submit.onNoDoubleClick {
            mModel.login(isWebView)
        }
        bind_phone_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isCodeFixed = s.toString().isNotEmpty()
                submit.isEnabled = isCodeFixed && isAgreement && et_phone.text?.length==11
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        mModel.viewListener = object : LoginRegisterViewModel.ViewListener {
            override fun onCountDown() {
                toast(getString(R.string.user_str_code_send_success))
                if (d != null) {
                    shutDown = true
                }
                initTimer()
            }
        }
        mModel.codeType = "PHONE_LOGIN"
        if (SPUtils.getInstance().getString(SPUtils.Config.TOKEN).isNotEmpty()) {
            // 登录成功
            EventBus.getDefault().post(LoginStatusEvent(LoginStatusEvent.LOGIN))


            if (isBackHome) {
                ARouter.getInstance()
                    .build(ARouterPath.MainModule.MAIN_ACTIVITY)
                    .navigation()
            }
            finish()
        }
    }

    override fun injectVm(): Class<LoginRegisterViewModel> =
        LoginRegisterViewModel::class.java

    override fun setTitle(): String = ResourceUtil.getStringResource(this, R.string.user_login)


    /**
     * 响应发送验证码的事件
     */
    open fun sendCode(v: View) {
        if (TextUtils.isEmpty(mModel.phone.get())) {
            toast(getString(R.string.user_str_have_account))
            return
        }
        mModel.senCode()
    }

    open fun goAnother(v: View) {
        ARouter.getInstance()
            .build(ARouterPath.UserModule.USER_REGISTER_ACTIVITY)
            .withString("registphone",mModel.registPhone)
            .navigation()
        finish()

    }

    /**
     * 计时器相关
     */
    var d: Disposable? = null
    var shutDown = false
    fun initTimer() {
        var timeLong: Long = 60
        d = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { timeLong >= 0 }
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    bind_phone_code_avail.setText(to)
                    bind_phone_code_avail.isEnabled = false
                } else {
                    bind_phone_code_avail.setText(getString(R.string.user_get_code))
                    bind_phone_code_avail.isEnabled = true
                }

            }
        shutDown = false
    }
}

/**
 * 注册
 */
@Route(path = ARouterPath.UserModule.USER_REGISTER_ACTIVITY)
class RegisterActivity : LoginActivity<RegisterViewModel>() {
    @JvmField
    @Autowired
    var registphone:String=""

    override fun initView() {
        super.initView()
        submit.text = ResourceUtil.getStringResource(this, R.string.user_str_register)
        goRegister.text = ResourceUtil.getStringResource(this, R.string.user_str_go_login)
        goRegisterLabel.text = ResourceUtil.getStringResource(this, R.string.user_str_have_account)
        llAgree.visibility = View.VISIBLE
        if(!registphone.isNullOrEmpty()){
            mModel.phone.set(registphone)
        }

        registerAgreement.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", "用户发布信息协议")
                .withString("html",  BaseApplication.webSiteUrl+"registration-agreement")
                .navigation()
        }
        userPostAgreement.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", "用户发布信息协议")
                .withString("html",  BaseApplication.webSiteUrl+"user-publishing-protocol")
                .navigation()
        }
        userPrivacyRights.setOnClickListener {
            if (BaseApplication.appArea == "sc") {
                goWebView(private_sc,"隐私声明")
            }else if (BaseApplication.appArea == "ws") {
                goWebView(private_ws,"隐私声明")
            } else {
                goWebView(private_xj,"隐私声明")
            }
        }
        isAgreement=false
        check.isChecked = isAgreement
        check.setOnCheckedChangeListener { buttonView, isChecked ->
            isAgreement = isChecked
            submit.isEnabled = isCodeFixed && isAgreement && et_phone.text?.length==11
        }
        // 设置为注册类型
        mModel.codeType = "PHONE_REGISTER"
    }

    override fun injectVm(): Class<LoginRegisterViewModel> =
        RegisterViewModel::class.java as Class<LoginRegisterViewModel>

    override fun setTitle(): String =
        ResourceUtil.getStringResource(this, R.string.user_str_register)

    override fun goAnother(v: View) {
        ARouter.getInstance()
            .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
            .navigation()
        finish()
    }

}
private fun goWebView(url :String ,title :String ) {
    ARouter
        .getInstance()
        .build(ARouterPath.Provider.WEB_ACTIVITY)
        .withString("mTitle", title)
        .withString("html", url)
        .withString("isscar", "true")
        .navigation()
}
/**
 * 绑定手机号
 */
@Route(path = ARouterPath.UserModule.USER_BIND_PHONE)
class BindPhoneActivity : LoginActivity<RegisterViewModel>() {

    override fun initView() {
        mBinding.vm = mModel
        phone = SPUtils.getInstance().getString("phone")
        if (TextUtils.isEmpty(phone)) {
            mModel.phone.set(phone)
        }
        // 验证输入的是否是正确的手机号
        et_phone.afterTextChangedListenr = ClearEditText.AfterTextChangedListenr {

            bind_phone_code_avail.isEnabled = RegexUtils.isMobileExact(it)
//            mModel.phone.set(it)

        }
        bind_phone_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isCodeFixed = s.toString().isNotEmpty()
                submit.isEnabled = isCodeFixed && isAgreement && et_phone.text?.length==11
            }

            override fun afterTextChanged(s: Editable?) {


            }

        })
        mModel.viewListener = object : LoginRegisterViewModel.ViewListener {
            override fun onCountDown() {
                toast(getString(R.string.user_str_code_send_success))
                if (d != null) {
                    shutDown = true
                }
                initTimer()
            }
        }
        submit.text = ResourceUtil.getStringResource(this, R.string.user_bind_phone)
        goRegister.text = ResourceUtil.getStringResource(this, R.string.user_bind_phone)
        goRegisterLabel.text = ResourceUtil.getStringResource(this, R.string.user_str_have_account)
        llAgree.visibility = View.VISIBLE

        registerAgreement.setOnClickListener {

        }
        userPostAgreement.setOnClickListener {

        }
        check.isChecked = isAgreement
        check.setOnCheckedChangeListener { buttonView, isChecked ->
            isAgreement = isChecked
            submit.isEnabled = isCodeFixed && isAgreement && et_phone.text?.length==11
        }
        // 设置为注册类型
        mModel.codeType = "PHONE_REGISTER"
        ll_to_another.visibility = View.GONE
    }

    override fun injectVm(): Class<LoginRegisterViewModel> =
        RegisterViewModel::class.java as Class<LoginRegisterViewModel>

    override fun setTitle(): String =
        ResourceUtil.getStringResource(this, R.string.user_bind_phone)

    override fun goAnother(v: View) {
        ARouter.getInstance()
            .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
            .navigation()
        finish()
    }

}

