package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 * 标签墙viewmodel
 */
class StoryTagActivityViewModel : BaseViewModel() {

    /**
     * 故事标签列表
     */
    val storyTagList by lazy { MutableLiveData<MutableList<HomeStoryTagBean>>() }

    /**
     * 热门标签
     */
    val storyHotTagList by lazy { MutableLiveData<MutableList<HomeStoryTagBean>>() }

    /**
     * 获取热门故事标签列表
     */
    fun getHotStoryTagList() {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
        // 最小故事数量
        param["minStoryNum"] = "1"
        //  size 查询数量 【选填】默认查询全部
        param["size"] = "8"
        mPresenter.value?.loading = false
        HomeRepository.service.getHotStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    storyHotTagList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取热门故事标签列表
     */
    fun getStoryTagList() {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
//        param["recommend"] = "1"
//        // top  是否置顶 选填】1：是 0：否
//        param["top"] = "1"
//        // 最小故事数量
        param["minStoryNum"] = "1"
        param["sortDayNum"] = "7"
//        //  size 查询数量 【选填】默认查询全部
//        param["size"] = "6"
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    storyTagList.postValue(response.datas)
                }
            })
    }

}