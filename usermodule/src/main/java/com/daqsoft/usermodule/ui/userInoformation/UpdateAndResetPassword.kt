package com.daqsoft.usermodule.ui.userInoformation

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityResetPasswordBinding
import com.daqsoft.usermodule.ui.fragment.UpdatePasswordStepOneFragment
import com.daqsoft.usermodule.ui.fragment.UpdatePasswordStepThreeFragment
import com.daqsoft.usermodule.ui.fragment.UpdatePasswordStepTwoFragment
import com.daqsoft.usermodule.uitls.ResourceUtil

/**
 * 设置重置密码页面
 */
@Route(path = ARouterPath.UserModule.USER_UPDATE_RESET_PASSWORD)
class UpdateAndResetPasswordActivity :
    TitleBarActivity<ActivityResetPasswordBinding, UpdateAndResetPasswordViewModel>() {


    override fun getLayout(): Int = R.layout.activity_reset_password

    override fun initData() {

    }

    override fun initView() {
        var updatePasswordStepOneFragment = UpdatePasswordStepOneFragment()
        transactFragment(R.id.frameLayout, updatePasswordStepOneFragment)
        mBinding.loading.setSteps(0)
    }

    override fun injectVm(): Class<UpdateAndResetPasswordViewModel> =
        UpdateAndResetPasswordViewModel::class.java

    override fun setTitle(): String = ResourceUtil.getStringResource(this, R.string.user_login)

    /**
     * 去到下一步
     */
    fun next(v: View){

        when (mBinding.loading.currentStep){
            0->{
                var updatePasswordStepTweFragment = UpdatePasswordStepTwoFragment()
                transactFragment(R.id.frameLayout, updatePasswordStepTweFragment)
                mBinding.loading.setSteps(1)
//                v.text = getString(R.string.user_str_conform)
            }
            1->{
                var updatePasswordStepThreeFragment = UpdatePasswordStepThreeFragment()
                transactFragment(R.id.frameLayout, updatePasswordStepThreeFragment)
                mBinding.loading.setSteps(1)
            }
        }

    }

}

class UpdateAndResetPasswordViewModel : BaseViewModel() {

}