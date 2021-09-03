package com.daqsoft.travelCultureModule.food.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   FoodDetailViewModel
 * @Author      luoyi
 * @Time        2020/4/11 9:21
 */
class FoodDetailViewModel : BaseViewModel() {

    /**
     * 餐厅详情
     */
    val foodDetailLiveData: MutableLiveData<FoodDetailBean> = MutableLiveData()
    /**
     * 刷新数据状态
     */
    var refreshFoodDetailLiveData:MutableLiveData<FoodDetailBean> =MutableLiveData()
    /**
     * 收藏状态变更
     */
    var colllectLiveData = MutableLiveData<Boolean>()
    /**
     * 点赞状态变更
     */
    var thumbLiveData = MutableLiveData<Boolean>()
    /**
     * 获取地图资源
     */
    var mapResLiveData = MutableLiveData<BaseResponse<MapResBean>>()

    /**
     * 美食商品
     */
    var foodProductLiveData = MutableLiveData<MutableList<FoodProductBean>>()
    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     * 评论列表
     */
    val commentBeans = MutableLiveData<MutableList<CommentBean>>()
    /**
     *  纬度
     */
    var lat: String? = null
    /**
     *  经度
     */
    var lng: String? = null

    var  storyNumber:String ?=""
    /**
     * 获取餐厅详情
     * @param id String
     */
    fun getFoodDetail(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false

        MainRepository.service.getFoodDetail(id).excute(object : BaseObserver<FoodDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<FoodDetailBean>) {
                foodDetailLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<FoodDetailBean>) {
                mError.postValue(response)
            }
        })
    }

    fun refreshFoodDetailStatus(id:String){
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false

        MainRepository.service.getFoodDetail(id).excute(object : BaseObserver<FoodDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<FoodDetailBean>) {
                refreshFoodDetailLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<FoodDetailBean>) {
                mError.postValue(response)
            }
        })
    }
    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_RESTAURANT)
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
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_RESTAURANT)
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
        CommentRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_RESTAURANT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("点赞成功~")
                    thumbLiveData.postValue(true)

                }

                override fun onFailed(response: BaseResponse<Any>) {
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
        CommentRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_RESTAURANT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    thumbLiveData.postValue(false)
                    toast.postValue("取消点赞成功~")
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue("取消点赞失败~")
                }
            })
    }

    /**
     * 获取推荐地图资源
     */
    fun gethMapRecList(type: String, id: String,lat:String,lng:String) {
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
                storyNumber = response.page?.total?.toString()
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
        param["resourceType"] = ResourceType.CONTENT_TYPE_RESTAURANT
        param["resourceId"] = id
        param["pageSize"] = "2"
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
            }
        })
    }

    /**
     * 获取商品列表
     */
    fun getFoodProducts(resourceCode: String, sysCode: String) {
        ElectronicRepository.electronicService.getFoodProductLs(resourceCode, sysCode)
            .excut(object : ElectronicObserver<MutableList<FoodProductBean>>() {
                override fun onSuccess(response: BaseResponse<MutableList<FoodProductBean>>) {
                    foodProductLiveData.postValue(response.data)
                }
            })
    }

    /**
     * 刷新用户信息
     */
    fun refreshUserInfo(resourceCode: String, sysCode: String) {
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
                    getFoodProducts(resourceCode,sysCode)
                }

                override fun onFailed(response: BaseResponse<UserLogin>) {
                    getFoodProducts(resourceCode,sysCode)
                }
            })
    }
}