package com.daqsoft.usermodule.ui.fragment

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse

import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentResetPasswordFirstStepBinding
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.support.v4.toast
import java.util.concurrent.TimeUnit

/**
 *  设置密码第一步
 * @author PuHua
 * @version 1.0.0
 * @date 2019-11-11
 * @since JDK 1.8.0_191
 */
class UpdatePasswordStepOneFragment : BaseFragment<FragmentResetPasswordFirstStepBinding, UpdatePasswordStepOneViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_reset_password_first_step

    override fun initData() {
        mModel.sendFinish.observe(this, Observer {
            if(it){
                initTimer()
            }
        })
    }

    override fun initView() {
//        mBinding.mf = this
        mBinding.bindPhoneCodeAvail.onNoDoubleClick {
            sendCode()
        }
    }

    override fun injectVm(): Class<UpdatePasswordStepOneViewModel> = UpdatePasswordStepOneViewModel::class.java


    /**
     * 响应发送验证码的事件
     */
    private fun sendCode() {
        if (TextUtils.isEmpty(mModel.phone.get())) {
            toast("请输入手机号!")
            return
        }
        mModel.senCode()
    }

    /**
     * 响应登录/注册按钮事件
     */
    open fun submit(v: View) {
        if(mBinding.etPhone.text.toString().isNullOrEmpty()){
            toast("请输入手机号!")
            return
        }
        if(mBinding.bindPhoneCode.text.toString().isNullOrEmpty()){
            toast("请输入验证码!")
            return
        }
        mModel.checkMsg()
    }


    private fun initTimer() {
        var timeLong: Long = 60
        var d = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map { count ->
                timeLong--
            }
            .subscribe { time ->
                if (time > 0) {
                    var to = getString(R.string.user_str_count_down, time)
                    bind_phone_code_avail.text = to
                    bind_phone_code_avail.isEnabled = false
                } else {
                    bind_phone_code_avail.text = getString(R.string.user_get_code)
                    bind_phone_code_avail.isEnabled = true
                }
            }
    }
}

class UpdatePasswordStepOneViewModel : BaseViewModel(){
    var phone = ObservableField<String>("")
    var code = ObservableField<String>("")

    var sendFinish = MutableLiveData<Boolean>()

//    /**
//     * 发送验证码
//     */
    fun senCode() {
        mPresenter.value?.loading = true
          UserRepository().userService.sendMsg(phone.get()!!, "PHONE_LOGIN")
              .excute(object :BaseObserver<Any>(){
                  override fun onSuccess(response: BaseResponse<Any>) {
                      if(response.code == 0){
                          toast.postValue("验证码已发送")
                          sendFinish.postValue(true)
                      }else{
                          toast.postValue(response.message)
                      }
                  }

              })

    }

    fun checkMsg(){
        UserRepository().userService.checkMsg(phone.get()!!,"PHONE_LOGIN",code.get()!!)
            .excute(object :BaseObserver<Any>(){
                override fun onSuccess(response: BaseResponse<Any>) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
    }

}