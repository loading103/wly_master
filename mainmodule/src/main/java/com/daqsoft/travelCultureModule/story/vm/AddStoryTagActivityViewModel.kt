package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 * 添加标签的view model
 */
class AddStoryTagActivityViewModel : BaseViewModel() {

    /**
     * 热门标签列表
     */
    val storyTagList by lazy { MutableLiveData<MutableList<HomeStoryTagBean>>() }

    /**
     * 标签记录
     */
    val storyTagRecordList by lazy { MutableLiveData<MutableList<String>>() }

    /**
     * 搜索的记录
     */
    val searchTagList by lazy { MutableLiveData<MutableList<HomeStoryTagBean>>() }

    /**
     * 获取热门故事标签列表
     */
    fun getHotStoryTagList() {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
        param["top"] = "1"
        // 最小故事数量
        param["minStoryNum"] = "0"
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
     * 常用故事标签列表
     */
    fun getStoryTagRecordList() {
        val param = HashMap<String, String>()
        mPresenter.value?.loading = true
        HomeRepository.service.getHomeTagRecordList()
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    storyTagRecordList.postValue(response.datas)
                }
            })
    }

    /**
     * 搜索故事标签列表
     */
    fun getStoryTagList(name: String) {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
        param["top"] = "1"
        // 最小故事数量
        param["minStoryNum"] = "0"
        //  size 查询数量 【选填】默认查询全部
        param["size"] = "6"
        // 按名称模糊查询
        param["tagName"] = name
        HomeRepository.service.getStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>() {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    searchTagList.postValue(response.datas)
                }
            })
    }

}