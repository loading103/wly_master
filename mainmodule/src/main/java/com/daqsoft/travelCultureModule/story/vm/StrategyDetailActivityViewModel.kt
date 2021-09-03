package com.daqsoft.travelCultureModule.story.vm

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.ThumbBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.NoPassMsgBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @des 故事详情的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 *
 */
class StrategyDetailActivityViewModel : BaseViewModel() {
    /**
     * 故事详情
     */
    val storyDetail by lazy { MutableLiveData<HomeStoryBean>() }

    val locationIntroduce by lazy { ObservableField<String>() }

    /**
     * 点赞列表
     */
    val thumbList by lazy { MutableLiveData<MutableList<ThumbBean>>() }

    /**
     * 获取评论列表
     */
    val commentBeans by lazy { MutableLiveData<MutableList<CommentBean>>() }

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     * 获取检测不通过原因
     */
    val noPassMsgBean by lazy { MutableLiveData<NoPassMsgBean>() }

    val dismissPop by lazy { MutableLiveData<Boolean>() }

    /**
     * 操作结束
     */
    val dofinish by lazy { MutableLiveData<Boolean>() }

    /**
     * 点赞
     */
    val like by lazy { MutableLiveData<Boolean>() }

    /**
     * 取消点赞
     */
    val cancelLike by lazy { MutableLiveData<Boolean>() }

    /**
     * 收藏
     */
    val collect by lazy { MutableLiveData<Boolean>() }

    /**
     * 取消收藏
     */
    val cancelCollect by lazy { MutableLiveData<Boolean>() }

    /**
     * 删除
     */
    val deleteFinish by lazy { MutableLiveData<Boolean>() }

    /**
     * 获取故事详情
     */
    fun getHotStoryTagDetail(id: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    var homeStoryTagBean = response.data
                    storyDetail.postValue(homeStoryTagBean)
//                    locationIntroduce.set(DividerTextUtils.convertDotString(StringBuilder(),response.data.))
                }
            })
    }

    /**
     * 获取我的故事详情
     */
    fun getMineStoryDetail(id: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getMineStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    var homeStoryTagBean = response.data
                    storyDetail.postValue(homeStoryTagBean)
//                   locationIntroduce.set(DividerTextUtils.convertDotString(StringBuilder(),response.data.))
                }
            })
    }

    /**
     * 获取故事审核不通过原因
     */
    fun getNoPassMsg(id: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getNoPassMsg(id)
            .excute(object : BaseObserver<NoPassMsgBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<NoPassMsgBean>) {
                    if (response != null && response.code == 0 && response.data != null) {
                        noPassMsgBean.postValue(response.data)
                    }
                }

            })
    }

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

    /**
     * 获取数据列表
     */
    fun getHotStoryList(id: String) {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        //  pageSize
        param["pageSize"] = "4"
        param["tagId"] = "0"
        param["ignoreId"] = id
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }

    /**
     * 点赞
     */
    fun addLike(id: String) {
        CommentRepository.service.postThumbUp(id, ResourceType.CONTENT_TYPE_STORY)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("感谢您的点赞")
                        like.postValue(true)
                    } else {
                        toast.postValue(response.message)
                    }
                    dofinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    toast.postValue("点赞失败!")
                }

            })
    }

    /**
     * 取消点赞
     */
    fun cancelLike(id: String) {
        CommentRepository.service.postThumbCancel(id, ResourceType.CONTENT_TYPE_STORY)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("成功取消")
                        cancelLike.postValue(true)
                    } else {
                        toast.postValue(response.message)
                    }
                    dofinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    toast.postValue("取消点赞失败")
                }
            })
    }

    /**
     * 收藏
     */
    fun collect(id: String) {
        CommentRepository.service.posClloection(id, ResourceType.CONTENT_TYPE_STORY)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("感谢您的收藏")
                        collect.postValue(true)
                    } else {
                        toast.postValue(response.message)
                    }
                    dofinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    toast.postValue("收藏失败")
                }
            })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(id: String) {
        CommentRepository.service.posCollectionCancel(id, ResourceType.CONTENT_TYPE_STORY)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("成功取消")
                        cancelCollect.postValue(true)
                    } else {
                        toast.postValue(response.message)
                    }
                    dofinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    toast.postValue("取消收藏失败")
                }
            })
    }

    /**
     * 发表评论
     * @param id 评论对象的id
     * @param type 评论对象的type
     * @param content 评论内容
     */
    fun publishComment(id: String, content: String) {
        CommentRepository.service.postAddComment(id, ResourceType.CONTENT_TYPE_STORY, content)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    Log.d("Comment", "res${response}")
                    ToastUtils.showMessage(response.message)
                    if (response.code == 0) {
                        dismissPop.postValue(true)
                    }
                }
            })
    }

    /**
     * 删除故事
     */
    fun deleteStory(id: String) {
        val param = HashMap<String, String>()
        param["id"] = id
        MainRepository.service.postDeleteStory(param).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                if (response.code == 0) {
                    deleteFinish.postValue(true)
                } else {
                    deleteFinish.postValue(false)
                }
            }

            override fun onError(e: Throwable) {
                deleteFinish.postValue(false)
            }

        })
    }
}