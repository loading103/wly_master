package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.MineVoteWorkBean
import com.daqsoft.provider.bean.VoteBean
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   MineVotes
 * @Author      luoyi
 * @Time        2020/11/19 11:57
 */
class MineVotesViewModel : BaseViewModel() {
    var currPage: Int = 1

    var mineVoteLsLd: MutableLiveData<BaseResponse<VoteBean>> = MutableLiveData()


    fun getMineVoteList() {
        VoteRespository.service.getMineVoteList(10, currPage).excute(
            object : BaseObserver<VoteBean>() {
                override fun onSuccess(response: BaseResponse<VoteBean>) {
                    mineVoteLsLd.postValue(response)
                }

                override fun onFailed(response: BaseResponse<VoteBean>) {
                    mineVoteLsLd.postValue(response)
                }

            }
        )
    }
}