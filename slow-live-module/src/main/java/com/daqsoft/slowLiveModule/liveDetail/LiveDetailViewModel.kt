package com.daqsoft.slowLiveModule.liveDetail

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.slowLiveModule.bean.LiveAroundBean
import com.daqsoft.slowLiveModule.bean.LiveDetailBean
import com.daqsoft.slowLiveModule.net.LiveRepository


/**
 * @des     LiveDetailViewModel
 * @class   LiveDetailViewModel
 * @author  Wongxd
 * @date    2020-4-16  20:18
 *
 */
internal class LiveDetailViewModel : BaseViewModel() {


    val liveDetail: MutableLiveData<LiveDetailBean> = MutableLiveData()

    fun getLiveDetail(scenicSpotsId: Int) {
        mPresenter.value?.loading = true

        LiveRepository.service.getLiveDetail(scenicSpotsId.toString()).excute(object : BaseObserver<LiveDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LiveDetailBean>) {
                mPresenter.value?.loading = false

                liveDetail.postValue(response.data)

                response.data?.let {
                    getLiveAround(scenicSpotsId, it.scenicId)
                    getCommentList(it.scenicId.toString(), ResourceType.CONTENT_TYPE_SCENERY)
                }
            }
        })
    }


    val liveAroundList: MutableLiveData<List<LiveAroundBean>> = MutableLiveData()

    fun getLiveAround(scenicSpotsId: Int, scenicId: Int) {

        LiveRepository.service.getLiveAround(scenicSpotsId.toString(), scenicId.toString()).excute(object : BaseObserver<LiveAroundBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LiveAroundBean>) {
                liveAroundList.postValue(response.datas)
            }
        })
    }


    /**
     * 评论列表
     */
    val commentList = MutableLiveData<MutableList<CommentBean>>()

    /**
     * 获取评论列表
     */
    fun getCommentList(id: String, type: String) {
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.CONTENT_TYPE_SCENERY
        param["resourceId"] = id
        param["pageSize"] = "2"
        mPresenter?.value?.isNeedToastMessage = false
        LiveRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentList.postValue(response.datas)
            }
        })
    }


    /**
     * 收藏状态变更
     */
    var collectLiveData = MutableLiveData<Boolean>()


    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter.value?.loading = true
        LiveRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_SCENERY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue("已收藏~")
                    collectLiveData.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue(response.message)
                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String) {
        mPresenter.value?.loading = true
        LiveRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_SCENERY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue("已取消收藏~")
                    collectLiveData.postValue(false)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue(response.message)
                }
            })
    }


    /**
     * 点赞状态变更
     */
    var thumbLiveData = MutableLiveData<Boolean>()

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
        mPresenter.value?.loading = true
        LiveRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_SCENERY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue("点赞成功~")
                    thumbLiveData.postValue(true)

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue(response.message)
                }
            })
    }

    /**
     * 取消点赞接口
     */
    fun thumbCancel(resourceId: String) {
        mPresenter.value?.loading = true
        LiveRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_SCENERY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    thumbLiveData.postValue(false)
                    toast.postValue("取消点赞成功~")
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mPresenter.value?.loading = false
                    toast.postValue(response.message)
                }
            })
    }


}