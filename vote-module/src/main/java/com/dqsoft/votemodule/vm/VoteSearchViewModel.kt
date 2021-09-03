package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VoteResultBean
import com.daqsoft.provider.bean.VoteTypeBean
import com.daqsoft.provider.bean.VoteWorkBean
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   VoteSearchViewModel
 * @Author      luoyi
 * @Time        2020/11/10 13:41
 */
class VoteSearchViewModel : BaseViewModel() {

    var voteTypeLd: MutableLiveData<MutableList<VoteTypeBean>> = MutableLiveData()

    /**
     * 投票结果
     */
    var voteWorkLd: MutableLiveData<VoteResultBean> = MutableLiveData()

    /**
     * 小分类
     */
    var typeChild: String? = ""

    /**
     * 大分类
     */
    var type: String? = ""

    /**
     * 搜索关键字
     */
    var name: String? = ""
    var currPage: Int = 1

    /**
     * 作品列表
     */
    var voteWorksLd: MutableLiveData<BaseResponse<VoteWorkBean>> = MutableLiveData()
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


    fun getVoteWorkList(voteId: String) {

        var params: HashMap<String, String> = hashMapOf()
        params["orderMode"] = "0"
        params["voteId"] = voteId
        if (!typeChild.isNullOrEmpty() && typeChild != "-1") {
            params["typeChild"] = typeChild!!
        }
        if (!type.isNullOrEmpty() && type != "-1") {
            params["type"] = type!!
        }

        if (!name.isNullOrEmpty()) {
            params["name"] = name!!
        }

        params["currPage"] = currPage.toString()
        params["pageSize"] = "10"

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
                voteWorkLd.postValue(null)
                if(response.message.toString().contains("登录")){
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
                }
            }

        })
    }
}