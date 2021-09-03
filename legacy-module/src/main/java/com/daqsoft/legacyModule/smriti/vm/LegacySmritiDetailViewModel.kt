package com.daqsoft.legacyModule.smriti.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.legacyModule.smriti.bean.LegacyDetailBean
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleBean
import com.daqsoft.legacyModule.smriti.net.LegacySmritiRepository
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.LegacyProducts
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut

/**
 * desc :非遗项目详情
 * @author 江云仙
 * @date 2020/4/24 11:34
 */
class LegacySmritiDetailViewModel :BaseViewModel(){
    var id=""
    /**
     * 非遗项目列表数据
     */
    val legacyDetails = MutableLiveData<LegacyDetailBean>()
    /**
     * 非遗传承人列表数据
     */
    val legacyPeopleBean = MutableLiveData<MutableList<LegacyHeritagePeopleListBean>>()
    /**
    * 非遗传承人id
    */
    var heritageItemId =""
    /**
     * 非遗项目详情
     */
    fun getLegacyDetail() {
        val map = HashMap<String, Any>()
        map["id"]=id
        mPresenter.value?.isNeedRecyleView = false
        mPresenter.value?.loading = false
        LegacySmritiRepository.service.getLegacyDetail(map).excute(object : BaseObserver<LegacyDetailBean>() {
            override fun onSuccess(response: BaseResponse<LegacyDetailBean>) {
                legacyDetails.postValue(response.data)
            }
            override fun onFailed(response: BaseResponse<LegacyDetailBean>) {
                mError.postValue(response)
            }
        })
    }
    /**
     * 非遗传承人
     */
    fun getLegacyPeople() {
        LegacyRepository.service.getHeritagePeopleList(heritageItemId = heritageItemId)
            .excute(object : BaseObserver<LegacyHeritagePeopleListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritagePeopleListBean>) {
                    legacyPeopleBean.postValue(response.datas)
                }
                override fun onFailed(response: BaseResponse<LegacyHeritagePeopleListBean>) {
                    mError.postValue(response)
                }
            })
    }



    val detailStoryList = MutableLiveData<List<LegacyStoryListBean>>()
    val storyPageBean = MutableLiveData<BaseResponse.PageBean>()
    /**
     * 获取故事列表
     */
    fun getDetailStoryList() {
        LegacyRepository.service.getDetailStoryList(ichTermId = heritageItemId)
            .excute(object : BaseObserver<LegacyStoryListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    detailStoryList.postValue(response.datas)
                    storyPageBean.postValue(response.page)
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
        param["resourceType"] = ResourceType.CONTENT_TYPE_HERITAGE_ITEM
        param["resourceId"] = heritageItemId
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
        CommentRepository.service.postThumbUp(heritageItemId,ResourceType.CONTENT_TYPE_HERITAGE_ITEM).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.postThumbCancel(heritageItemId,ResourceType.CONTENT_TYPE_HERITAGE_ITEM).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posClloection(heritageItemId,ResourceType.CONTENT_TYPE_HERITAGE_ITEM).excute(object :BaseObserver<Any>(){
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
        CommentRepository.service.posCollectionCancel(heritageItemId,ResourceType.CONTENT_TYPE_HERITAGE_ITEM).excute(object :BaseObserver<Any>(){
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
     * 非遗商品
     */
    val productBean = MutableLiveData<LegacyProducts>()
    var sysCode =  SPUtils.getInstance().getString(SPKey.SHOP_CODE, "")
    fun getProductList(resourceCode:String,page: Int = 1,pageSize:Int = 10){
        ElectronicRepository.electronicService.getLegacyProductLs(resourceCode = resourceCode,sysCode = sysCode,pageNum = page,pageSize = pageSize)
            .excut(object : ElectronicObserver<LegacyProducts>() {
                override fun onSuccess(response: BaseResponse<LegacyProducts>) {
                    productBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<LegacyProducts>) {
                    mError.postValue(response)
                }

            })
    }

}