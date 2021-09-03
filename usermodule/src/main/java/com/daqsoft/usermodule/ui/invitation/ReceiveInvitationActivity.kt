package com.daqsoft.usermodule.ui.invitation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.InviteCodeInfo
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityReceiveInvationBinding

/**
 * @Description
 * @ClassName   ReceiveInvitationActivity
 * @Author      luoyi
 * @Time        2020/7/28 16:10
 */
@Route(path = ARouterPath.UserModule.USER_INIVITE_RECEIVE_ACTIVITY)
class ReceiveInvitationActivity : TitleBarActivity<ActivityReceiveInvationBinding, ReceiveInvitationViewModel>() {

    @JvmField
    @Autowired
    var code: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_receive_invation
    }

    override fun setTitle(): String {
        return "邀请有礼"
    }

    override fun injectVm(): Class<ReceiveInvitationViewModel> {
        return ReceiveInvitationViewModel::class.java
    }

    override fun initView() {
        if (!code.isNullOrEmpty()) {
            mBinding.tvMineInviteCode.text = "邀请码：${code}"
        }
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.inviteCodeInfoLd.observe(this, Observer {
            if (it != null) {
                mBinding.tvRecevieInvivteDesc.text = "您的好友 ${it.nickName} 正在邀请您成为${resources.getString(R.string.app_name)}的注册会员"
            }
        })
    }

    private fun initViewEvent() {
        mBinding.tvInputInviteCode.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouterPath.UserModule.toInPutInviteCodePage()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouterPath.UserModule.toLoginPage()
            }
        }
        mBinding.tvMineInviteLs.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouterPath.UserModule.toMineInvitePage()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouterPath.UserModule.toLoginPage()
            }
        }
        mBinding.tvCopyInviteCode.onNoDoubleClick {
            if (!code.isNullOrEmpty()) {
                SystemHelper.copyContentToClip(code!!)
                ToastUtils.showMessage("复制成功！")
            }
        }
    }

    override fun initData() {
        if(!code.isNullOrEmpty())
        mModel.getInviteCodeInfo(code!!)
    }
}

class ReceiveInvitationViewModel : BaseViewModel() {

    var inviteCodeInfoLd: MutableLiveData<InviteCodeInfo> = MutableLiveData()


    fun getInviteCodeInfo(invatieCode: String) {
        mPresenter?.value?.loading = false
        UserRepository.userService.getInviteCodeInfo(invatieCode).excute(object : BaseObserver<InviteCodeInfo>() {
            override fun onSuccess(response: BaseResponse<InviteCodeInfo>) {
                inviteCodeInfoLd?.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<InviteCodeInfo>) {
                mError.postValue(response)
            }

        })
    }

}