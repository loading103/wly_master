package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class TimeFragmentViewModel : BaseViewModel() {
    /**
     * 热门
     */
    val TYPEHOT = "likeNumAndCommentNumAndShowNum"

    /**
     * 攻略列表
     */
    val strategyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     * 话题列表
     */
    val topicList by lazy { MutableLiveData<MutableList<HomeTopicBean>>() }

    /**
     * 故事标签列表
     */
    val storyTagList by lazy { MutableLiveData<MutableList<HomeStoryTagBean>>() }

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     *
     */
    val coverList by lazy { MutableLiveData<HomeStoryBean>() }

    /**
     * 标签id
     */
    var tagId: String? = ""

    var tagName: String? = ""

    /**
     * 获取话题列表
     */
    fun getTopicList() {
        mPresenter.value?.loading = true
        HomeRepository.service.getTopicList("5")
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    topicList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<HomeTopicBean>) {
                    topicList.postValue(null)
                }
            })
    }

    /**
     * 获取故事标签列表
     */
    fun getHotStoryTagList() {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
//        param["top"] = "1"
        // 最小故事数量
        param["minStoryNum"] = "1"
        //  size 查询数量 【选填】默认查询全部
        param["size"] = "6"
        mPresenter.value?.loading = true
        HomeRepository.service.getHotStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    storyTagList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取攻略数据列表
     */
    fun getStrategyList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        param["storyType"] = "strategy"
        //  pageSize
        param["pageSize"] = "20"

        // 默认按发布时间降序排列 likeNum:按点赞数量降序排序 likeNumAndCommentNum:按点赞+评论降序排列 likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
        param["orderType"] = "likeNumAndCommentNumAndShowNum"

        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    strategyList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取数据列表
     */
    fun getHotStoryList(currPage: Int) {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        // pageSize
        param["pageSize"] = "10"
        param["currPage"] = currPage.toString()
        if (!tagId.isNullOrEmpty()) {
            param["tagId"] = tagId!!
        }
        param["oderType"] = "createDateDesc"
        if (BaseApplication.appArea == "xj") {
            if (!tagName.isNullOrEmpty()) {
                param["priorityTagName"] = tagName!!
            }
        }
        mPresenter.value?.loading = false
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(null)
                }
            })
    }

    /**
     * 获取封面数据列表
     */
    fun getCoverList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        param["homeCover"] = "1"
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryCover(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    try {
                        var obj = response.data
                        coverList.postValue(obj)
                    } catch (e: Exception) {
                    }
                }
            })
    }
}