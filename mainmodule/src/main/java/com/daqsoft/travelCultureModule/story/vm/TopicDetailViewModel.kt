package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.story.bean.TopicBean

/**
 * @des 话题详情的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 */
class TopicDetailViewModel : BaseViewModel() {

    /**
     * 热门
     */
    val TYPEHOT = "likeNumAndCommentNumAndShowNum"

    /**当前页*/
    var currPage = 1

    /**
     * 话题详情
     */
    val topicDetail by lazy { MutableLiveData<TopicBean>() }

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     * 取消收藏
     */
    val cancelCollectLiveData by lazy { MutableLiveData<Boolean>() }

    /**
     * 收藏
     */
    val collectLiveData by lazy { MutableLiveData<Boolean>() }


    /**
     * 获取标签详情
     */
    fun getTopicDetail(id: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getTopicDetail(id)
            .excute(object : BaseObserver<TopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<TopicBean>) {
                    topicDetail.postValue(response.data)
                }
            })
    }

    /**
     * 获取数据列表
     * @param orderType 排序方式
     * 1、likeNum 默认按发布时间降序排列
     * 2、likeNumAndCommentNum 按点赞数量降序排序
     * 3、likeNumAndCommentNumAndShowNum 按点赞+评论降序排列
     *
     * @param storyType 【strategy攻略】 【story故事】
     * @param topicId 话题ID
     */
    fun getHotStoryList(topicId: String, orderType: String, storyType: String = "") {
        val param = HashMap<String, String>()
        if (orderType != "") {
            param["orderType"] = orderType
        }
        param["pageSize"] = "30"
        param["topicId"] = topicId
        param["storyType"] = storyType
        param["currPage"] = currPage.toString()
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param).excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                storyList.postValue(response.datas)
            }
        })
    }


    /**
     * 收藏
     */
    fun collect(id: String) {
        CommentRepository.service.posClloection(id, ResourceType.CONTENT_TYPE_TOPIC).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                collectLiveData.postValue(true)
                toast.postValue("收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("收藏失败，请稍后再试~")
            }
        })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(id: String) {
        CommentRepository.service.posCollectionCancel(id, ResourceType.CONTENT_TYPE_TOPIC).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                cancelCollectLiveData?.postValue(true)
                toast.postValue("取消收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("取消收藏，请稍后再试~")
            }
        })
    }
}