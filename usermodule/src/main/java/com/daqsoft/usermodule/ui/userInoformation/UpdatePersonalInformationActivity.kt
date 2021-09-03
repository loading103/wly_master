package com.daqsoft.usermodule.ui.userInoformation

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityUpdatePersonalInformationBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.bean.UserBean
import com.daqsoft.usermodule.uitls.StringUtils
import org.jetbrains.anko.toast

/**
 * @Description 修改个人信息页面
 * @ClassName   UpdatePersonalInformationActivity
 * @Author      PuHua
 * @Time        2019/11/4 18:15
 */
@Route(path = ARouterPath.UserModule.USER_UPDATE_INFORMATION)
class UpdatePersonalInformationActivity :
    TitleBarActivity<ActivityUpdatePersonalInformationBinding, UpdatePersonalInformationViewModel>() {
    @JvmField
    @Autowired(name = IntentConstant.OBJECT)
    var userBean: String? = null

    @JvmField
    @Autowired(name = IntentConstant.TYPE)
    var type: String? = null


    override fun getLayout(): Int = R.layout.activity_update_personal_information

    override fun initData() {

    }

    override fun initView() {

        mModel.type = this!!.type.toString()

        mBinding.content = userBean
        when (type) {
            UpdatePersonalInformationViewModel.nickName -> {
                // 修改昵称
//                mBinding.content = userBean!!.nickName
                mBinding.labelText = getString(R.string.user_nickname)
                mBinding.limitText = getString(R.string.user_update_nick_limit)
                mBinding.edit.maxElement = 10
            }
            UpdatePersonalInformationViewModel.signature -> {
                // 修改个性签名
//                mBinding.content = userBean!!.signature
                mBinding.labelText = getString(R.string.user_personal_sign)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
            UpdatePersonalInformationViewModel.address ->{
                // 修改家庭住址
//                mBinding.content = userBean!!.address
                mBinding.labelText = getString(R.string.user_more_f_address)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
            UpdatePersonalInformationViewModel.constellation ->{
                // 修改星座
//                mBinding.content = userBean!!.constellation
                mBinding.labelText = getString(R.string.user_more_star)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
            UpdatePersonalInformationViewModel.school ->{
                // 修改学校
//                mBinding.content = userBean!!.school
                mBinding.labelText = getString(R.string.user_more_school)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
            UpdatePersonalInformationViewModel.workplace ->{
                // 修改公司
//                mBinding.content = userBean!!.workplace
                mBinding.labelText = getString(R.string.user_more_company)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
            UpdatePersonalInformationViewModel.email ->{
                // 修改邮箱
//                mBinding.content = userBean!!.email
                mBinding.labelText = getString(R.string.user_more_email)
                mBinding.limitText = getString(R.string.user_update_limit)
                mBinding.edit.maxElement = 50
            }
        }
        if(type !=UpdatePersonalInformationViewModel.email){
            StringUtils().setProhibitEmoji(mBinding.edit)
        }
    }

    override fun injectVm(): Class<UpdatePersonalInformationViewModel> =
        UpdatePersonalInformationViewModel::class.java

    override fun setTitle(): String {
        return when (type) {
            UpdatePersonalInformationViewModel.nickName -> getString(R.string.user_update_nick)

            UpdatePersonalInformationViewModel.signature -> getString(R.string.user_update_sign)

            UpdatePersonalInformationViewModel.address -> getString(R.string.user_update_address)
            UpdatePersonalInformationViewModel.constellation -> getString(R.string.user_update_address)
            UpdatePersonalInformationViewModel.school -> getString(R.string.user_update_school)
            UpdatePersonalInformationViewModel.workplace -> getString(R.string.user_update_company)
            UpdatePersonalInformationViewModel.email -> getString(R.string.user_update_email)

            else -> ""
        }

    }

    fun submit(v: View) {

        if (mBinding.content!!.isNullOrEmpty()) {
            toast(getString(R.string.user_update_nick_empty))
            return
        }
        mModel.updatePsersonalInformation(
            this!!.type.toString(),
            mBinding.content!!
        )
    }

}