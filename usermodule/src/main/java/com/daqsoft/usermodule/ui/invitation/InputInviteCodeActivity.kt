package com.daqsoft.usermodule.ui.invitation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.InviteInputBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityInputInviteCodeBinding

/**
 * @Description 输入邀请码页面
 * @ClassName   InputInviteCodeActivity
 * @Author      luoyi
 * @Time        2020/7/28 16:32
 */
@Route(path = ARouterPath.UserModule.USER_INIVITE_INPUT_CODE_ACITIVITY)
class InputInviteCodeActivity : TitleBarActivity<ActivityInputInviteCodeBinding, InputInviteViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_input_invite_code
    }

    override fun setTitle(): String {
        return "邀请有礼"
    }

    override fun injectVm(): Class<InputInviteViewModel> {
        return InputInviteViewModel::class.java
    }

    override fun initView() {
        mBinding.tvInputInviteCode.onNoDoubleClick {
            var code: String? = mBinding.edtInputInviteCode.text.toString()
            if (code.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入邀请码再提交！")
            } else {
                showLoadingDialog()
                mModel.inputCode(code)
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.inputCodeLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                ARouter.getInstance().build(ARouterPath.UserModule.USER_INVITE_SUCCESS_ACTIVITY)
                    .withString("ownHeader", it.myHeadUrl)
                    .withString("otherHeader", it.otherHeadUrl)
                    .navigation()
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && !it.message.isNullOrEmpty()) {
                ToastUtils.showMessage(it.message)
            }
        })
    }

    override fun initData() {
    }

}

class InputInviteViewModel : BaseViewModel() {
    var inputCodeLd: MutableLiveData<InviteInputBean> = MutableLiveData()
    fun inputCode(code: String) {
        UserRepository.userService.inputInviteCode(code).excute(object : BaseObserver<InviteInputBean>() {
            override fun onSuccess(response: BaseResponse<InviteInputBean>) {
                inputCodeLd?.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<InviteInputBean>) {
                mError.postValue(response)
            }

        })
    }
}