package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VoteResultBean
import com.daqsoft.provider.bean.VoteWorkDetailBean
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description 我的参与
 * @ClassName   MineVoteDetailViewModel
 * @Author      luoyi
 * @Time        2020/11/10 13:46
 */
class MineVoteDetailViewModel : BaseViewModel() {

    var voteDetailLd: MutableLiveData<VoteWorkDetailBean> = MutableLiveData()


    var voteWorkOpeartionLd: MutableLiveData<Boolean> = MutableLiveData()
    /**
     * 投票结果
     */
    var voteWorkLd: MutableLiveData<VoteResultBean> = MutableLiveData()
    /**
     * 获取作品详情
     */
    fun getVoteWorkDetail(proId: String) {
        mPresenter?.value?.loading = false
        VoteRespository.service.getVoteWorkInfo(proId)
            .excute(object : BaseObserver<VoteWorkDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VoteWorkDetailBean>) {
                    voteDetailLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VoteWorkDetailBean>) {
                    voteDetailLd.postValue(null)
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
    fun voteWork(proId: String) {
        VoteRespository.service.voteWork(proId).excute(object : BaseObserver<VoteResultBean>() {
            override fun onSuccess(response: BaseResponse<VoteResultBean>) {
                voteWorkLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VoteResultBean>) {
                if (!response.message.isNullOrEmpty()) {
                    ToastUtils.showMessage(response.message)
                    if(response.message.toString().contains("登录")){
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
                    }
                }
                voteWorkLd.postValue(null)
            }

        })
    }
}