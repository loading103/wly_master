package com.daqsoft.travelCultureModule.country.model

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.mainmodule.R
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_HOTEL
import com.daqsoft.travelCultureModule.country.adapter.CountryHotelMoreAdapter
import com.daqsoft.travelCultureModule.country.bean.ApiHoteltBean
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.net.CountryRepository

/**
 * desc :民宿列表viewModel
 * @author 江云仙
 * @date 2020/4/21 17:26
 */
class CountryHotelMoreViewModel : BaseViewModel() {
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    //地区编码
    var region = ""
    //排序方式
    var sortType = ""
    /**
     * 分页页码
     */
    var mCurrPage = 1
    /**
     * 获取页码大小
     */
    var mPageSize = 10
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
    /**
     * 酒店等级
     */
    val hotelLevel = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 酒店类型
     */
    val hotelType = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 酒店设施
     */
    val hotelFacilities = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 酒店服务
     */
    val hotelService = MutableLiveData<MutableList<ResourceTypeLabel>>()
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
    val typesHotel = mutableListOf(
        ResourceTypeLabel("", "", "", "1", "酒店等级").setSelects(true),
        ResourceTypeLabel("", "", "", "1", "酒店类型").setSelects(false),
        ResourceTypeLabel("", "", "", "1", "酒店设施").setSelects(false),
        ResourceTypeLabel("", "", "", "1", "酒店服务").setSelects(false)
    )
    /**
     * 名宿列表
     */
    val hotelList = MutableLiveData<MutableList<ApiHoteltBean>>()
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

            override fun onFailed(response: BaseResponse<ApiHoteltBean>) {
//                super.onFailed(response)
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
     *酒店等级
     */
    fun getHotelLevels(labelType: String,resourceType:String) {
        val map = HashMap<String, String>()
        map["labelType"] = labelType
        map["resourceType"] = resourceType
        CountryRepository.service.getSelectResLabel(map)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    hotelLevel.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     *酒店设施
     */
    fun getHotelFacilities(labelType: String,resourceType:String) {
        val map = HashMap<String, String>()
        map["labelType"] = labelType
        map["resourceType"] = resourceType
        CountryRepository.service.getSelectResLabel(map)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    hotelFacilities.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     *酒店服务
     */
    fun getHotelService(labelType: String,resourceType:String) {
        val map = HashMap<String, String>()
        map["labelType"] = labelType
        map["resourceType"] = resourceType
        CountryRepository.service.getSelectResLabel(map)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    hotelService.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     *酒店类型
     */
    fun getHotelTypes(labelType: String,resourceType:String) {
        val map = HashMap<String, String>()
        map["labelType"] = labelType
        map["resourceType"] = resourceType
        CountryRepository.service.getSelectResLabel(map)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    hotelType.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 收藏接口
     */
    fun collection(
        resourceId: String,
        imgCountryHapCollect: ImageView,
        item: ApiHoteltBean,
        countryHappinessMoreAdapter: CountryHotelMoreAdapter
    ) {
        CommentRepository.service.posClloection(resourceId, CONTENT_TYPE_HOTEL)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("收藏成功~")
                        item.vipResourceStatus.collectionStatus =true
                        countryHappinessMoreAdapter.notifyDataSetChanged()
//                        imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_selected)
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(
        resourceId: String,
        imgCountryHapCollect: ImageView,
        item: ApiHoteltBean,
        countryHappinessMoreAdapter: CountryHotelMoreAdapter
    ) {
//        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, CONTENT_TYPE_HOTEL)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("取消收藏成功~")
//                        notify.postValue(response)
                        item.vipResourceStatus.collectionStatus =false
                        countryHappinessMoreAdapter.notifyDataSetChanged()
                        imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_normal)
                    }

                }
            })
    }
}