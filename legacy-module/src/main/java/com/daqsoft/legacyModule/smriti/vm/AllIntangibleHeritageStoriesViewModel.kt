package com.daqsoft.legacyModule.smriti.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.bean.HomeStoryBean

/**
 * @package name：com.daqsoft.legacyModule.smriti.vm
 * @date 16/11/2020 上午 9:51
 * @author zp
 * @describe
 */
class AllIntangibleHeritageStoriesViewModel : BaseViewModel() {


    /**
     * 非遗故事 liveData
     */
    var intangibleHeritageStoryLiveData = MutableLiveData<BaseResponse<HomeStoryBean>>()
    /**
     * 获取非遗故事列表
     */
    fun getIntangibleHeritageStory(resourceId:String,resourceType:String) {
        LegacyRepository.service.getIntangibleHeritageStory(
            resourceId = resourceId,resourceType = resourceType,pageSize = 100
        )
            .excute(object : BaseObserver<HomeStoryBean>() {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    intangibleHeritageStoryLiveData.postValue(response)
                }
            })
    }
}