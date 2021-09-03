package com.daqsoft.travelCultureModule.hotel.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
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
import com.daqsoft.travelCultureModule.net.MainRepository
import retrofit2.http.Query

/**
 * @Description 酒店列表的viewModel
 * @ClassName   HotActivitiesFragmentViewModel
 * @Author      luoyi
 * @Time        2020/4/7 10:58
 */
class HotelDetailViewModel : BaseViewModel() {

    /**
     *  纬度
     */
    var lat: String? = null
    /**
     *  经度
     */
    var lng: String? = null
    /**
     * 酒店详情liveData
     */
    var hotelDetailLiveData: MutableLiveData<HotelDetailBean> = MutableLiveData()
    /**
     * 酒店详情liveData
     */
    var refreshDetailLiveData: MutableLiveData<HotelDetailBean> = MutableLiveData()
    /**
     * 获取地图资源
     */
    var mapResLiveData = MutableLiveData<BaseResponse<MapResBean>>()

    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     * 评论列表
     */
    val commentBeans = MutableLiveData<MutableList<CommentBean>>()

    /**
     * 是否隐藏活动
     */
    val hideActivity = ObservableField<Boolean>(true)

    /**
     * 收藏状态变更
     */
    var colllectLiveData = MutableLiveData<Boolean>()
    /**
     * 点赞状态变更
     */
    var thumbLiveData = MutableLiveData<Boolean>()

    /**
     * 酒店房间
     */
    var hotelRoomsLiveData = MutableLiveData<MutableList<HotelRoomBean>>()

    /**
     * 酒店房间详情
     */
    var hotelRoomInfoLiveData = MutableLiveData<HotelRoomInfoBean>()
    /**
     * 线路须知
     */
    var routerReservationLiveData = MutableLiveData<RouterReservationBean>()
    /**
     * 线路列表
     */
    val routeResult = MutableLiveData<RouteResult>()

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
     * 获取酒店详情
     * @param id  酒店id
     */
    fun getHotelDetail(id: String) {
        MainRepository.service.getHotelDetail(id).excute(object : BaseObserver<HotelDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HotelDetailBean>) {
                hotelDetailLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<HotelDetailBean>) {
                mError.postValue(response)
            }

        })
    }

    /**
     * 刷新用户信息
     */
    fun refreshUserInfo(id: String) {
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
                    getHotelDetail(id)
                }

                override fun onFailed(response: BaseResponse<UserLogin>) {
                    getHotelDetail(id)
                }
            })
    }

    /**
     * 刷新酒店详情数据
     * @param id  酒店id
     */
    fun refreshHotelDetail(id: String) {
        MainRepository.service.getHotelDetail(id).excute(object : BaseObserver<HotelDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HotelDetailBean>) {
                refreshDetailLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<HotelDetailBean>) {
                mError.postValue(response)
            }

        })
    }

    /**
     * 获取推荐地图资源
     */
    fun gethMapRecList(type: String, id: String, lat: String, lng: String) {
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
     * 获取相关故事数据列表
     */
    var  storyNumber:String ?=""
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
                storyNumber = response.page?.total?.toString()
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
        param["resourceType"] = ResourceType.CONTENT_TYPE_HOTEL
        param["resourceId"] = id
        param["pageSize"] = "2"
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
            }
        })
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_HOTEL)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("已收藏~")
                    colllectLiveData.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
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
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_HOTEL)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("已取消收藏~")
                    colllectLiveData.postValue(false)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue("取消收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_HOTEL)
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
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_HOTEL)
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
     * 获取酒店房间列表
     * @param resourceId 资源id
     * @param roomNum 房间num
     * @param outTime 离店时间
     * @param inTime 入店时间
     * @param sysCode 店铺编号
     *
     */
    fun getHotelRoomLs(resourceId: String, roomNum: Int, outTime: String, inTime: String, sysCode: String) {
        ElectronicRepository.electronicService.getHotelRoomLs(roomNum, outTime, inTime, sysCode, resourceId)
            .excut(object : ElectronicObserver<MutableList<HotelRoomBean>>() {
                override fun onSuccess(response: BaseResponse<MutableList<HotelRoomBean>>) {
                    hotelRoomsLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<MutableList<HotelRoomBean>>) {
                    mError.postValue(response.apply {
                        requestCode = 1
                    })
                }
            })
    }

    /**
     * 获取酒店房间详情
     * @param outTime 离开事件
     * @param inTime  入店时间
     * @param roomSn  房间编号
     */
    fun getHotelRoomDetail(outTime: String, inTime: String, roomSn: String) {
        ElectronicRepository.electronicService.getHotelRoomDetail(outTime, inTime, roomSn)
            .excut(object : ElectronicObserver<HotelRoomInfoBean>() {
                override fun onSuccess(response: BaseResponse<HotelRoomInfoBean>) {
                    hotelRoomInfoLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HotelRoomInfoBean>) {
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
        ElectronicRepository.electronicService.getRouteList(resourceCode, sysCode, "hotel", "")
            .excut(object : ElectronicObserver<RouteResult>(mPresenter) {
                override fun onSuccess(response: BaseResponse<RouteResult>) {
                    if (response.data!!.totalNum > 0) {
                        routeResult.postValue(response.data)
                    }
                }

            })
    }

    /**
     * 获取下单地址信息
     * @param id 资源id
     */
    fun getOrderAddressInfo(id: String) {
        MainRepository.service.getOrderAddressInfo(ResourceType.CONTENT_TYPE_HOTEL, id)
            .excute(object : BaseObserver<OderAddressInfoBean>() {
                override fun onSuccess(response: BaseResponse<OderAddressInfoBean>) {
                    orderAddresInfoLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<OderAddressInfoBean>) {
                    mError.postValue(response)
                }

            })
    }
}