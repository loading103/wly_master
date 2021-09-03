package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.ReplyBean
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * @Description
 * @ClassName   ProviderAddEmoticonsLsViewModel
 * @Author      luoyi
 * @Time        2020/11/2 10:52
 */
class ProviderAddCommentLsViewModel : BaseViewModel() {

    var currPage: Int = 1
    var pageSize: Int = 10

    var emoticonsLd: MutableLiveData<BaseResponse<EmoticonsBean>> = MutableLiveData()
    var dismissPop = MutableLiveData<Boolean>()
    fun getEmoticons() {
        mPresenter?.value?.loading = false
        CommentRepository.service.getEmotList(currPage, pageSize).excute(object :
            BaseObserver<EmoticonsBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(response)
            }

            override fun onFailed(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(response)
            }

        })


    }

    /**
     * 发表评论
     * @param id 评论对象的id
     * @param type 评论对象的type
     * @param content 评论内容
     */
    fun publishComment(id: String, resourceType: String, content: String, emoticonsId: String?) {
        CommentRepository.service.postAddComment(
            id,
            resourceType,
            content,
            emoticonsId
        )
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        ToastUtils.showMessage(response.message)
                        dismissPop.postValue(true)
                    }
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    dismissPop.postValue(false)
                }
            })
    }

    /**
     * 发表回复
     */
    fun publishReplyComment(id: String, pid: String, content: String, emoticonsId: ArrayList<String>) {

        val params = HashMap<String, Any>()
        if (!id.isNullOrEmpty()) {
            params["commentId"] = id
        }
        if (!pid.isNullOrEmpty()) {
            params["pid"] = pid
        }
        if (!content.isNullOrEmpty()) {
            params["replyContent"] = content
        }
        if (!emoticonsId.isNullOrEmpty()) {
            params["emoticonsIds"] = emoticonsId
        }
        val param = Gson().toJson(params)
        mPresenter?.value?.loading = true
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param)
        CommentRepository.service
            .postReplyComment(body)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        dismissPop.postValue(true)
                    }
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    dismissPop.postValue(false)
                }
            })
    }


    /**
     * 发表回复
     */
    val pageManager: PageManager by lazy { PageManager(10) }
    val dataList = MutableLiveData<MutableList<ReplyBean>>()
    var totleNumber:Int=0
    fun getReplyList(commentId: String) {
        CommentRepository.service
            .getReplyList(commentId,pageManager.pageIndex.toString(), pageManager.pageSize.toString())
            .excute(object : BaseObserver<ReplyBean>() {
                override fun onSuccess(response: BaseResponse<ReplyBean>) {
                    if (response.code == 0) {
                        dataList.postValue(response.datas)
                        totleNumber= response.page?.total ?: 0

                    }
                }

                override fun onFailed(response: BaseResponse<ReplyBean>) {
                    dataList.postValue(null)
                }
            })
    }
}