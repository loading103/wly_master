package com.daqsoft.travelCultureModule.playground.viewmodel
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
import com.daqsoft.provider.bean.*
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
class PlayGroundLsViewModel : BaseViewModel() {


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

    var applyType: String = ""

    var feature: String = ""
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

    var totleNumber: Int = 0

    val playgroundList = MutableLiveData<MutableList<PlayGroundBean>>()

    /**
     * 场馆类型
     */
    val foodTypesLiveData = MutableLiveData<MutableList<VenueLevelBean>>()

    /**
     * 场馆设施
     */
    val foodServiceToolsLiveData = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 娱乐场景类型
     */
    val playServiceLiveData = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 特色服务类型
     */
    val featureLiveData = MutableLiveData<MutableList<ResourceTypeLabel>>()
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
        ResourceTypeLabel("", "", "", "1", "娱乐类型").setSelects(true),
        ResourceTypeLabel("", "", "", "2", "适用场景"),
        ResourceTypeLabel("", "", "", "3", "娱乐设施"),
        ResourceTypeLabel("", "", "", "4", "特色服务")
    )
    /**
     * 获取娱乐场所列表数据
     * http://ctc-api.test.daqsoft.com/v2/res/api/entertainment/getApiEntertainList?token=604198ccdae343e28b4ae3bae4e89373&eqt=&type=entertainmentType5&region=&keyword=&areaSiteSwitch=&sortType=&lng=104.08497&lat=30.50943&applyType=&feature=&currPage=1&pageSize=10
     */
    fun getPlaygroundLs() {
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
        if (!applyType.isNullOrEmpty()) {
            param["applyType"] = applyType
        }
        if (!feature.isNullOrEmpty()) {
            param["feature"] = feature
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

        MainRepository.service.getPlayGroundList(param).excute(object : BaseObserver<PlayGroundBean>() {
            override fun onSuccess(response: BaseResponse<PlayGroundBean>) {
                playgroundList.postValue(response.datas)
                totleNumber= response.page?.total!!
            }

            override fun onFailed(response: BaseResponse<PlayGroundBean>) {
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
     * 获取娱乐场所类型
     */
    fun getFoodTypes() {
        MainRepository.service.getVenueLevel("ENTER_TYPE")
            .excute(object : BaseObserver<VenueLevelBean>() {
                override fun onSuccess(response: BaseResponse<VenueLevelBean>) {
                    foodTypesLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VenueLevelBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取适用场景选择项
     */
    fun getFoodServiceTools() {
        MainRepository.service.getSelectResLabel("APPLY_TYPE", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
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
     * 获取娱乐设施选择项
     */
    fun getEntEqtType() {
        MainRepository.service.getSelectResLabel("ENT_EQT_TYPE", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    playServiceLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 获取娱特色服务选择享
     */
    fun getFeatureType() {
        MainRepository.service.getSelectResLabel("ENT_FEATURE_TYPE", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    featureLiveData.postValue(response.datas)
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
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
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
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT
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