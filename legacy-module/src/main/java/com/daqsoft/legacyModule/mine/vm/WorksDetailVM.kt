package com.daqsoft.legacyModule.mine.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.ThumbBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 *@package:com.daqsoft.legacyModule.mine.vm
 *@date:2020/5/19 15:46
 *@author: caihj
 *@des:TODO
 **/
class WorksDetailVM:BaseViewModel() {
    /**
     * 作品详情详情
     */
    var worksDetail = MutableLiveData<HomeStoryBean>()
    var id = ""
    /**
     * 获取我的作品详情
     */
    fun getMineWorkDetail(id: String) {
        HomeRepository.service.getMineStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    var homeStoryTagBean = response.data
                    worksDetail.postValue(homeStoryTagBean)
                }
            })
    }

    /**
     * 获取非遗传承人作品详情
     */
    fun getPeopleWorksDetail(id: String) {
        HomeRepository.service.getStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    var homeStoryTagBean = response.data
                    worksDetail.postValue(homeStoryTagBean)
                }
            })
    }
    /**
     * 点赞列表
     */
    var thumbList = MutableLiveData<MutableList<ThumbBean>>()
    /**
     * 获取点赞列表
     */
    fun getThumbList(resourceId: String, resourceType: String) {
        HomeRepository.service.getThumbList(resourceId, resourceType)
            .excute(object : BaseObserver<ThumbBean>() {
                override fun onSuccess(response: BaseResponse<ThumbBean>) {
                    thumbList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取评论列表
     */
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()

    /**
     * 获取评论列表
     */
    fun getActivityCommentList(id: String) {

        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.CONTENT_TYPE_STORY
        param["resourceId"] = id
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response.datas)
                }
            })
    }

    var pkWorks = MutableLiveData<MutableList<LegacyStoryListBean>>()
    var pageBean = MutableLiveData<BaseResponse.PageBean>()
    fun getPkWorks(id:String){
        LegacyRepository.service.getPKWorksList(id).excute(object :BaseObserver<LegacyStoryListBean>(){
            override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                pkWorks.postValue(response.datas)
                pageBean.postValue(response.page)
            }

        })
    }
    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()

    /**
     * 点赞
     */
    fun addLike(){
        CommentRepository.service.postThumbUp(id,ResourceType.CONTENT_TYPE_STORY).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的点赞")
                    likeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("点赞失败!")
            }

        })
    }

    /**
     * 取消点赞
     */
    fun cancelLike(){
        CommentRepository.service.postThumbCancel(id,ResourceType.CONTENT_TYPE_STORY).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelLikeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消点赞失败")
            }
        })
    }

    var collectStatus = MutableLiveData<Boolean>()
    var cancelCollectStatus = MutableLiveData<Boolean>()

    /**
     * 收藏
     */
    fun collect(){
        CommentRepository.service.posClloection(id,ResourceType.CONTENT_TYPE_STORY).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的收藏")
                    collectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("收藏失败")
            }
        })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(){
        CommentRepository.service.posCollectionCancel(id,ResourceType.CONTENT_TYPE_STORY).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelCollectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消收藏失败")
            }
        })
    }

    var focusStatus = MutableLiveData<Boolean>()
    var cancelFocusStatus = MutableLiveData<Boolean>()


    /**
     * 关注
     */
    fun focusHeritagePeople(){
        CommentRepository.service.attentionResource(id,ResourceType.CONTENT_TYPE_STORY).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    ToastUtils.showMessage("关注成功!")
                    focusStatus.postValue(true)
                }else{
                    ToastUtils.showMessage(response.message)
                }
            }
        })
    }
    /**
     * 取消关注
     */
    fun focusCancelHeritagePeople(){
        CommentRepository.service.attentionResourceCancle(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    ToastUtils.showMessage("取消成功!")
                    cancelFocusStatus.postValue(true)
                }else{
                    ToastUtils.showMessage(response.message)
                }
            }
        })
    }
}