package com.daqsoft.travelCultureModule.clubActivity.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.ThumbBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubActivityBean
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubInfoBean
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubPersonBean
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.net.MainRepository

class ClubActicityInfoViewModel : BaseViewModel() {
    var clubInfo = MutableLiveData<ClubInfoBean>()

    var thumbList = MutableLiveData<MutableList<ThumbBean>>()
    var zixunList = MutableLiveData<BaseResponse<ClubZixunBean>>()
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()
    var clubPersonList = MutableLiveData<MutableList<ClubPersonBean>>()
    fun getClubInfo(id: Int) {
        mPresenter.value?.loading = true
        MainRepository.service.getClubInfo(id).excute(object : BaseObserver<ClubInfoBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ClubInfoBean>) {
                clubInfo.postValue(response.data)
            }
        })
    }

    var clubActivityList = MutableLiveData<MutableList<ClubActivityBean>>()
    var page_total = MutableLiveData<String>()
    var totalSize =0
    fun getClubActivity(id: String, page_activity: String,pageSize: String) {
        MainRepository.service.getClubActivityList(id, "5", page_activity, pageSize).excute(object
            : BaseObserver<ClubActivityBean>
            (mPresenter) {
            override fun onSuccess(response: BaseResponse<ClubActivityBean>) {
                clubActivityList.postValue(response.datas)
            }

        })

    }

    fun getZanData(resourceId: String, resourceType: String) {
        HomeRepository.service.getThumbList(resourceId, resourceType)
            .excute(object : BaseObserver<ThumbBean>() {
                override fun onSuccess(response: BaseResponse<ThumbBean>) {
                    thumbList.postValue(response.datas)
                    totalSize= response.page?.total!!
                }
            })
    }

    fun getZixunList(
        linksResourceId: String, linksResourceType: String, orderType: String,
        pageSize: String, currPage: String, region: String
    ) {
        MainRepository.service.getClubZixunList(
            linksResourceId, linksResourceType, orderType,
            pageSize, currPage, region, Constant.HOME_CONTENT_TYPE_societyNews
        ).excute(object : BaseObserver<ClubZixunBean>() {
            override fun onSuccess(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(response)
            }

            override fun onFailed(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(null)
            }

        })
    }

    /**
     * 获取评论列表
     */
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

    fun getClubPersonList(id: String, pageSize: String) {
        MainRepository.service.getClubPersonList(id, pageSize).excute(object : BaseObserver<ClubPersonBean>() {
            override fun onSuccess(response: BaseResponse<ClubPersonBean>) {
                clubPersonList.postValue(response.datas)
            }

        })
    }

    //点赞
    var like = MutableLiveData<String>()

    fun check_like(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbUp(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                like.postValue(response.data.toString())
            }
        })
    }

    //取消点赞
    fun check_dislike(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
            }

        })
    }

    //收藏
    fun check_colect(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })
    }

    //取消收藏
    fun check_discolect(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })
    }

//    //发送评论
//    var message_comment = MutableLiveData<String>()
//
//    fun post_comment(resourceId: String, resourceType: String, content: String) {
//        mPresenter.value?.loading = true
//        CommentRepository.service.postAddComment(resourceId, resourceType, content).excute(object : BaseObserver<Any>(mPresenter) {
//            override fun onSuccess(response: BaseResponse<Any>) {
//                Log.e("e", "erroe");
//                message_comment.postValue(response.message)
//            }
//
//        })
//    }

    val subscribeLiveData = MutableLiveData<Boolean>(false)
    //关注资源
    fun attentionResource(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResource(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                subscribeLiveData.value =true
            }

            override fun onFailed(response: BaseResponse<Any>) {
                subscribeLiveData.value = false
            }
        })
    }

    val unsubscribeLiveData = MutableLiveData<Boolean>(false)
    //取消关注资源
    fun attentionResourceCancle(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResourceCancle(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                unsubscribeLiveData.value = true
            }

            override fun onFailed(response: BaseResponse<Any>) {
                unsubscribeLiveData.value = false
            }
        })
    }
}