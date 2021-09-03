package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.TagBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @des 活动详情的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 */
class MyStoryListActivityViewModel : BaseViewModel() {


    /**
     * 故事类型
     */
    val types = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("故事", "story"),
        ValueKeyBean("攻略", "strategy"),
        ValueKeyBean("草稿", "caogao")
    )

    /**
     * 故事排序
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("浏览量优先", "showNum"),
        ValueKeyBean("评论量优先", "commentNum"),
        ValueKeyBean("点赞量优先", "likeNum")
    )

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     * 个人标签
     */
    val tags by lazy { MutableLiveData<MutableList<TagBean>>() }

    /**
     * 删除状态
     */
    val deleteFinish by lazy { MutableLiveData<Boolean>() }

    /**
     * 当前类型
     */
    var currentType = ""

    /**
     * 当前标签
     */
    var currentTag = ""

    /**
     * 当前排序
     */
    var currentSort = ""

    /**
     * 当前页码
     */
    var currentPage: Int = 1

    /**
     * 删除位置
     */
    var deletePosition: Int = -1

    var deleteStory: HomeStoryBean? = null

    val pageSize = 20

    /**
     * 获取标签
     */
    fun getTagList() {
        val param = HashMap<String, String>()
        HomeRepository.service.getVIPTagList(param).excute(object : BaseObserver<TagBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<TagBean>) {
                tags.postValue(response.datas)
            }
        })
    }

    fun getMyStoryList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        //  pageSize
        param["pageSize"] = "$pageSize"
        param["currPage"] = "$currentPage"
        if (currentType == "caogao") {
            param["status"] = "3"
        } else {
            param["storyType"] = currentType
        }
        param["tagId"] = currentTag

        if (currentSort != "") {
            // 默认按发布时间降序排列 likeNum:按点赞数量降序排序 likeNumAndCommentNum:按点赞+评论降序排列 likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
            param["orderType"] = currentSort
        }
        mPresenter.value?.loading = false
        MainRepository.service.getVipList(param).excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                storyList.postValue(response.datas)
            }
        })
    }

    /**
     * 删除故事
     */
    fun deleteStory() {
        val param = HashMap<String, String>()
        param["id"] = deleteStory!!.id
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