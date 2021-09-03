package com.daqsoft.travelCultureModule.sidetour.viewmodel

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.sidetour.bean.GasStationBean

/**
 * @Description
 * @ClassName   SideTourViewModel
 * @Author      luoyi
 * @Time        2020/3/18 16:07
 */
class SideTourViewModel : BaseViewModel() {

    val param = HashMap<String, String>()

    /**
     *  地图列表
     * sortType  为空(默认排序) recommendHomePage （推荐排序）；hot（热度排行）；disNum(距离排行)
     */
    var mapList = MutableLiveData<MutableList<MapResBean>>()

    /**加油站详情*/
    var gasStationDetail = MutableLiveData<GasStationBean>()

    /**
     * 停车位详情
     */
    var parkingInfo = MutableLiveData<ParkingBean>()

    /**
     * 搜索厕所资源列表
     */
    fun searchMapList(p: HashMap<String, String>) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getMapToilentRecInfo(p)
            .excute(object : BaseObserver<MapResBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MapResBean>) {
                   mapList.postValue(response.datas)
                }
            })
    }

    /**
     * 加油站详情
     * 需要id 经度 纬度
     */
    fun getGasstationDetails(id: Int,lng: Double, lat: Double){
        val params = ArrayMap<String,Any>()
        params["id"] = id
        params["lng"] = lng
        params["lat"] = lat
        params["siteCode"] = SPUtils.getInstance().getString(SPKey.SITE_CODE)

        MainRepository.service.getGasstations(params)
            .excute(object : BaseObserver<GasStationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<GasStationBean>) {
                    gasStationDetail.postValue(response.data)
                }
            })
    }

}