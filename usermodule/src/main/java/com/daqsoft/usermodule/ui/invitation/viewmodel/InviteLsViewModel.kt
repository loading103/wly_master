package com.daqsoft.usermodule.ui.invitation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.InviteBean
import com.daqsoft.provider.network.net.UserRepository

/**
 * @Description
 * @ClassName   InviteLsViewModel
 * @Author      luoyi
 * @Time        2020/7/27 16:35
 */
class InviteLsViewModel : BaseViewModel() {

    var inviteLsLd: MutableLiveData<MutableList<InviteBean>> = MutableLiveData()

    /**
     * 获取邀请列表
     */
    fun getInviteLs(pageSize: Int, currPage: Int) {
        mPresenter?.value?.loading = false
        UserRepository.userService.getMineInviteLs(pageSize, currPage)
            .excute(object : BaseObserver<InviteBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<InviteBean>) {
                    inviteLsLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<InviteBean>) {
                    mError.postValue(response)
                }

            })
    }
}