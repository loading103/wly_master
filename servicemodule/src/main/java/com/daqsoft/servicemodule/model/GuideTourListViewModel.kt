package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.servicemodule.bean.TourGuideBean
import com.daqsoft.servicemodule.bean.TourLevelBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :导游列表的viewmodel
 * @author 江云仙
 * @date 2020/4/2 9:21
 * @version 1.0.0
 * @since JDK 1.8
 */

class GuideTourListViewModel : BaseViewModel() {
    /**
     * 获取导游列表
     */
    var result = MutableLiveData<MutableList<TourGuideBean>>()
    fun getTourGuideList(map: HashMap<String, Any>) {
        mPresenter?.value?.loading = true
        ServiceRepository().service.tourGuideList(map)
            .excute(object : BaseObserver<TourGuideBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<TourGuideBean>) {
                    result.postValue(response.datas)
                }
                override fun onFailed(response: BaseResponse<TourGuideBean>) {
                    super.onFailed(response)
                    mError.postValue(response)
                }
            })
    }
    //获取地区
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    fun getChildRegions(){
        mPresenter.value?.loading = true
        ServiceRepository().service.getChildRegions().excute(object : BaseObserver<ChildRegion>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }
    /**
     *导游等级数据
     */

     fun getLevelData():MutableList<TourLevelBean> {
        var tourLevels: MutableList<TourLevelBean>? = ArrayList()
        tourLevels?.add(TourLevelBean("初级","guideLevel_1"))
        tourLevels?.add(TourLevelBean("中级","guideLevel_2"))
        tourLevels?.add(TourLevelBean("高级","guideLevel_3"))
        tourLevels?.add(TourLevelBean("特级","guideLevel_4"))
        return tourLevels?: mutableListOf<TourLevelBean>()
    }
}