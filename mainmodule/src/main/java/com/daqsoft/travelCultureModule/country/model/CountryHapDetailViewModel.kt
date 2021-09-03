package com.daqsoft.travelCultureModule.country.model

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.InformationBean
import com.daqsoft.provider.bean.ListStatusBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.excut
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.bean.CountryHapDetailBean
import com.daqsoft.travelCultureModule.country.bean.FoodProductBean
import com.daqsoft.travelCultureModule.country.net.CountryRepository
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity

/**
 * desc :农家乐详情viewModel
 * @author 江云仙
 * @date 2020/4/15 15:46
 */
class CountryHapDetailViewModel : BaseViewModel() {


    /**
     * 农家乐经度
     */
    var countryLng: Double = 0.0
    /**
     * 农家乐纬度
     */
    var countryLat: Double = 0.0
    /**
     * 点赞，收藏之后刷新页面
     */
    val notify = MutableLiveData<BaseResponse<Any>>()
    /**
     * 收藏状态变更
     */
//    var colllectLiveData = MutableLiveData<Boolean>()
    /**
     * 点赞状态变更
     */
//    var thumbLiveData = MutableLiveData<Boolean>()
    /**
     *  纬度
     */
    var lat: String? = null
    /**
     *  经度
     */
    var lng: String? = null
    /**
     * 获取地图资源
     */
    var mapResLiveData = MutableLiveData<BaseResponse<MapResBean>>()
    /**
     * 查询id
     */
    var id = ""
    /**
     *农家乐集合数据
     */
    var countryHapDetails = MutableLiveData<CountryHapDetailBean>()
    /**
     * 评论列表
     */
    val commentBeans = MutableLiveData<MutableList<CommentBean>>()
    /**
     * 是否隐藏评论
     */
    val hideCommentList = ObservableField<Boolean>(true)

    /**
     * 美食商品
     */
    var foodProductLiveData = MutableLiveData<MutableList<FoodProductBean>>()
    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     *资讯列表
     */
    val informationBean = MutableLiveData<MutableList<InformationBean>>()
    /**
     * 预约提醒状态更新
     */
    val notifyRemainLiveData = MutableLiveData<ListStatusBean>()

    /**
     * 获取评论列表
     */
    fun getActivityCommentList(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = CONTENT_TYPE_AGRITAINMENT
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

    fun getCountryHapDetails() {
//        mPresenter.value?.loading = true
//        mPresenter.value?.isNeedRecyleView=false
        val map = HashMap<String, String>()
        map["id"] = id
        CountryRepository.service.getCountryHapDetail(map).excute(object : BaseObserver<CountryHapDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CountryHapDetailBean>) {
                countryHapDetails.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<CountryHapDetailBean>) {
//                super.onFailed(response)
                mError.postValue(response)
            }
        })
    }

    /**
     * 获取商家商品列表
     */
    fun getCommendList(resourceCode: String, sysCode: String) {
        CountryRepository.electronicService.getFoodProductLs(resourceCode, sysCode)
            .excut(object : ElectronicObserver<MutableList<FoodProductBean>>() {
                override fun onSuccess(response: BaseResponse<MutableList<FoodProductBean>>) {
                    foodProductLiveData.postValue(response.data)
                }
            })

//        ElectronicRepository.electronicService.getFoodProductLs(resourceCode, sysCode)
//            .excut(object : ElectronicObserver<FoodProductBean>() {
//                override fun onSuccess(response: BaseResponse<FoodProductBean>) {
//                    foodProductLiveData.postValue(response.datas)
//                }
//            })
    }

    /**
     * 预约提醒
     */
    fun notify(status: Boolean, productId: String, position: Int) {
        // TODO 暂时没做提醒，目前接口环境在微信环境，娶不到openId
        CountryRepository.electronicService.notifyProduct(status, productId)
            .excut(object : ElectronicObserver<Any>() {
                override fun onSuccess(response:BaseResponse<Any>) {
                    if (response.code == 1) {
                        notifyRemainLiveData.postValue(ListStatusBean(position, status, ""))
                    } else {
                        mError.postValue(BaseResponse<Any>().apply {
                            message = "" + response.message
                        })
                    }
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError.postValue(BaseResponse<Any>().apply {
                        message = "" + response.message
                    })
                }
            })
    }

    /**
     * 获取相关故事数据列表
     */
    fun getStoryList(resourceType: String, resourceId: String) {
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
     * 获取推荐地图资源
     */
    fun gethMapRecList(type: String, id: String) {
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
     * 去到身边游找厕所
     */
    val goToilet = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                .withDouble("lat", countryLat)
                .withDouble("lon", countryLng)
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
                .withDouble("lat", countryLat)
                .withDouble("lon", countryLng)
                .navigation()
        }
    }
    /**
     * 公交查询
     */
    val goToQueryBus = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                .navigation()
        }
    }
    /**
     * 旅游投诉
     */
    val goToComplaint = object : ReplyCommand {
        override fun run() {
            MenuJumpUtils.gotoComplaint()
//            ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
//                .navigation()
        }
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
//        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
//                        colllectLiveData.postValue(true)
                        toast.postValue("收藏成功~")
                        notify.postValue(response)
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String) {
//        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消收藏成功~")
//                        colllectLiveData.postValue(false)
                        notify.postValue(response)
                    }

                }
            })
    }

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
//        mPresenter.value?.loading = true
        CommentRepository.service.postThumbUp(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("点赞成功~")
//                        thumbLiveData.postValue(true)
                        notify.postValue(response)
                    }

                }

                override fun onFailed(response: BaseResponse<Any>) {
//                    toast.postValue("点赞失败~")
                }
            })
    }

    /**
     * 取消点赞接口
     */
    fun thumbCancell(resourceId: String) {
//        mPresenter.value?.loading = true
        CommentRepository.service.postThumbCancel(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消点赞成功~")
//                        thumbLiveData.postValue(false)
                        notify.postValue(response)
                    }

                }
            })
    }

    /**
     * 收藏功能
     */
    val collectionCommand: ReplyCommand = object : ReplyCommand {
        override fun run() {
            if (countryHapDetails.value!!.vipResourceStatus.collectionStatus) {
                collectionCancel(countryHapDetails.value!!!!.id)
            } else {
                collection(countryHapDetails.value!!!!.id)
            }
        }
    }
    /**
     * 点赞功能
     */
    val thumbCommand: ReplyCommand = object : ReplyCommand {
        override fun run() {
            if (countryHapDetails.value!!.vipResourceStatus.likeStatus) {
                // 取消点赞
                thumbCancell(countryHapDetails.value!!.id)
            } else {
                // 点赞
                thumbUp(countryHapDetails.value!!.id)
            }
        }
    }

    /**
     *获取资讯列表
     */
    fun getCountryInfo(id: String) {
        val map = HashMap<String, Any>()
        map["pageSize"] = "2"
        map["linksResourceType"] = "CONTENT_TYPE_AGRITAINMENT"
        map["orderType"] = "publishTime"
        map["linksResourceId"] = id
        CountryRepository.service.getInfoList(map).excute(object : BaseObserver<InformationBean>() {
            override fun onSuccess(response: BaseResponse<InformationBean>) {
                informationBean.postValue(response.datas)
            }

        })
    }
//    fun getHomeChannelList() {
//        val map = HashMap<String, Any>()
//        CountryRepository.service.getHomeChannelList(map).excute(object :BaseObserver<Any>(){
//            override fun onSuccess(response: BaseResponse<Any>) {
////                labelId.postValue(response.data)
//            }
//
//        })
//    }
}