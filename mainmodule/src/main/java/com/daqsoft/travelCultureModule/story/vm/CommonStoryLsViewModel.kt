package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.travelCultureModule.net.MainRepository

class CommonStoryLsViewModel : BaseViewModel() {
    var pageIndex = 1

    /**
     * 相关故事列表
     */
    val storyList by lazy { MutableLiveData<BaseResponse<StoreBean>>() }

    /**
     * 获取相关故事数据列表
     */
    fun getStoryList(resourceId: String, resourceType: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = resourceType
        //  pageSize
        param["pageSize"] = "10"
        param["currPage"] = pageIndex.toString()

        if (resourceId != "") {
            param["resourceId"] = resourceId
        }
        mPresenter.value?.loading = false
        MainRepository.service.getStoryList(param)
            .excute(object : BaseObserver<StoreBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<StoreBean>) {
                    storyList.postValue(response)
                }

                override fun onFailed(response: BaseResponse<StoreBean>) {
                    storyList.postValue(null)
                }
            })
    }
}