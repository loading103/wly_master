package com.daqsoft.travelCultureModule.country.model

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.electronicBeans.RouteResult
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.travelCultureModule.country.net.CountryRepository
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity

/**
 * 乡村介绍ViewModel
 */
class CountryDetailMoreViewModel : BaseViewModel() {

    var id = ""
    /**
     *  纬度
     */
    var lat: String? = null
    /**
     *  经度
     */
    var lng: String? = null
    /**
     * 乡村介绍纬度
     */
    var scenicLat: Double = 0.0
    /**
     * 乡村介绍经度
     */
    var scenicLng: Double = 0.0
    /**
     * 乡村介绍详情
     */
    val scenicDetail = MutableLiveData<CountryDetailBean>()
    /**
     * 刷新乡村介绍数据
     */
    val refereshScenicDetail = MutableLiveData<CountryDetailBean>()
    /**
     * 景点列表
     */
    val spots = MutableLiveData<MutableList<Spots>>()
    /**
     * 音频和金牌解说
     */
    val goldStory = MutableLiveData<GoldStory>()
    val voices = MutableLiveData<MutableList<GoldStory>>()
    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     * 线路列表
     */
    val routeResult = MutableLiveData<RouteResult>()
    /**
     * 直播列表
     */
    val scenicSpotsPanor = MutableLiveData<MutableList<ScenicSpotsPanor>>()
    /**
     * 评论列表
     */
    val commentBeans = MutableLiveData<MutableList<CommentBean>>()
    /**
     * 是否隱藏景點模块
     */
    val hideSpots = ObservableField<Boolean>(true)

    val hideGoldStory = ObservableField<Boolean>(true)
    /**
     * 是否隐藏线路
     */
    val hideRoute = ObservableField<Boolean>(true)
    /**
     * 是否隐藏活动
     */
    val hideActivity = ObservableField<Boolean>(true)
    /**
     * 是否隐藏评论
     */
    val hideCommentList = ObservableField<Boolean>(true)
    /**
     * 是否隐藏故事
     */
    val hideStory = ObservableField<Boolean>(true)
    /**
     * 隐藏直播
     */
    val hidePanor = ObservableField<Boolean>(true)

    /**
     * 品牌
     */
    val scenicBrandListLiveData = MutableLiveData<MutableList<HomeBranchBean>>()
    /**
     * 获取地图资源
     */
    var mapResLiveData = MutableLiveData<BaseResponse<MapResBean>>()

    /**
     * 收藏状态变更
     */
    var colllectLiveData = MutableLiveData<Boolean>()
    /**
     * 点赞状态变更
     */
    var thumbLiveData = MutableLiveData<Boolean>()
    /**
     * 乡村介绍门票
     */
    var tiketsLiveData = MutableLiveData<SptTicketBean>()
    /**
     * 乡村介绍须知
     */
    var scenicReservationLiveData = MutableLiveData<ScenicReservationBean>()
    /**
     * 线路须知
     */
    var routerReservationLiveData = MutableLiveData<RouterReservationBean>()
    /**
     * 订单地址信息
     */
    var orderAddresInfoLiveData = MutableLiveData<MutableList<OderAddressInfoBean>>()
    /**
     * 系统code
     */
    var sysCode: String = ""
    /**
     * 资源编码
     */
    var resourceCode: String = ""

    /**
     * 资讯列表
     */
    var contentLsLd = MutableLiveData<MutableList<ContentBean>>()

    /**
     * 获取乡村介绍详情
     */
    fun getScenicDetail(id: String, getMore: Boolean) {
        mPresenter?.value?.loading = false
        CountryRepository.service.getApiRuralInfo(id).excute(object : BaseObserver<CountryDetailBean>() {
            override fun onSuccess(response: BaseResponse<CountryDetailBean>) {

                scenicDetail.postValue(response.data)
                if (getMore) {
//                    getSiteInfo()
//                    getScenicPanor(
//                        response.data!!.region, response.data!!.latitude.toString(), response.data
//                        !!.longitude.toString()
//                    )
                }
            }
        })
    }

    fun refreshScenicDetail(id: String) {
        mPresenter?.value?.loading = false
        CountryRepository.service.getApiRuralInfo(id).excute(object : BaseObserver<CountryDetailBean>() {
            override fun onSuccess(response: BaseResponse<CountryDetailBean>) {
                refereshScenicDetail.postValue(response.data)
            }
        })
    }

    /**
     * @param id 乡村介绍id
     */
    fun getScenicBrandList(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getScenicBrandList("1", id, ResourceType.CONTENT_TYPE_COUNTRY).excute(object : BaseObserver<HomeBranchBean>() {

            override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                scenicBrandListLiveData.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<HomeBranchBean>) {
                scenicBrandListLiveData.postValue(null)
            }
        })
    }

    /**
     * 景点列表
     */
    fun getScenicSpots(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CountryRepository.service.getApiRuralSpotsList(id).excute(object : BaseObserver<Spots>() {
            override fun onSuccess(response: BaseResponse<Spots>) {
                if (response.datas!!.size == 0) {
                    hideSpots.set(true)
                } else {
                    hideSpots.set(false)
                }
                spots.postValue(response.datas)
            }
        })
    }


    /**
     * 获取直播列表
     */
    fun getScenicPanor(region: String, currentLat: String, currentLon: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["region"] = region
        param["type"] = ResourceType.CONTENT_TYPE_COUNTRY
        param["lat"] = currentLat.toString()
        param["lng"] = currentLon.toString()
        MainRepository.service.getScenicSpotsPanor(param).excute(object : BaseObserver<ScenicSpotsPanor>() {
            override fun onSuccess(response: BaseResponse<ScenicSpotsPanor>) {
                if (response.datas!!.size == 0) {
                    hidePanor.set(true)
                } else {
                    hidePanor.set(false)
                }
                scenicSpotsPanor.postValue(response.datas)
            }
        })

    }

    /**
     * 获取相关故事数据列表
     */
    fun getStoryList(resourceId: String, resourceType: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = resourceType
        //  pageSize
        param["pageSize"] = "4"

        if (resourceId != "") {

            param["resourceId"] = resourceId
        }
        mPresenter.value?.loading = false
        MainRepository.service.getStoryList(param).excute(object : BaseObserver<StoreBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<StoreBean>) {
                storyList.postValue(response.datas)
            }
        })
    }


    /**
     * 获取评论列表
     */
    fun getActivityCommentList(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.CONTENT_TYPE_COUNTRY
        param["resourceId"] = id
        param["pageSize"] = "2"
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {

                if (response.datas!!.size > 0) {
                    hideCommentList.set(false)
                } else {
                    hideCommentList.set(true)
                }
                commentBeans.postValue(response.datas)


            }
        })
    }

    /**
     * 门票列表
     */
    fun getTickList(id: String, sysCode: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
        ElectronicRepository.electronicService
            .getTicketList(id, sysCode, token)
            .excut(object : ElectronicObserver<SptTicketBean>() {
                override fun onSuccess(response: BaseResponse<SptTicketBean>) {
                    tiketsLiveData.postValue(response.data)
                }
            })
    }

    /**
     * 获取乡村介绍预订须知
     * @param productSn 产品编号
     */
    fun getReservationInfo(productSn: String) {
        ElectronicRepository.electronicService
            .getScenicReservationInfo(productSn)
            .excut(object : ElectronicObserver<ScenicReservationBean>() {
                override fun onSuccess(response: BaseResponse<ScenicReservationBean>) {
                    scenicReservationLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ScenicReservationBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取线路预订须知
     * @param productSn 产品编号
     */
    fun getRouterReservationInfo(productSn: String, name: String) {
        ElectronicRepository.electronicService
            .getRouterReservationInfo(sysCode, productSn)
            .excut(object : ElectronicObserver<RouterReservationBean>() {
                override fun onSuccess(response: BaseResponse<RouterReservationBean>) {
                    if (response.data != null) {
                        // 增加 名称
                        response.data?.name = name
                    }
                    routerReservationLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<RouterReservationBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取线路列表
     */
    fun getRouteList() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        ElectronicRepository.electronicService.getRouteList(resourceCode, sysCode, "scenic", "")
            .excut(object : ElectronicObserver<RouteResult>(mPresenter) {
                override fun onSuccess(response: BaseResponse<RouteResult>) {
                    if (response.data!!.totalNum > 0) {
                        hideRoute.set(false)
                        routeResult.postValue(response.data)
                    } else {
                        hideRoute.set(true)
                    }
                }

            })
    }

    /**
     * 获取站点信息
     */
    fun getSiteInfo() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        // 下面这段代码 不知道干嘛的，ios也说不清楚，暂时不请求
//        UserRepository().userService.getSiteInfo()
//            .excute(object : BaseObserver<SiteInfo>(mPresenter) {
//                override fun onSuccess(response: BaseResponse<SiteInfo>) {
//                    SPUtils.getInstance().put(SPKey.DOMAIN, response.data?.shopUrl)
//                    SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
//                    SPUtils.getInstance().put(SPKey.SHOP_URL, response.data?.shopUrl)
//                }
//
//            })
//        if (AppUtils.isLogin()) {
        // 如果已经登录，同步一下token到 其实这个也不知道是干什么的，也没人能说清楚
//            loginElectronic(SPUtils.getInstance().getString(SPKey.UUID), SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN))
//        } else {
        refreshUserInfo()

//        }
    }

    /**
     * 获取推荐地图资源
     */
    fun gethMapRecList(type: String, id: String, lng: String, lat: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        var params: HashMap<String, String> = hashMapOf()
        params["pageSize"] = "4"
        if (!lat.isNullOrEmpty()) {
            params["latitude"] = lat!!
        } else {
            params["latitude"] = "0"
        }
        if (!lng.isNullOrEmpty()) {
            params["longitude"] = lng!!
        } else {
            params["longitude"] = "0"
        }
        params["type"] = type
        params["id"] = id
        MainRepository.service.getCommonRecInfo(params)
            .excute(object : BaseObserver<MapResBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MapResBean>) {
                    response.type = type
                    mapResLiveData.postValue(response)
                }
            })
    }

    /**
     * 小电商登录
     */
    fun loginElectronic(uuid: String, token: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.isNeedToastMessage = false
        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
        ElectronicRepository.electronicService.login(uuid, token, encry)
            .excut(object : ElectronicObserver<ElectronicLogin>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicLogin>) {
                    var userLogin = response.data
                    sysCode = userLogin!!.userInfo.sysCode
                    SPUtils.getInstance().put(SPKey.SESSIONID, userLogin?.sessionId)
                    if (!sysCode.isNullOrEmpty() && !resourceCode.isNullOrEmpty()) {
                        getTickList(resourceCode, sysCode)
                        getRouteList()
                    }
                }
            })
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("已收藏~")
                    colllectLiveData.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(response)
                    toast.postValue("收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 取消收藏接口
     */

    fun collectionCancel(resourceId: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("已取消收藏~")
                    colllectLiveData.postValue(false)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(response)
                    toast.postValue("取消收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
        CommentRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("点赞成功~")
                    thumbLiveData.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(response)
                    toast.postValue("点赞失败~")
                }
            })
    }

    /**
     * 取消点赞接口
     */
    fun thumbCancell(resourceId: String) {
        CommentRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
            .excute(object : BaseObserver<Any>(mPresenter) {

                override fun onSuccess(response: BaseResponse<Any>) {
                    thumbLiveData.postValue(false)
                    toast.postValue("取消点赞成功~")
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(response)
                    toast.postValue("取消点赞失败~")
                }
            })
    }

    /**
     * 去到身边游找厕所
     */
    val goToilet = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                .withDouble("lat", scenicLat)
                .withDouble("lon", scenicLng)
                .navigation()
        }
    }

    /**
     * 去到停车场
     */
    val goToPark = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_PARK)
                .withDouble("lat", scenicLat)
                .withDouble("lon", scenicLng)
                .navigation()
        }
    }

    /**
     * 获取下单地址信息
     * @param id 资源id
     */
    fun getOrderAddressInfo(id: String) {
        mPresenter?.value?.loading = false
        MainRepository.service.getOrderAddressInfo(ResourceType.CONTENT_TYPE_COUNTRY, id)
            .excute(object : BaseObserver<OderAddressInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OderAddressInfoBean>) {
                    orderAddresInfoLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<OderAddressInfoBean>) {
                    mError.postValue(response)
                }

            })
    }

    /**
     * 场馆资讯列表
     */
    fun getScenicContentLs(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false

        VenuesRepository.venuesService.getContentLs("publishTime", ResourceType.CONTENT_TYPE_COUNTRY, id)
            .excute(object : BaseObserver<ContentBean>() {
                override fun onSuccess(response: BaseResponse<ContentBean>) {
                    contentLsLd?.postValue(response.datas)
                }

            })
    }

    /**
     * 刷新用户信息
     */
    fun refreshUserInfo() {
        mPresenter?.value?.loading = false
        UserRepository().userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    SPUtils.getInstance()
                        .put(SPKey.USER_CENTER_TOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.UUID, response.data?.unid)
                    SPUtils.getInstance().put(SPKey.SITEID, response.data?.siteId ?: -1)
                    SPUtils.getInstance().put(SPKey.ENCRYPTION, response.data?.encryption ?: "")
                    SPUtils.getInstance().put(SPKey.VIPID, response.data?.vipId ?: -1)
                    SPUtils.getInstance().put(SPKey.UID, response.data?.unid)
                    SPUtils.getInstance().put(SPKey.PHONE, response.data?.phone)
                    SPUtils.getInstance().put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                    getTickList(resourceCode, sysCode)
                    getRouteList()
                }

                override fun onFailed(response: BaseResponse<UserLogin>) {
                    getTickList(resourceCode, sysCode)
                    getRouteList()
                }
            })

    }
}