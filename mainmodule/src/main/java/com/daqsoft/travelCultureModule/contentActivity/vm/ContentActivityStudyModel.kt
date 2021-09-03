package com.daqsoft.travelCultureModule.contentActivity.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.contentActivity.bean.ContentInfoBean
import com.daqsoft.travelCultureModule.net.MainRepository

class ContentActivityStudyModel : BaseViewModel() {


    //获取咨询列表
    var zixunList = MutableLiveData<BaseResponse<ClubZixunBean>>()


    fun getZixunList(linksResourceId: String, linksResourceType: String,  channelCode: String,pageSize: String, currPage: String) {
         MainRepository.service.getStudyAllContent(linksResourceType=linksResourceType, linksResourceId=linksResourceId,channelCode =channelCode,currPage = currPage,pageSize = pageSize)
            .excute(object : BaseObserver<ClubZixunBean>() {
                override fun onSuccess(response: BaseResponse<ClubZixunBean>) {
                    zixunList?.postValue(response)
                }

                override fun onFailed(response: BaseResponse<ClubZixunBean>) {
                    super.onFailed(response)
                }
            })
    }

    //获取咨询详情
    var zixunInfo = MutableLiveData<ContentInfoBean>()

    fun getZixunInfo(id: String) {
        mPresenter?.value?.loading=false
        MainRepository.service.getZixunInfo(id).excute(object : BaseObserver<ContentInfoBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ContentInfoBean>) {
                zixunInfo.postValue(response.data)
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

    fun post_comment(resourceId: String, resourceType: String, content: String) {
        CommentRepository.service.postAddComment(resourceId, resourceType, content).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
                message_comment.postValue(response.message)
            }

        })
    }

    //获取地区
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    fun getChildRegions() {
        MainRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }

}