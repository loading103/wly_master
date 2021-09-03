package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.network.home.HomeRepository
import java.lang.Exception

/**
 * @des 找攻略列表的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 */
class FindStrategyListActivityViewModel : BaseViewModel() {

    /**
     * 热门
     */
    val TYPEHOT = "likeNumAndCommentNumAndShowNum"

    /**
     * 最新
     */
    val NEW = "likeNum"

    /**
     * 攻略封面
     */
    var strategyCover = MutableLiveData<HomeStoryBean>()

    /**
     * 故事列表数据
     */
    val storyList = MutableLiveData<BaseResponse<HomeStoryBean>>()

    var currPage: Int = 1

    /**
     * 获取攻略封面
     */
    fun getStoryCover() {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        param["listCover"] = "1"
        //  pageSize

        HomeRepository.service.getStoryCover(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    try {
                        var obj = response.data
                        strategyCover.postValue(obj)
                    } catch (e: Exception) {
                        strategyCover.postValue(null)
                    }

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
        param["pageSize"] = "10"

        if (orderType != "") {
            // 默认按发布时间降序排列 likeNum:按点赞数量降序排序 likeNumAndCommentNum:按点赞+评论降序排列 likeNumAndCommentNumAndShowNum:按点赞数+评论数+浏览数排序
            param["orderType"] = orderType
        }
        param["currPage"] = currPage.toString()
        param["storyType"] = "strategy"

        mPresenter.value?.loading = false
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response)
                }

                override fun onFailed(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response)
                }
            })
    }

}