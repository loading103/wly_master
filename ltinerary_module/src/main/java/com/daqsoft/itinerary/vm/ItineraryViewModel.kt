package com.daqsoft.itinerary.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.bean.*
import com.daqsoft.itinerary.repository.ItineraryRepository
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

/**
 * @Author:     邓益千
 * @Create by   2020/4/20 20:14
 * @Description
 */
class ItineraryViewModel : BaseViewModel() {

    /**推荐行程数据总数*/
    var total = 0

    /**我的行程数据总数*/
    var myTotal = 0

    /**当前页*/
    var currPage: Int = 1

    /**页面大小*/
    private var pageSize: Int = 10

    /**我的行程列表*/
    var myItineraryList = MutableLiveData<MutableList<ItineraryBean>>()

    /**行程列表*/
    var itineraryList = MutableLiveData<BaseResponse<ItineraryBean>>()

    /**行程详情*/
    val itineraryDetail = MutableLiveData<ItineraryDetailBean>()

    /**交通列表*/
    val trafficDetail = MutableLiveData<Traffbean>()

    /**附近餐馆or酒店列表*/
    val nearbyList = MutableLiveData<MutableList<NearbyBean>>()

    /**推荐页-标签筛选List*/
    var filterLabel = MutableLiveData<RecommFilterLabelBean>()

    /**刷新详情数据*/
    val refresh = MutableLiveData<Int>()

    /**
     * 行程列表
     * @param sourceType CLIENT我的定制，SYSTEM系统推荐
     * @param day 行程天数
     * @param fitTags 人群
     * @param tags 景区标签
     * @param enablePage 是否分页
     */
    fun getItineraryList(sourceType: String,day: String = "",fitTags: String = "",tags: String = "",enablePage: Boolean = true) {
        mPresenter.value?.loading = true
        ItineraryRepository.service.getItineraryList(sourceType, day, fitTags, tags,pageSize,currPage,enablePage).excute(object : BaseObserver<ItineraryBean>() {
            override fun onSuccess(response: BaseResponse<ItineraryBean>) {
                if (sourceType == "CLIENT"){
                    myTotal = response.page!!.total
                    myItineraryList.postValue(response.datas)
                } else {
                    total = response.page!!.total
                    itineraryList.postValue(response)
                }
            }

            override fun onFailed(response: BaseResponse<ItineraryBean>) {
                super.onFailed(response)
                myItineraryList.postValue(response.datas)
                //ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 行程更新
     * @param itineraryId 行程id
     * @param renamed 重命名
     */
    fun itineraryUpdate(itineraryId: String,renamed: String){
        ItineraryRepository.service.itineraryUpdate(itineraryId,renamed).excute(object: Observer<ResponseBody> {
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(response: ResponseBody) {
                val resultJson = response.string()
                val json = JSONObject(resultJson)
                if (json.getInt("code") == 0){
                    refresh.value = 0
                } else {
                    ToastUtils.showMessage(json.getString("message"))
                }
            }
        })
    }

    /**
     * 获取筛选数据
     */
    fun getFilterLabel() {
        ItineraryRepository.service.getFilterLabel().excute(object : BaseObserver<RecommFilterLabelBean>() {
            override fun onSuccess(response: BaseResponse<RecommFilterLabelBean>) {
                filterLabel.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<RecommFilterLabelBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 行程详情
     * @param itineraryId 行程id
     */
    fun getItineraryDetail(itineraryId: String){
        mPresenter.value?.loading = true
        ItineraryRepository.service.getItineraryDetail(itineraryId).excute(object : BaseObserver<ItineraryDetailBean>() {
            override fun onSuccess(response: BaseResponse<ItineraryDetailBean>) {
                itineraryDetail.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<ItineraryDetailBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 查询景区门票
     */
    fun queryTicket(sysCode: String,resourceCode: String){
        ItineraryRepository.service.queryTicket(sysCode,resourceCode).excute(object : Observer<ResponseBody> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}
            override fun onError(e: Throwable) {}
            override fun onNext(response: ResponseBody) {
                val json = JSONObject(response.string())
                if (json.getString("code").toInt() == 1){

                }
            }
        })
    }

    /**
     * 添加到我的行程
     * @param id 行程id
     * @param startTime 出发日期
     */
    fun addMyItinerary(id: String,startTime: String): MutableLiveData<String>{
        val result = MutableLiveData<String>()
        ItineraryRepository.service.addMyItinerary(id.toInt(),startTime).excute(object: Observer<ResponseBody> {
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(response: ResponseBody) {
                val resultJson = response.string()
                val json = JSONObject(resultJson)
                if (json.getInt("code") == 0){
                    val body = JSONObject(json.getString("data"))
                    result.postValue(body.getInt("id").toString())
                } else {
                    ToastUtils.showMessage(json.getString("message"))
                }
            }
        })
        return result
    }

    /**
     * 查询交通
     */
    fun queryTraffic(startId: Int, endId: Int,travelType: String = ""){
        mPresenter.value?.loading = true
        ItineraryRepository.service.queryTraffic(startId,endId,travelType).excute(object : BaseObserver<Traffbean>() {
            override fun onSuccess(response: BaseResponse<Traffbean>) {
                trafficDetail.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<Traffbean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 餐馆列表
     * 传入经纬度
     */
    fun getDinerList(plan: ItineraryDetailBean.AgendaBean.PlansBean, currPage: Int){
        mPresenter.value?.loading = true
        ItineraryRepository.service.getDinerList(plan.latitude,plan.longitude,currPage).excute(object : BaseObserver<NearbyBean>() {
            override fun onSuccess(response: BaseResponse<NearbyBean>) {
                nearbyList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<NearbyBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 酒店列表
     * 传入经纬度
     */
    fun getHotelList(plan: ItineraryDetailBean.AgendaBean.PlansBean, currPage: Int){
        mPresenter.value?.loading = true
        ItineraryRepository.service.getHotelList(plan.latitude,plan.longitude,currPage).excute(object : BaseObserver<NearbyBean>() {
            override fun onSuccess(response: BaseResponse<NearbyBean>) {
                nearbyList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<NearbyBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**在行程中添加或者删除*/
    fun operateSource(params: Map<String,Any>){
        mPresenter.value?.loading = true
        val json = Gson().toJson(params)
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),json)
        ItineraryRepository.service.operateSource(body).excute(object : Observer<ResponseBody> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}
            override fun onError(e: Throwable) {}
            override fun onNext(response: ResponseBody) {
                val resultJson = response.string()
                val json = JSONObject(resultJson)
                refresh.value = json.getInt("code")
                if (json.getInt("code") != 0){
                    ToastUtils.showMessage(json.getString("message"))
                }
            }
        })
    }

}