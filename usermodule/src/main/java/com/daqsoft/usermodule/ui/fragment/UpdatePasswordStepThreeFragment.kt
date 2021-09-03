package com.daqsoft.usermodule.ui.fragment

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment

import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentResetPasswordFirstStepBinding
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.usermodule.databinding.FragmentResetPasswordThreeStepBinding
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
class UpdatePasswordStepThreeFragment : BaseFragment<FragmentResetPasswordThreeStepBinding, UpdatePasswordStepOneViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_reset_password_three_step

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

}
