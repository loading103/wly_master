package com.daqsoft.guidemodule.search

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.guidemodule.bean.*
import com.daqsoft.guidemodule.net.GuideRepository
import java.util.HashMap


/**
 * @Description  导游导览搜索ViewModel
 * @ClassName   GuideSearchViewModel
 * @Author      Wongxd
 * @Time        2020/4/1 17:12
 */
class GuideSearchViewModel : BaseViewModel() {
    var searchList = MutableLiveData<MutableList<GuideSearchBean>>()

    fun getSearchList(lat: String, lng: String, tourId: String, name: String) {
        GuideRepository.service.searchAll(lat, lng, tourId, name).excute(object :
            BaseObserver<GuideSearchBean>() {
            override fun onSuccess(response: BaseResponse<GuideSearchBean>) {
                searchList.postValue(response.datas)
            }
        })
    }

    val homeList = MutableLiveData<BaseResponse<GuideScenicListBean>>()

    fun getGuideHomeList(param: HashMap<String, String>) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        GuideRepository.service.searchGuideListNewAll(param)
            .excute(object : BaseObserver<GuideScenicListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<GuideScenicListBean>) {
                    response.datas?.let {
                        it.forEach {
                            it.showTypeForDev = param["resourceType"] ?: ""
                        }
                        homeList.postValue(response)
                    }
                }

                override fun onFailed(response: BaseResponse<GuideScenicListBean>) {
                    homeList.postValue(null)
                }
            })
    }


    val guideLineData = MutableLiveData<List<GuideLineBean>>()

    fun getLine(param: HashMap<String, String>) {
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
    fun getGuideResoureType(tourId: String, mapMode: Int = 1) {
        mPresenter?.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        var mode = if (mapMode > 0) {
            mapMode
        } else {
            1
        }
        // 搜索默认全域查询
        GuideRepository.service.getGuideResourceList(tourId, mode).excute(object : BaseObserver<GuideResouceBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<GuideResouceBean>) {
                tourResourceListLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<GuideResouceBean>) {
                tourResourceListLd.postValue(null)
            }

        })
    }
}