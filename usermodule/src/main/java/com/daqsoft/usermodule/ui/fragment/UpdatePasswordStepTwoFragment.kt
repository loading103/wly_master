package com.daqsoft.usermodule.ui.fragment

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.daqsoft.baselib.base.BaseFragment

import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentResetPasswordFirstStepBinding
import com.daqsoft.usermodule.databinding.FragmentResetPasswordTwoStepBinding
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.support.v4.toast
import java.util.concurrent.TimeUnit

/**
 *  设置密码第二步
 * @author PuHua
 * @version 1.0.0
 * @date 2019-11-11
 * @since JDK 1.8.0_191
 */
class UpdatePasswordStepTwoFragment : BaseFragment<FragmentResetPasswordTwoStepBinding, UpdatePasswordStepOneViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_reset_password_two_step

    override fun initData() {
    }

    override fun initView() {
//        mBinding.mf = this
    }

    override fun injectVm(): Class<UpdatePasswordStepOneViewModel> = UpdatePasswordStepOneViewModel::class.java

    /**
     * 响应登录/注册按钮事件
     */
    open fun submit(v: View) {

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
                    bind_phone_code_avail.setText(to)
                    bind_phone_code_avail.isEnabled = false
                } else {
                    bind_phone_code_avail.setText(getString(R.string.user_get_code))
                    bind_phone_code_avail.isEnabled = true
                }

            }
    }
}
