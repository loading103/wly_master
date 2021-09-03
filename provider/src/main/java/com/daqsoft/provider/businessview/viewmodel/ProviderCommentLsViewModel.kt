package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.CommentTagsBean
import com.daqsoft.provider.network.comment.beans.CommtentTagBean

/**
 * @Description 评论列表
 * @ClassName   ProviderCommentLsViewModel
 * @Author      luoyi
 * @Time        2020/4/13 11:01
 */
class ProviderCommentLsViewModel : BaseViewModel() {

    val pageSize: Int = 10
    var pageIndex: Int = 1

    /**
     * 便签分组Id
     */
    var commentTagId: String = ""

    /**
     * 资源类型
     */
    var resourceType: String = ""

    /**
     *  评论等级
     */
    var commentLevel: String = ""

    /**
     * 资源id
     */
    var resourceId: String = ""

    /**
     * 评论列表
     */
    val commentBeans = MutableLiveData<MutableList<CommentBean>>()
    var totalNum = MutableLiveData<Int>()

    /**
     * 评论便签
     */
    val commentTagsBeans = MutableLiveData<HashMap<String, CommentTagsBean>>()

    /**
     * 评论便签
     */
    val commentTagsBeans2 = MutableLiveData<MutableList<CommtentTagBean>>()

    /**
     * 根据分组，获取评论便签
     */
    fun getCommentTagCountInfo(resourceId: String, resourceType: String) {
        CommentRepository.service.getCommentTagCountInfo(resourceType, resourceId, true)
            .excute(object : BaseObserver<HashMap<String, CommentTagsBean>>() {
                override fun onSuccess(response: BaseResponse<HashMap<String, CommentTagsBean>>) {
                    commentTagsBeans.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HashMap<String, CommentTagsBean>>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取评论列表
     */
    fun getCommentLs() {
        mPresenter?.value?.loading = true
//        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = resourceType
        param["resourceId"] = resourceId
        param["pageSize"] = pageSize.toString()
        param["currPage"] = pageIndex.toString()
        if (!commentLevel.isNullOrEmpty()) {
            param["commentLevel"] = commentLevel
        }
        if (!commentTagId.isNullOrEmpty()) {
            param["commentTagId"] = commentTagId
        }
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
                if (commentTagId.isNullOrEmpty())
                    totalNum.postValue(response.page?.total ?: 0)
            }

            override fun onFailed(response: BaseResponse<CommentBean>) {
                mError.postValue(response)
            }
        })
    }

    /**
     * 根据分组，获取评论便签
     */
    fun getCommentTagCountInfo2(resourceId: String, resourceType: String) {
        CommentRepository.service.getCommentTag(resourceType, resourceId)
            .excute(object : BaseObserver<CommtentTagBean>() {
                override fun onSuccess(response: BaseResponse<CommtentTagBean>) {
                    commentTagsBeans2.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CommtentTagBean>) {
                    mError.postValue(response)
                }
            })
    }


    /**
     * 获取平均评论星级
     */
    var totalStartNum = MutableLiveData<String>()

    fun getCommNumber(resourceId: String, resourceType: String) {
        CommentRepository.service.getCommentStart(resourceType, resourceId)
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    totalStartNum.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<String>) {
                    totalStartNum.postValue(response.data)
                }
            })
    }
}