package com.daqsoft.legacyModule.smriti.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.legacyModule.smriti.CONTENT_TYPE_HERITAGE_ITEM
import com.daqsoft.legacyModule.smriti.bean.LegacyBaseDetailBean
import com.daqsoft.legacyModule.smriti.bean.LegacyDetailBean
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleBean
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleDetailBean
import com.daqsoft.legacyModule.smriti.net.LegacySmritiRepository
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean

/**
 * desc :非遗项目详情
 * @author 江云仙
 * @date 2020/4/24 11:34
 */
class LegacyExperienceBaseDetailViewModel :BaseViewModel(){
    var id=""
    /**
     * 非遗体验基地详情
     */
    val legacyDetails = MutableLiveData<LegacyBaseDetailBean>()

    /**
    * 非遗传承人id
    */
    var heritageItemId =""
    var type:String =""

    /**
     * 非遗体验基地详情
     */
    fun getLegacyExperienceBaseDetail() {
        val map = HashMap<String, Any>()
        map["id"]=id
        LegacySmritiRepository.service.getLegacyExperienceBaseDetail(id).excute(object : BaseObserver<LegacyBaseDetailBean>() {
            override fun onSuccess(response: BaseResponse<LegacyBaseDetailBean>) {
                legacyDetails.postValue(response.data)
                getLegacyItem(response.data?.heritageItemId!!)
            }
            override fun onFailed(response: BaseResponse<LegacyBaseDetailBean>) {
                mError.postValue(response)
            }
        })
    }


    /**
     * 非遗传习基地详情
     */
    fun getLegacyTeachingBaseDetail() {
        val map = HashMap<String, Any>()
        map["id"]=id
        LegacySmritiRepository.service.getLegacyTeachingBaseDetail(id).excute(object : BaseObserver<LegacyBaseDetailBean>() {
            override fun onSuccess(response: BaseResponse<LegacyBaseDetailBean>) {
                legacyDetails.postValue(response.data)
                getLegacyItem(response.data?.heritageItemId!!)
            }
            override fun onFailed(response: BaseResponse<LegacyBaseDetailBean>) {
                mError.postValue(response)
            }
        })
    }

    /**
     * 非遗项目列表
     */
    val legacyItems = MutableLiveData<MutableList<LegacyHeritageItemListBean>>()

    /**
     * 获取非遗项目
     */
    fun getLegacyItem(heritageItemId:String){
        LegacyRepository.service.getHeritageItemList(ids = heritageItemId).excute(object :BaseObserver<LegacyHeritageItemListBean>(){
            override fun onSuccess(response: BaseResponse<LegacyHeritageItemListBean>) {
                legacyItems.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LegacyHeritageItemListBean>) {
                mError.postValue(response)
            }

        })
    }



    val detailStoryList = MutableLiveData<BaseResponse<LegacyStoryListBean>>()

    /**
     * 获取故事列表
     */
    fun getDetailStoryList() {
        LegacyRepository.service.getDetailStoryList(ichTermId = id)
            .excute(object : BaseObserver<LegacyStoryListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    detailStoryList.postValue(response)
                }
            })
    }

    /**
     * 获取评论列表
     */
    var commentBeans = MutableLiveData<BaseResponse<CommentBean>>()

    /**
     * 获取评论列表
     */
    fun getActivityCommentList() {
        val param = HashMap<String, String>()
        param["resourceType"] = type
        param["resourceId"] = id
        param["pageSize"] = 3.toString()
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response)
                }
            })
    }


    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()

    /**
     * 点赞
     */
    fun addLike(){
        CommentRepository.service.postThumbUp(id,type).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.postThumbCancel(id,type).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posClloection(id,type).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posCollectionCancel(id,type).excute(object :BaseObserver<Any>(){
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


    /**
     * 非遗故事 liveData
     */
    var intangibleHeritageStoryLiveData = MutableLiveData<BaseResponse<HomeStoryBean>>()
    /**
     * 获取非遗故事列表
     */
    fun getIntangibleHeritageStory(type:String) {
        LegacyRepository.service.getIntangibleHeritageStory(
            resourceId = id,resourceType = type
        )
            .excute(object : BaseObserver<HomeStoryBean>() {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    intangibleHeritageStoryLiveData.postValue(response)
                }
            })
    }
}