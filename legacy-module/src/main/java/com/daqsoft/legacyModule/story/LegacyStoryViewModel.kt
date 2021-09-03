package com.daqsoft.legacyModule.story

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.bean.LegacyStoryTagListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.bean.PageManager

/**
 * des      非遗故事  ViewModel
 * @class   LegacyStoryViewModel
 * @author  Wongxd
 * @date    2020-4-24  11:57
 */
internal class LegacyStoryViewModel : BaseViewModel() {


    val storyTagList = MutableLiveData<List<LegacyStoryTagListBean>>()

    fun getStoryTagList() {
        LegacyRepository.service.getStoryHotTagList()
            .excute(object : BaseObserver<LegacyStoryTagListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryTagListBean>) {
                    storyTagList.postValue(response.datas)
                }
            })
    }


    val pageManager = PageManager(10)

    val storyList = MutableLiveData<List<LegacyStoryListBean>>()

    var orderType = "likeNumAndCommentNumAndShowNum"

    fun getStoryList() {
        mPresenter.value?.loading = true
        LegacyRepository.service.getStoryList(orderType = orderType,pageSize = pageManager.pageSize, currPage = pageManager.pageIndex)
            .excute(object : BaseObserver<LegacyStoryListBean>(mPresenter,true) {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }
}