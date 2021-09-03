package com.daqsoft.travelCultureModule.country.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_HOTEL
import com.daqsoft.travelCultureModule.country.HOTEL_TYPE
import com.daqsoft.travelCultureModule.country.bean.*
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.net.CountryRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * desc :乡村游viewmodel
 * @author 江云仙
 * @date 2020/4/13 14:58
 */
class CountryTourViewModel : BaseViewModel() {
    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     * 名宿列表
     */
    val hotelList = MutableLiveData<MutableList<ApiHoteltBean>>()
    /**
    *乡村游头部信息
    */
    val visitingCardBean = MutableLiveData<VisitingCardBean>()
    /**
     * 分页页码
     */
    var mCurrPage = 1
    /**
    *风物推荐页码
    */
    var mFwCurrPage = 1
    var fwTotalPage=MutableLiveData<Int>()
    /**
     * 获取页码大小
     */
    var mPageSize = 10
    //banner type
    var cardType = "TOURISM"
    //地区编码
    var region = ""
    //类型
    var type = ""
    //等级
    var level = ""
    //设施
    var eqt = ""
    //服务
    var special = ""
    //经纬度
    var lat = ""
    var lon = ""
    //下级站点ID
    var areaSiteSwitch = ""
    //排序方式
    var sortType = ""
    //资源类型
    var resourceType = CONTENT_TYPE_AGRITAINMENT

    // 排序
    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("可订优先", "order"),
        ValueKeyBean("诚信优先", "credit"),
        ValueKeyBean("距离优先", "distance"),
        ValueKeyBean("推荐优先", "recommend"),
        ValueKeyBean("人气优先", "popularity"),
        ValueKeyBean("好评优先", "praise")
    )
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    /**
     * 酒店等级
     */
    val hotelLevel = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     *农家乐集合数据
     */
    var agritainment = MutableLiveData<MutableList<AgritainmentBean>>()

    fun getAgritainmentList() {
        val map = HashMap<String, Any>()
        map["currPage"] = mCurrPage
        map["pageSize"] = mPageSize
        map["region"] = region
        map["type"] = type
        map["lat"] = lat
        map["lon"] = lon
        map["areaSiteSwitch"] = areaSiteSwitch
        map["sortType"] = sortType
        CountryRepository.service.agritainmentList(map).excute(object : BaseObserver<AgritainmentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<AgritainmentBean>) {
                agritainment.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<AgritainmentBean>) {
                super.onFailed(response)
                mError.postValue(response)
            }
        })
    }

    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CountryRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ChildRegion>) {
                mError.postValue(response)
            }
        })
    }



    /**
     * 获取相关故事数据列表
     */
    fun getStoryList() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["orderType"] = "likeNumAndCommentNumAndShowNum"
        param["pageSize"] = "4"

        param["resourceType"] = CONTENT_TYPE_AGRITAINMENT
        mPresenter.value?.loading = false
        MainRepository.service.getStoryList(param).excute(object : BaseObserver<StoreBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<StoreBean>) {
                storyList.postValue(response.datas)
            }
        })
    }

    /**
     * 获取名宿列表
     */
    fun getApiHotelList() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, Any>()
        map["currPage"] = mCurrPage
        map["pageSize"] = mPageSize
        map["type"] = type
        map["level"] = level
        map["eqt"] = eqt
        map["special"] = special
        map["lat"] = lat
        map["lng"] = lon
        map["areaSiteSwitch"] = areaSiteSwitch
        mPresenter.value?.loading = false
        CountryRepository.service.getApiHotelList(map).excute(object : BaseObserver<ApiHoteltBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ApiHoteltBean>) {
                hotelList.postValue(response.datas)
            }
        })
    }
    /**
     * 乡村游头部信息
     */
    fun getVisitingCard(siteId: String) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, String>()
        map["cardType"] = cardType
        map["siteId"] = siteId
        mPresenter.value?.loading = false
        CountryRepository.service.getVisitingCard(map).excute(object : BaseObserver<VisitingCardBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<VisitingCardBean>) {
                visitingCardBean.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<VisitingCardBean>) {
                mError.postValue(response)
            }
        })
    }

    /**
     *获取用户昵称
     */
    var nickName = MutableLiveData<String>()
    fun refresh() {
        UserRepository().userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    nickName.postValue(response.data?.nickName.toString() )
                }
            })
    }


    /**
     * 获取天气
     */
    val weather=MutableLiveData<WeatherBean>()
    fun getWeather(region:String){

        HomeRepository.service.getWeather(region).excute(object :BaseObserver<WeatherBean>(){
            override fun onSuccess(response: BaseResponse<WeatherBean>) {
                weather.postValue(response.data)
            }

        })
    }
    /**
     * 乡村游记攻略
     */
    val travelGLGuides=MutableLiveData<MutableList<TravelGuideBean>>()

    fun getTravelGuide(channelCode: String) {
        val map = HashMap<String, Any>()
        map["tagId"] = ""
        map["channelCode"] = channelCode
        map["pageSize"] = "10"
        map["currPage"] = mCurrPage
        map["areaSiteSwitch"] = areaSiteSwitch
        CountryRepository.service.getTravelGuide(map).excute(object :BaseObserver<TravelGuideBean>(){
            override fun onSuccess(response: BaseResponse<TravelGuideBean>) {
                travelGLGuides.postValue(response.datas)
            }

        })
    }

    /**
     * 全部乡村列表
     */
    val countryList = MutableLiveData<MutableList<CountryListBean>>()
    fun getCountryAllList(siteId: String?=null) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, Any>()
        map["currPage"] = mCurrPage
        map["pageSize"] = "20"
        map["region"] = region
        map["keyword"] = ""
        map["sortType"] = "recommendHomePage"
        map["lng"] = lon
        map["lat"] = lat
        siteId?.let { map["areaSiteSwitch"] = siteId }

        CountryRepository.service.getApiRuralList(map)
            .excute(object : BaseObserver<CountryListBean>() {
                override fun onSuccess(response: BaseResponse<CountryListBean>) {
                    countryList.postValue(response.datas)
                }
            })
    }

    /**
     * 风物推荐
     */
    val travelFWGuides = MutableLiveData<MutableList<TravelGuideBean>>()
    fun getTravelFWGuide(channelCode: String) {
        val map = HashMap<String, Any>()
        map["channelCode"] = channelCode
        map["pageSize"] = "5"
        map["currPage"] = mFwCurrPage
        map["areaSiteSwitch"] = areaSiteSwitch
        CountryRepository.service.getTravelGuide(map).excute(object :BaseObserver<TravelGuideBean>(){
            override fun onSuccess(response: BaseResponse<TravelGuideBean>) {
                travelFWGuides.postValue(response.datas)
                fwTotalPage.postValue(response.page?.total?:-1)

            }

        })
    }
    //风物推荐
    val labelId=MutableLiveData<LabelBean>()
    fun getCloudLabelId() {
        val map = HashMap<String, Any>()
        map["labelType"] = HOTEL_TYPE
        map["resourceType"] = CONTENT_TYPE_HOTEL
        map["labelName"] = "民宿"
        CountryRepository.service.getCloudLabelId(map).excute(object :BaseObserver<LabelBean>(){
            override fun onSuccess(response: BaseResponse<LabelBean>) {
                labelId.postValue(response.data)
            }

        })
    }

}