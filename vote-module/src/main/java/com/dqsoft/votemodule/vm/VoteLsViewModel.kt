package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.VoteBean
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   VoteLsViewModel
 * @Author      luoyi
 * @Time        2020/11/9 10:18
 */
class VoteLsViewModel : BaseViewModel() {

    var currPage: Int = 1

    var searchKey: String = ""

    var voteBeans: MutableLiveData<BaseResponse<VoteBean>> = MutableLiveData()


    fun getVoteLs() {

        var param: HashMap<String, String> = hashMapOf()
        param["pageSize"] = "10"
        param["currPage"] = "$currPage"
        if (!searchKey.isNullOrEmpty()) {
            param["name"] = searchKey!!
        }

        VoteRespository.service.searchVoteList(param).excute(object : BaseObserver<VoteBean>() {
            override fun onSuccess(response: BaseResponse<VoteBean>) {
                voteBeans.postValue(response)
            }

            override fun onFailed(response: BaseResponse<VoteBean>) {
                voteBeans.postValue(null)
            }

        })
    }
}