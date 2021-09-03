package com.dqsoft.votemodule.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.VipInfoBean
import com.daqsoft.provider.bean.VoteDetailBean
import com.daqsoft.provider.bean.VoteTypeBean
import com.daqsoft.provider.bean.VoteWorkDetailBean
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.provider.network.vote.VoteRespository

/**
 * @Description
 * @ClassName   VoteInPartViewModel
 * @Author      luoyi
 * @Time        2020/11/10 13:40
 */
class VoteInPartViewModel : BaseViewModel() {

    var saveVoteLd: MutableLiveData<Boolean> = MutableLiveData()

    var voteTypeLd: MutableLiveData<MutableList<VoteTypeBean>> = MutableLiveData()

    var voteDetailLd: MutableLiveData<VoteDetailBean> = MutableLiveData()

    /**
     * 实名认证信息
     */
    val vipInfold = MutableLiveData<VipInfoBean>()

    var voteWorkDetailLd: MutableLiveData<VoteWorkDetailBean> = MutableLiveData()

    /**
     * 保存作品信息
     */
    fun saveVoteInPart(
        idCard: String?, typeId: String?, video: String?, voteId: String, name: String?, intro: String?
        , typeChild: String?, author: String?, phone: String?, images: String?, proId: String? = ""
    ) {
        var params = HashMap<String, String>()
        if (!idCard.isNullOrEmpty()) {
            params["idCard"] = SM4Util.encryptByEcb(idCard)
        }
        if (!typeId.isNullOrEmpty()) {
            params["type"] = typeId
        }

        if (!video.isNullOrEmpty()) {
            params["video"] = video
        }
        if (!typeChild.isNullOrEmpty()) {
            params["typeChild"] = typeChild
        }
        params["voteId"] = voteId
        if (!name.isNullOrEmpty()) {
            params["name"] = name
        }
        if (!intro.isNullOrEmpty()) {
            params["intro"] = intro!!
        }
        if (!author.isNullOrEmpty()) {
            params["author"] = author
        }
        if (!phone.isNullOrEmpty()) {
            params["phone"] = SM4Util.encryptByEcb(phone)
        }
        if (!images.isNullOrEmpty()) {
            params["images"] = images
        }
        // 编辑模式
        if (!proId.isNullOrEmpty()) {
            params["id"] = proId
        }
        VoteRespository.service.saveVoteWork(params).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                saveVoteLd.postValue(true)
            }

            override fun onFailed(response: BaseResponse<Any>) {
                ToastUtils.showMessage("" + response.message)
                saveVoteLd.postValue(false)
            }

        })
    }

    fun getVoteTypes(voteId: String) {

        VoteRespository.service.getVoteTypeList(voteId, 0)
            .excute(object : BaseObserver<VoteTypeBean>() {
                override fun onSuccess(response: BaseResponse<VoteTypeBean>) {
                    voteTypeLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VoteTypeBean>) {
                    voteTypeLd.postValue(null)
                }

            })
    }


    /**
     * 获取用户信息
     */
    fun getVipInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        VoteRespository.service.getVipInfo().excute(object : BaseObserver<VipInfoBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<VipInfoBean>) {
                if (response.data != null) {
                    if (!response.data!!.idCard.isNullOrEmpty()) {
                        response.data!!.idCard = SM4Util.decryptByEcb(response.data!!.idCard)
                    }
                }
                vipInfold?.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VipInfoBean>) {
                vipInfold?.postValue(null)
            }
        })
    }


    /**
     * 获取作品详情
     */
    fun getVoteWorkDetail(proId: String) {
        mPresenter?.value?.loading = false
        VoteRespository.service.getVoteWorkInfo(proId)
            .excute(object : BaseObserver<VoteWorkDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VoteWorkDetailBean>) {
                    voteWorkDetailLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VoteWorkDetailBean>) {
                    voteWorkDetailLd.postValue(null)
                }
            })
    }


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
}