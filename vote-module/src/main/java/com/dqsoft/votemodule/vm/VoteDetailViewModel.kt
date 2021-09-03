package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   VoteDetailModel
 * @Author      luoyi
 * @Time        2020/11/9 11:21
 */
class VoteDetailViewModel : BaseViewModel() {


    var currPage: Int = 1

    var voteDetailLd: MutableLiveData<VoteDetailBean> = MutableLiveData()


    var cutDownTimeLd: MutableLiveData<VoteDetailBean> = MutableLiveData()

    /**
     * 投票类型
     */
    var voteTypeLd: MutableLiveData<MutableList<VoteTypeBean>> = MutableLiveData()

    /**
     * 作品列表
     */
    var voteWorksLd: MutableLiveData<BaseResponse<VoteWorkBean>> = MutableLiveData()

    /**
     * 投票结果
     */
    var voteWorkLd: MutableLiveData<VoteResultBean> = MutableLiveData()


    /**
     * 大分类
     */
    var type: String? = ""

    /**
     * 小分类
     */
    var typeChild: String? = ""

    fun getVoteDetail(voteId: String) {

        VoteRespository.service.getVoteDetail(voteId).excute(object : BaseObserver<VoteDetailBean>() {
            override fun onSuccess(response: BaseResponse<VoteDetailBean>) {
                voteDetailLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VoteDetailBean>) {
                voteDetailLd.postValue(null)
            }

        })

    }

    fun refreshVoteDetailTime(voteId: String) {

        VoteRespository.service.getVoteDetail(voteId).excute(object : BaseObserver<VoteDetailBean>() {
            override fun onSuccess(response: BaseResponse<VoteDetailBean>) {
                cutDownTimeLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VoteDetailBean>) {
                cutDownTimeLd.postValue(null)
            }

        })

    }

    fun getVoteTypes(voteId: String) {

        VoteRespository.service.getVoteTypeList(voteId, 1)
            .excute(object : BaseObserver<VoteTypeBean>() {
                override fun onSuccess(response: BaseResponse<VoteTypeBean>) {
                    voteTypeLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VoteTypeBean>) {
                    voteTypeLd.postValue(null)
                }

            })
    }

    fun getVoteWorkList(voteId: String, orderMode: String = "0") {
        var params: HashMap<String, String> = hashMapOf()
        params["pageSize"] = "10"
        params["currPage"] = currPage.toString()
        params["voteId"] = voteId
        params["orderMode"] = orderMode
        if (!type.isNullOrEmpty()) {
            params["type"] = type!!
        }
        if (!typeChild.isNullOrEmpty()) {
            params["typeChild"] = typeChild!!
        }
        VoteRespository.service.searchVoteWorkList(params)
            .excute(object : BaseObserver<VoteWorkBean>() {
                override fun onSuccess(response: BaseResponse<VoteWorkBean>) {
                    voteWorksLd.postValue(response)
                }

                override fun onFailed(response: BaseResponse<VoteWorkBean>) {
                    voteWorksLd.postValue(null)
                }

            })
    }

    fun voteWork(proId: String, position: Int) {
        VoteRespository.service.voteWork(proId).excute(object : BaseObserver<VoteResultBean>() {
            override fun onSuccess(response: BaseResponse<VoteResultBean>) {
                if (response.data != null) {
                    response.data!!.position = position
                }
                voteWorkLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VoteResultBean>) {
                if (!response.message.isNullOrEmpty()) {
                    ToastUtils.showMessage(response.message)
                    if(response.message.toString().contains("请先登录")){
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
                    }
                }
                voteWorkLd.postValue(null)
            }

        })
    }
}