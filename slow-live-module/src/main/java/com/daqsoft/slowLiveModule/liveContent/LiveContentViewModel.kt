package com.daqsoft.slowLiveModule.liveContent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.slowLiveModule.bean.LiveContentBean
import com.daqsoft.slowLiveModule.bean.LiveContentInfoBean
import com.daqsoft.slowLiveModule.net.LiveRepository

internal class LiveContentViewModel : BaseViewModel() {


    //获取地区
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    fun getChildRegions() {
        LiveRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }

    //获取列表
    val contentList: MutableLiveData<MutableList<LiveContentBean>> = MutableLiveData()

    fun getContentList(
        areaSiteSwitch: String,
        linksResourceId: String, linksResourceType: String, orderType: String,
        pageSize: String, currPage: String, region: String
        , channelCode: String
    ) {
        mPresenter.value?.loading = true
        LiveRepository.service.getContentList(
            areaSiteSwitch,
            linksResourceId,
            linksResourceType,
            orderType,
            pageSize,
            currPage,
            region,
            channelCode
        ).excute(object : BaseObserver<LiveContentBean>(mPresenter, true) {
            override fun onSuccess(response: BaseResponse<LiveContentBean>) {
                contentList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LiveContentBean>) {
                super.onFailed(response)
                mPresenter.value?.error = true
            }


        })
    }


    //获取资讯详情
    var contentDetail = MutableLiveData<LiveContentInfoBean>()

    fun getContentDetail(id: String) {
        LiveRepository.service.getContentDetail(id).excute(object : BaseObserver<LiveContentInfoBean>() {
            override fun onSuccess(response: BaseResponse<LiveContentInfoBean>) {
                contentDetail.postValue(response.data)
            }
        })
    }


    //获取评论列表
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()

    fun getActivityCommentList(id: String, resourceType: String, currPage: Int, pageSize: Int) {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        param["resourceType"] = resourceType
        param["resourceId"] = id
        param["currPage"] = currPage.toString()
        param["pageSize"] = pageSize.toString()

        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
            }
        })
    }

    //点赞
    var like = MutableLiveData<String>()

    fun check_like(resourceId: String, resourceType: String) {
        CommentRepository.service.postThumbUp(resourceId, resourceType).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                like.postValue(response.data.toString())
            }
        })
    }

    //取消点赞
    fun check_dislike(resourceId: String, resourceType: String) {
        CommentRepository.service.postThumbCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
            }

        })
    }

    //收藏
    fun check_colect(resourceId: String, resourceType: String) {
        CommentRepository.service.posClloection(resourceId, resourceType).excute(object
            : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })
    }

    //取消收藏
    fun check_discolect(resourceId: String, resourceType: String) {
        CommentRepository.service.posCollectionCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })
    }

    //发送评论
    var message_comment = MutableLiveData<String>()

//    fun post_comment(resourceId: String, resourceType: String, content: String) {
//        CommentRepository.service.postAddComment(resourceId, resourceType, content).excute(object : BaseObserver<Any>() {
//            override fun onSuccess(response: BaseResponse<Any>) {
//                Log.e("e", "erroe");
//                message_comment.postValue(response.message)
//            }
//
//        })
//    }


}