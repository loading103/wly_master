package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.servicemodule.SERVICE_PLANE
import com.daqsoft.servicemodule.SERVICE_SUBWAY
import com.daqsoft.servicemodule.SERVICE_TRAIN
import com.daqsoft.servicemodule.bean.ChooseCityBean
import com.daqsoft.servicemodule.bean.RecordBean
import com.daqsoft.servicemodule.bean.StationBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/8 14:19
 */
class ChooseCityViewModel : BaseViewModel() {
    var name=""//精确匹配
    var initial=""//首字母关键字
    var keyword=""//
    //城市地址
    var queryTraffic: String=""
    //搜索类型
    var searchType:String=""
    /**
     * 字母集合数据
     */
    fun getLetter(): ArrayList<ChooseCityBean> {
        val result = ArrayList<ChooseCityBean>()
        for (i in 1..26) {
            result.add(ChooseCityBean(Character.toUpperCase((96 + i).toChar()).toString(), false))
        }
        return result
    }

    var result = MutableLiveData<MutableList<RecordBean>>()

    /**
     *获取历史记录
     */
    fun getRecordList() {
        mPresenter?.value?.loading= false
        val map = HashMap<String, String>()
        map["searchType"] = searchType
        map["size"] = "10"
        ServiceRepository().service.getSearchRecord(map)
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    val recordBeans = mutableListOf<RecordBean>()
                    response.datas?.forEach {
                        recordBeans.add(RecordBean(it,false))
                    }
                    result.postValue(recordBeans)
                }

            })
    }

    /**
     *保存历史记录
     */
    fun saveSearchRecord(key: String) {
        val map = HashMap<String, String>()
        map["searchType"] =searchType
        map["keyword"] = key
        ServiceRepository().service.saveSearchRecord(map).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {

            }

        })
    }
    /**
     *删除历史记录
     */
    var clear = MutableLiveData<String>()
    fun clearSearchRecord() {
        val map = HashMap<String, String>()
        map["searchType"] = searchType
        ServiceRepository().service.clearSearchRecord(map).excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                clear.postValue("")
            }

        })
    }
    /**
     *城市列表
     */
    var stationBean = MutableLiveData<MutableList<StationBean>>()
    fun getStationName() {
        mPresenter?.value?.loading = true
        val map = HashMap<String, String>()
        map["name"] = name
        map["keyword"] = keyword
        when (queryTraffic) {
            SERVICE_TRAIN -> {//火车城市查询
                map["initial"] = initial
                ServiceRepository().service.getStationName(map).excute(object : BaseObserver<StationBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<StationBean>) {
                        stationBean.postValue(response.datas)
                    }

                })
            }
            SERVICE_SUBWAY -> {//汽车城市查询
                map["letter"] = initial
                ServiceRepository().service.cityList(map).excute(object : BaseObserver<StationBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<StationBean>) {
                        stationBean.postValue(response.datas)
                    }

                })
            }
            SERVICE_PLANE -> {//飞机城市查询
                map["letter"] = initial
    //            map["country"] = ""
                ServiceRepository().service.aviationCityList(map).excute(object : BaseObserver<StationBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<StationBean>) {
                        stationBean.postValue(response.datas)
                    }

                })
            }
        }

    }
    //获取定位城市车站码
    var trainStationBean = MutableLiveData<MutableList<StationBean>>()
    fun getTrainStationName() {
        mPresenter?.value?.loading = true
        val map = HashMap<String, String>()
        map["name"] = name
        map["keyword"] = keyword
        if (queryTraffic == SERVICE_TRAIN){//火车城市查询
            map["initial"] = initial
            ServiceRepository().service.getStationName(map).excute(object : BaseObserver<StationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<StationBean>) {
                    trainStationBean.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<StationBean>) {
                    toast.postValue("请选择正确的城市")
                }

            })
        }

    }

}