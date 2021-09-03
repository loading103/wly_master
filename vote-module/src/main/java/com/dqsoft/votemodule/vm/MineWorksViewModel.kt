package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.MineVoteWorkBean
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   MineWorksViewModel
 * @Author      luoyi
 * @Time        2020/11/19 13:32
 */
class MineWorksViewModel : BaseViewModel() {

    var currPage: Int = 1

    var mineWorkLsLd: MutableLiveData<BaseResponse<MineVoteWorkBean>> = MutableLiveData()

    var voteWorkOpeartionLd: MutableLiveData<Boolean> = MutableLiveData()

    fun getMineInPartWorkList() {
        var params: HashMap<String, String> = hashMapOf()
        params["currPage"] = currPage.toString()
        params["pageSize"] = "10"
        VoteRespository.service.getMineVoteWorkList(params).excute(
            object : BaseObserver<MineVoteWorkBean>() {
                override fun onSuccess(response: BaseResponse<MineVoteWorkBean>) {
                    mineWorkLsLd.postValue(response)
                }

                override fun onFailed(response: BaseResponse<MineVoteWorkBean>) {
                    mineWorkLsLd.postValue(response)
                }

            }
        )
    }

    fun operationWorkDetail(proId: String, status: String) {
        var params: HashMap<String, String> = hashMapOf()
        params["proId"] = proId
        params["status"] = status
        mPresenter?.value?.isNeedToastMessage = false
        mPresenter?.value?.loading = false
        VoteRespository.service.operationWork(params)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    voteWorkOpeartionLd.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("" + response.message)
                    voteWorkOpeartionLd.postValue(false)
                }

            })
    }

}