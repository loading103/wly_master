package com.daqsoft.travelCultureModule.resource.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.HotelBean
import com.daqsoft.provider.bean.MapResourceIconBean
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   ScenicMapMViewModel
 * @Author      PuHua
 * @Time        2020/3/16 18:09
 */
class ScenicMapMViewModel : BaseViewModel() {

    val param = HashMap<String, String>()

    /**
     * 活动列表
     * sortType  为空(默认排序) recommendHomePage （推荐排序）；hot（热度排行）；disNum(距离排行)
     */
    var secnicList = MutableLiveData<MutableList<ScenicBean>>()

    var mapResourceIconsLd = MutableLiveData<MutableList<MapResourceIconBean>>()

    /**
     * 搜索景区相关信息
     */
    fun searchScenicMapList(p: HashMap<String, String>) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getMapRecInfo(p)
            .excute(object : BaseObserver<ScenicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ScenicBean>) {
                    secnicList.postValue(response.datas)
                }
            })
    }

    fun getMapRecIcon() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getMapRecIcons()
            .excute(object : BaseObserver<MapResourceIconBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MapResourceIconBean>) {
                    mapResourceIconsLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<MapResourceIconBean>) {
                    mapResourceIconsLd.postValue(null)
                }
            })
    }

}