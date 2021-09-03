package com.daqsoft.venuesmodule.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.VenueLevelBean
import com.daqsoft.provider.bean.VenueTypeBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * 文化场馆列表ViewModel
 * @author 黄熙
 * @date 2019/12/18 0018
 * @version 1.0.0
 * @since JDK 1.8
 */
class VenuesViewModel : BaseViewModel() {
    /**
     * 文化场馆数据元
     */
    var venuesList = MutableLiveData<MutableList<VenuesListBean>>()
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    /**
     *  文化馆等级
     */
    var venueLevel: String? = null

    /**
     * 文化馆类型
     */
    var venueType: String? = null

    /**
     * 文化馆tag
     */
    var tag: String? = null

    /**
     * 排序规则
     */
    var orderType: String? = null
    /**
     *  区域地址id
     */
    var areaSiteSwitch: String? = null

    /**
     * 当前页
     */
    var currPage: Int = 1
    /**
     *  页面大小
     */
    var pageSize: Int = 10
    /**
     *  纬度
     */
    var lat: String? = null
    /**
     *  经度
     */
    var lng: String? = null

    var venuesTypeLiveData = MutableLiveData<MutableList<VenueTypeBean>>()

    var venuesLevelLiveData = MutableLiveData<MutableList<VenueLevelBean>>()

    var regionLiveData = MutableLiveData<MutableList<ChildRegion>>()

    var canceCollectLiveData = MutableLiveData<Int>()

    var collectVenueLiveData = MutableLiveData<Int>()

    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()
    /**
     * 文化场馆列表
     *  venueLevel	文化馆等级
     *  region	地区
     *  type	  类型
     *  areaSiteSwitch 地区位置id
     *  lng	""  经度
     *  lat	""  纬度
     *  keyword	关键字
     *   orderType	文化馆类型
     *  currPage	当前页
     *  pageSize	页码
     */
    fun getVenusList() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        val param = HashMap<String, String>()
        if (!venueLevel.isNullOrEmpty()) {
            param["venueLevel"] = venueLevel!!
        }
        if (!venueType.isNullOrEmpty()) {
            param["type"] = venueType!!
        }
        if (!areaSiteSwitch.isNullOrEmpty()) {
            param["areaSiteSwitch"] = areaSiteSwitch!!
        }
        if (!orderType.isNullOrEmpty()) {
            param["orderType"] = orderType!!
        }
        param["currPage"] = currPage.toString()
        param["pageSize"] = pageSize.toString()

        if (!lat.isNullOrEmpty() && !lng.isNullOrEmpty()) {
            param["lat"] = lat!!
            param["lng"] = lng!!
        }
        if (!tag.isNullOrEmpty()){
            param["tag"] = tag!!
        }
        VenuesRepository.venuesService.getVenuesList(
            param
        ).excute(object : BaseObserver<VenuesListBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<VenuesListBean>) {
                venuesList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VenuesListBean>) {
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
        VenuesRepository.venuesService.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ChildRegion>) {
                mError.postValue(response)
            }
        })
    }

    /**
     *  获取文化馆类型
     */
    fun getVenueTypes() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.getVenueType("VENUE_type").excute(object : BaseObserver<VenueTypeBean>() {
            override fun onSuccess(response: BaseResponse<VenueTypeBean>) {
                venuesTypeLiveData.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<VenueTypeBean>) {
                mError.postValue(response)
            }

        })
    }

    /**
     * 获取文化等级
     */
    fun getVenueLevels(venueLevel:String) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.getVenueLevel(venueLevel  ).excute(object : BaseObserver<VenueLevelBean>() {
            override fun onSuccess(response: BaseResponse<VenueLevelBean>) {
                venuesLevelLiveData.postValue(response.datas)
            }
            override fun onFailed(response: BaseResponse<VenueLevelBean>) {
                mError.postValue(response)
            }
        })
    }

    /**
     * 收藏
     */
    fun collect(id: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.collectVenue("CONTENT_TYPE_VENUE", id).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                collectVenueLiveData.postValue(position)
                toast.postValue("收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("收藏失败，请稍后再试~")
                mError.postValue(response)
            }
        })
    }

    /**
     * 取消收藏
     */
    fun canceCollect(id: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        VenuesRepository.venuesService.canceCollectVenue(id, "CONTENT_TYPE_VENUE").excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                canceCollectLiveData?.postValue(position)
                toast.postValue("取消收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("取消收藏，请稍后再试~")
                mError.postValue(response)
            }
        })
    }

    /**
     * 获取景区顶部广告
     */
    fun getVenueLsTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.VENUE_LIST_TOP_ADV)
            .excute(object : BaseObserver<HomeAd>() {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(null)
                }
            })
    }
}