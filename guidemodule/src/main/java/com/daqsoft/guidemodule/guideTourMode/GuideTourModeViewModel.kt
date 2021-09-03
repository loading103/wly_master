package com.daqsoft.guidemodule.guideTourMode

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.guidemodule.bean.GuideLineBean
import com.daqsoft.guidemodule.bean.GuideScenicDetailBean
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.net.GuideRepository
import com.daqsoft.guidemodule.net.GuideService
import java.util.HashMap

/**
 * @Description  导览地图-预览及导览模式 ViewModel
 * @ClassName   GuideTourModeViewModel
 * @Author      Wongxd
 * @Time        2020/4/10 19:29
 */
internal class GuideTourModeViewModel : BaseViewModel() {

    val homeList = MutableLiveData<List<GuideScenicListBean>>()

    fun getGuideHomeList(param: HashMap<String, String>) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        GuideRepository.service.getGuideScenicList(param)
            .excute(object : BaseObserver<GuideScenicListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<GuideScenicListBean>) {
                    response.datas?.let {
                        it.forEach {
                            it.showTypeForDev = param["resourceType"] ?: ""
                        }
                        homeList.postValue(it)
                    }

                }
            })
    }

}