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
 * @ClassName   VoteInPartLsViewModel
 * @Author      luoyi
 * @Time        2020/11/17 13:32
 */
class VoteInPartLsViewModel : BaseViewModel() {
    public var currPage: Int = 1
    public var voteId: String? = ""

    var voteWorkListLd: MutableLiveData<BaseResponse<MineVoteWorkBean>> = MutableLiveData()

    var voteWorkOpeartionLd: MutableLiveData<Boolean> = MutableLiveData()

    fun getVoteWorkList() {
        var param: HashMap<String, String> = hashMapOf()
        param["voteId"] = voteId ?: ""
        mPresenter?.value?.loading = false
        VoteRespository.service.getMineVoteWorkList(param)
            .excute(object : BaseObserver<MineVoteWorkBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineVoteWorkBean>) {
                    voteWorkListLd.postValue(response)
                }

                override fun onFailed(response: BaseResponse<MineVoteWorkBean>) {
                    voteWorkListLd.postValue(null)
                }

            })

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