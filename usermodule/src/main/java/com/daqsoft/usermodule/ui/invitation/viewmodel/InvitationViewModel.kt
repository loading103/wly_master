package com.daqsoft.usermodule.ui.invitation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.InviteInfo
import com.daqsoft.provider.network.net.UserRepository

/**
 * @Description
 * @ClassName   InvitationViewModel
 * @Author      luoyi
 * @Time        2020/7/2 11:50
 */
class InvitationViewModel : BaseViewModel() {

    var inviteInfoLd: MutableLiveData<InviteInfo> = MutableLiveData()

    fun getInviteInfo() {
        UserRepository.userService?.getMineInvite().excute(object : BaseObserver<InviteInfo>() {

            override fun onSuccess(response: BaseResponse<InviteInfo>) {
                inviteInfoLd?.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<InviteInfo>) {
                inviteInfoLd?.postValue(null)
            }

        })
    }

}