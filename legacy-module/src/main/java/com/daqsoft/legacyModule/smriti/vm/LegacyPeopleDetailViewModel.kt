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
class LegacyPeopleDetailViewModel :BaseViewModel(){
    var id=""
    /**
     * 非遗项传承人详情
     */
    val legacyDetails = MutableLiveData<LegacyPeopleDetailBean>()

    /**
     * 非遗项目详情
     */
    fun getLegacyPeopleDetail() {
        val map = HashMap<String, Any>()
        map["id"]=id
        LegacySmritiRepository.service.getLegacyPeopleDetail(map).excute(object : BaseObserver<LegacyPeopleDetailBean>() {
            override fun onSuccess(response: BaseResponse<LegacyPeopleDetailBean>) {
                legacyDetails.postValue(response.data)
                response.data?.phone?.let { getDetailStoryList(it) }
                val heritageItemId = response.data?.heritageItem
                if (heritageItemId != null) {
                    getLegacyItem(heritageItemId)
                }
            }
            override fun onFailed(response: BaseResponse<LegacyPeopleDetailBean>) {
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



    val detailStoryList = MutableLiveData<List<LegacyStoryListBean>>()

    /**
     * 获取故事列表
     */
    fun getDetailStoryList(phone:String) {
        val param = HashMap<String, String>()
        param["ichTermId"] = id
        param["ichHpPhone"] = phone
        LegacyRepository.service.getLegacyWorks(param)
            .excute(object : BaseObserver<LegacyStoryListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    detailStoryList.postValue(response.datas)
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
        param["resourceType"] = ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE
        param["resourceId"] = id
        param["pageSize"] = 3.toString()
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response)
                }
            })
    }
    /**
     * 操作结束
     */
    var dofinish = MutableLiveData<Boolean>()


    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()

    /**
     * 点赞
     */
    fun addLike(){
        CommentRepository.service.postThumbUp(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.postThumbCancel(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posClloection(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posCollectionCancel(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.attentionResource(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(mPresenter){
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