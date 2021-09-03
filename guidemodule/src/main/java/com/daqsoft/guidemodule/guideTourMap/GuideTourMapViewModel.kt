package com.daqsoft.guidemodule.guideTourMap

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.guidemodule.GuideMapShowType
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideLineBean
import com.daqsoft.guidemodule.bean.GuideResouceBean
import com.daqsoft.guidemodule.bean.GuideScenicDetailBean
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.net.GuideRepository
import com.daqsoft.guidemodule.net.GuideService
import com.daqsoft.provider.base.ResourceType
import java.util.HashMap

/**
 * @Description  导览地图ViewModel
 * @ClassName   GuideTourMapViewModel
 * @Author      Wongxd
 * @Time        2020/4/9 14:04
 */
internal class GuideTourMapViewModel : BaseViewModel() {


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


    val infoData = MutableLiveData<GuideScenicDetailBean>()

    fun getInfo(id: String) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        GuideRepository.service.getGuideDetail(id)
            .excute(object : BaseObserver<GuideScenicDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<GuideScenicDetailBean>) {
                    infoData.postValue(response.data)
                }
            })
    }

    val guideLineData = MutableLiveData<List<GuideLineBean>>()

    fun getLine(param: HashMap<String, String>) {
        mPresenter?.value?.loading = false
        mPresenter.value?.isNeedToastMessage = false
        GuideRepository.service.getGuideRoute(param).excute(object : BaseObserver<GuideLineBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<GuideLineBean>) {
                guideLineData.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<GuideLineBean>) {
                guideLineData.postValue(null)
            }
        })
    }

    val tourResourceListLd = MutableLiveData<MutableList<GuideResouceBean>>()
    fun getGuideResoureType(tourId: String, type: Int = 1) {
        mPresenter?.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        GuideRepository.service.getGuideResourceList(tourId, type).excute(object : BaseObserver<GuideResouceBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<GuideResouceBean>) {
                tourResourceListLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<GuideResouceBean>) {
                tourResourceListLd.postValue(null)
            }

        })
    }


}