package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeStoryTagDetail
import com.daqsoft.provider.network.home.HomeRepository
import java.lang.Exception


/**
 * @des 活动详情的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 */
class StoryTagDetailActivityViewModel : BaseViewModel() {

    /**
     * 热门
     */
    val TYPEHOT = "likeNumAndCommentNumAndShowNum"

    /**
     * 故事标签详情
     */
    val storyTagDetail by lazy { MutableLiveData<HomeStoryTagDetail>() }

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     *
     */
    val coverList by lazy { MutableLiveData<HomeStoryBean>() }

    var tagId = ""

    /**
     * 获取标签详情
     */
    fun getHotStoryTagDetail() {
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryTagDetail(tagId)
            .excute(object : BaseObserver<HomeStoryTagDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagDetail>) {
                    storyTagDetail.postValue(response.data)
                }
            })
    }

    /**
     * 获取数据列表
     */
    fun getHotStoryList(orderType: String) {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        //  pageSize
        param["pageSize"] = "40"

        param["tagId"] = tagId

        if (orderType != "") {
            // 默认按发布时间降序排列 likeNum:按点赞数量降序排序 likeNumAndCommentNum:按点赞+评论降序排列 likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
            param["orderType"] = orderType
        }
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取封面数据列表
     */
    fun getCoverList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        param["listCover"] = "1"
        //  pageSize
        param["pageSize"] = "20"
        //标签Id
        param["tagId"] = tagId
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryCover(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    try {
                        var obj = response.data
                        coverList.postValue(obj)
                    } catch (e: Exception) {
                        coverList.postValue(null)
                    }
                }
            })
    }
}