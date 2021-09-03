package com.daqsoft.travelCultureModule.food.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.FoodBean
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 美食列表ViewModel
 * @ClassName   FoodLsViewModel
 * @Author      luoyi
 * @Time        2020/4/10 9:24
 */
class FoodLsViewModel : BaseViewModel() {


    /**
     * 页面大小
     */
    var pageSize: Int = 10
    /**
     * 页码
     */
    var currPage: Int = 1
    /**
     * 地区编码
     */
    var region: String = ""
    /**
     * 关键字
     */
    var keyWord: String = ""
    /**
     * 类型id
     */
    var type: String = ""
    /**
     * 服务设施
     */
    var eqt: String = ""

    /**
     * 经度
     */
    var lng: Double = 0.0
    /**
     * 纬度
     */
    var lat: Double = 0.0
    /**
     * 排序方式
     */
    var sortType: String = ""

    var areaSiteSwitch: String = ""

    val foodList = MutableLiveData<MutableList<FoodBean>>()

    /**
     * 美食类型
     */
    val foodTypesLiveData = MutableLiveData<MutableList<ResourceTypeLabel>>()

    /**
     * 美食服务设施
     */
    val foodServiceToolsLiveData = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    var collectLiveData: MutableLiveData<Int> = MutableLiveData()

    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()
    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()
    // 排序
    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("可订优先", "orderStatus"),
        ValueKeyBean("距离优先", "disNum"),
        ValueKeyBean("推荐优先", "recommendHomePage"),
        ValueKeyBean("人气优先", "hot"),
        ValueKeyBean("好评优先", "commentLevel")
    )

    val types = mutableListOf(
        ResourceTypeLabel("", "", "", "1", "餐厅类型").setSelects(true),
        ResourceTypeLabel("", "", "", "2", "餐厅设施")
    )

    /**
     * 获取美食列表数据
     */
    fun getFoodsLs() {

        var param: HashMap<String, String> = HashMap()
        if (!region.isNullOrEmpty()) {
            param["region"] = region
        }

        if (!areaSiteSwitch.isNullOrEmpty()) {
            param["areaSiteSwitch"] = areaSiteSwitch
        }
        param["pageSize"] = pageSize.toString()
        param["currPage"] = currPage.toString()
        if (!type.isNullOrEmpty()) {
            param["type"] = type
        }
        if (!eqt.isNullOrEmpty()) {
            param["eqt"] = eqt
        }
        if (lng != 0.0) {
            param["lng"] = lng.toString()
        }
        if (lat != 0.0) {
            param["lat"] = lat.toString()
        }
        if (!sortType.isNullOrEmpty()) {
            param["sortType"] = sortType
        }

        MainRepository.service.getFoodList(param).excute(object : BaseObserver<FoodBean>() {
            override fun onSuccess(response: BaseResponse<FoodBean>) {
                foodList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<FoodBean>) {
                mError.postValue(response)
            }

        })
    }

    fun getChildRegion() {
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
     * 获取美食类型
     */
    fun getFoodTypes() {
        MainRepository.service.getSelectResLabel("TYPE", ResourceType.CONTENT_TYPE_RESTAURANT)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    foodTypesLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取美食服务设施
     */
    fun getFoodServiceTools() {
        MainRepository.service.getSelectResLabel("FACILITIES", ResourceType.CONTENT_TYPE_RESTAURANT)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    foodServiceToolsLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 收藏接口
     */
    fun collectionScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_RESTAURANT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已收藏~")
                    collectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }

    /**
     * 取消收藏接口
     */

    fun collectionCancelScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_RESTAURANT
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已取消收藏~")
                    canceCollectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取景区顶部广告
     */
    fun getFoodLsTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.FOOD_LIST_TOP_ADV)
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