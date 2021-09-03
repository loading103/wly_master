package com.daqsoft.travelCultureModule.citycard.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardDetail
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import java.util.*

class CityCardViewModel : BaseViewModel() {
    /**
     * 获取城市列表
     */
    var mddCityList = MutableLiveData<MutableList<BrandMDD>>()
    var mddDQXList = MutableLiveData<MutableList<BrandMDD>>()

    /**
     * @param siteId 站点id
     * @param pageSize 页码
     * @param
     */
    fun getMDDCity(siteId: String, pagesize: String, type: String, region: String?) {
        mPresenter.value?.loading = true
        var siteIdStr: String = ""
        if (!siteId.isNullOrEmpty() && siteId != "null") {
            siteIdStr = siteId
        }
        HomeRepository.service.getMDDCity(pagesize, type, siteIdStr)
            .excute(object : BaseObserver<BrandMDD>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BrandMDD>) {
                    if (type == "city") {
                        mddCityList.postValue(response.datas)
                    } else {
                        mddDQXList.postValue(response.datas)
                    }
                }
            })
    }

    /**
     *获取城市名片
     */
    var cityInfo = MutableLiveData<CityCardDetail>()
    fun getCityCard(siteId: String) {
        HomeRepository.service.getCityCard(siteId).excute(object : BaseObserver<CityCardDetail>() {
            override fun onSuccess(response: BaseResponse<CityCardDetail>) {
                cityInfo.postValue(response.data)
            }
        })
    }

    /**
     *刷新用户信息获取站点信息
     */
    var siteId = MutableLiveData<String>()
    fun refresh() {
        UserRepository.userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    //SPUtils.getInstance().put(SPKey.SITEID, response.data?.siteId ?: -1)
                    siteId.postValue(response.data?.siteId.toString())
                }
            })
    }

    /**
     * 顶部菜单
     */
    var topMenus = MutableLiveData<MutableList<HomeMenu>>()
    fun getCityMenus(location: String, siteId: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getCityMenuList(Constant.HOME_CLIENT_TYPE, location, siteId)
            .excute(object :
                BaseObserver<HomeMenu>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeMenu>) {
                    topMenus.postValue(response.datas)
                }
            })
    }

    /**
     * 获取品牌
     */
    var homeBranchBeanList = MutableLiveData<MutableList<HomeBranchBean>>()
    fun getBranchList(siteId: String) {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        // queryType  【选填】默认为1 1 首页/品牌名片 2 城市名片 3 随机推荐
        param["queryType"] = "2"
        // 活动类型id
        param["pageSize"] = "6"
        param["currPage"] = "1"
        param["siteId"] = siteId
        HomeRepository.service.getBranchList(param)
            .excute(object : BaseObserver<HomeBranchBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                    homeBranchBeanList.postValue(response.datas)
                }
            })
    }

    /**
     * 必游景区
     */
    val secnicList = MutableLiveData<MutableList<ScenicBean>>()
    fun getScenicList(siteId: String) {
        mPresenter.value?.loading = true
        var map = HashMap<String, kotlin.String>()
        map["pageSize"] = "4"
        map["areaSiteSwitch"] = siteId
        MainRepository.service.getScenicList(map)
            .excute(object : BaseObserver<ScenicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ScenicBean>) {
                    secnicList.postValue(response.datas)
                }
            })
    }

    /**
     * 找活动
     */

    val activities = MutableLiveData<MutableList<ActivityBean>>()
    fun getActivityList(siteId: String) {
        val param = HashMap<String, String>()
        // orderType  为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
        param["orderType"] = "1"
        param["siteId"] = siteId
        param["notEndStatus"] = "1"
        param["pageSize"] = "4"
        mPresenter.value?.loading = true
        HomeRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }
            })
    }

    /**
     * 文化场馆列表
     * @param currPage 分页参数
     * @param region 地区编码
     * @param type 类型id
     * @param lng    经度
     * @param lat    纬度
     * @param hot    热度
     * @param activity 可预订活动
     * @param orderRoom 可预订活动室
     * @param tag 场馆标签
     */
    var venuesList = MutableLiveData<MutableList<VenuesListBean>>()
    fun getVenusList(pagesize: String, areaSiteSwitch: String) {
        VenuesRepository.venuesService.getCityVenuesList(pagesize, areaSiteSwitch)
            .excute(object : BaseObserver<VenuesListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VenuesListBean>) {
                    venuesList.postValue(response.datas)
                }
            })
    }

    /**
     * 酒店
     */
    val hotelList = MutableLiveData<MutableList<HotelBean>>()
    fun getHotelList(siteId: String) {
        val param = HashMap<String, String>()
        param["areaSiteSwitch"] = siteId
        param["pageSize"] = "4"
        MainRepository.service.getHotelList(param)
            .excute(object : BaseObserver<HotelBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HotelBean>) {
                    hotelList.postValue(response.datas)
                }
            })
    }

    /**
     * 美食
     */
    val foodList = MutableLiveData<MutableList<FoodBean>>()
    fun getFoodList(siteId: String) {
        val param = HashMap<String, String>()
        param["areaSiteSwitch"] = siteId
        param["pageSize"] = "4"
        MainRepository.service.getFoodList(param).excute(object : BaseObserver<FoodBean>() {
            override fun onSuccess(response: BaseResponse<FoodBean>) {
                foodList.postValue(response.datas)
            }

        })
    }

    /**
     * 故事
     */
    val storyList = MutableLiveData<MutableList<HomeStoryBean>>()
    fun getHotStoryList(siteId: String) {
        val param = HashMap<String, String>()
        param["pageSize"] = "4"
        param["areaSiteSwitch"] = siteId
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取天气
     */
    val weather = MutableLiveData<WeatherBean>()
    fun getWeather(region: String) {
        HomeRepository.service.getWeather(region).excute(object : BaseObserver<WeatherBean>() {
            override fun onSuccess(response: BaseResponse<WeatherBean>) {
                weather.postValue(response.data)
            }

        })
    }

    var foundArouds = MutableLiveData<MutableList<FoundAroundBean>>()

    /**
     * 获取周边
     */
    fun getFounds(lat: Double, lon: Double, id: String) {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["distance"] = "2"
        param["size"] = "3"
        param["latitude"] = "$lat"
        param["longitude"] = "$lon"
        param["areaSiteSwitch"] = id
        HomeRepository.service.getFoundList(param)
            .excute(object : BaseObserver<FoundAroundBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<FoundAroundBean>) {
                    foundArouds.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<FoundAroundBean>) {
                    foundArouds.postValue(null)
                }

            })
    }


    /**
     * 获取特产列表信息
     */
    val researchList = MutableLiveData<MutableList<SpeaiclBean>>()

    val param = HashMap<String, String>()
    fun getSpical(areaSiteSwitch: String ) {
        param.clear()
        mPresenter.value?.loading = false
        // 页面大小
        param["pageSize"] = "4"
        // 当前页码
        param["currPage"] = "1"
        if (!areaSiteSwitch.isNullOrEmpty()){
            param["areaSiteSwitch"] = areaSiteSwitch!!
        }
        MainRepository.service.getSpecialList(param).excute(object : BaseObserver<SpeaiclBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<SpeaiclBean>) {
                researchList.postValue(response?.datas!!)
            }

            override fun onFailed(response: BaseResponse<SpeaiclBean>) {
                super.onFailed(response)
                mError.postValue(response)
            }
        })
    }
}