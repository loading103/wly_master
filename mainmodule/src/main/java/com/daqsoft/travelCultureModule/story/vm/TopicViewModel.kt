package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository

/**
 * 话题列表页面ViewModel
 * @author caihj
 * @date 2020/4/2 0020
 * @version 1.0.0
 * @since JDK 1.8
 */
class TopicViewModel : BaseViewModel() {
    var name: String = ""
    /**
     * 话题列表
     */
    val topicList by lazy { MutableLiveData<MutableList<HomeTopicBean>>() }

    /**
     * 当前页
     */
    var currPage: Int = 1

    /**
     *  页面大小
     */
    var pageSize: Int = 10

    val canceCollectLiveData by lazy { MutableLiveData<Int>() }

    val collectVenueLiveData by lazy { MutableLiveData<Int>() }

    /**
     * 获取话题列表数据
     */
    fun getTopicList(content: String = "") {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["currPage"] = currPage.toString()
        param["pageSize"] = pageSize.toString()
        if (content.isNotEmpty()) {
            param["name"] = content
        }else{
            if(!name.isNullOrBlank()){
                param["name"] = name
            }
        }

        HomeRepository.service.getTopicList(param)
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    topicList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<HomeTopicBean>) {
                    mError.postValue(response)
                }
            })
    }


    /**
     * 收藏
     */
    fun collect(id: String, position: Int) {
        CommentRepository.service.posClloection(id, ResourceType.CONTENT_TYPE_TOPIC).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                collectVenueLiveData.postValue(position)
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
    fun canceCollect(id: String, position: Int) {
        CommentRepository.service.posCollectionCancel(id, ResourceType.CONTENT_TYPE_TOPIC).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                canceCollectLiveData?.postValue(position)
                toast.postValue("取消收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("取消收藏，请稍后再试~")
            }
        })
    }
}