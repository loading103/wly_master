package com.daqsoft.venuesmodule.model

import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.provider.network.venues.bean.VenuesDetailsBean
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.utils.Utils
import com.daqsoft.venuesmodule.R
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function

/**
 * 文化场馆详情页ViewModel
 * @author luoyi
 * @date 2020/3/27 17:13
 * @version 1.0.0
 * @since JDK 1.8
 */
class VenuesDetailsViewModel : BaseViewModel() {
    /**
     *详情数据
     */
    var venuesDetailsBean = MutableLiveData<VenuesDetailsBean>()
    /**
     * 场馆开放时间
     */
    var openWeek = MutableLiveData<String>()
    /**
     * 获取评论列表
     */
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()

    /**
     * 资讯列表
     */
    var contentLsLd = MutableLiveData<MutableList<ContentBean>>()
    /**
     * 获取地图资源
     */
    var mapResLiveData = MutableLiveData<BaseResponse<MapResBean>>()
    /**
     * 故事列表
     */
    var storeisLiveData = MutableLiveData<MutableList<HomeStoryBean>>()
    var totalStoryLiveData = MutableLiveData<Int>()

    var canceThumbLiveData = MutableLiveData<BaseResponse<Any>>()

    var thumbLiveData = MutableLiveData<BaseResponse<Any>>()

    var collectLiveData = MutableLiveData<BaseResponse<Any>>()

    var canceCollectLiveData = MutableLiveData<BaseResponse<Any>>()
    /**
     * 订单地址信息
     */
    var orderAddresInfoLiveData = MutableLiveData<MutableList<OderAddressInfoBean>>()

    var venuesLat: Double = 0.0
    var venuesLon: Double = 0.0
    var mDatasStickTop: MutableList<ValueIdKeyBean> = mutableListOf(
        ValueIdKeyBean("介绍", 0),
        ValueIdKeyBean("解说", 0),
        ValueIdKeyBean("活动", 0),
        ValueIdKeyBean("活动室", 0),
        ValueIdKeyBean("资讯", 0),
        ValueIdKeyBean("点评", 0),
        ValueIdKeyBean("推荐", 0)
    )
    var lat: String? = null

    var lng: String? = null
    /**
     * 获取文化场馆详情数据
     */
    fun getVenuesDetails(id: String) {
        mPresenter.value?.loading = true
        mPresenter.value?.isNeedRecyleView = false
        VenuesRepository.venuesService.getVenuesDetails(id)
            .excute(object : BaseObserver<VenuesDetailsBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VenuesDetailsBean>) {
//                    openWeek.value = Utils().OpenWeekUtils(response.data!!.openWeek)
                    venuesDetailsBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VenuesDetailsBean>) {
                    super.onFailed(response)
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取评论列表
     */
    fun getVenuesCommentList(id: String) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.CONTENT_TYPE_VENUE
        param["resourceId"] = id
        param["pageSize"] = "2"
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
            }
        })
    }

    /**
     * 场馆资讯列表
     */
    fun getVenueContentLs(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false

        VenuesRepository.venuesService.getContentLs("publishTime", ResourceType.CONTENT_TYPE_VENUE, id)
//        VenuesRepository.venuesService.getContentLs("", "", "")
            .excute(object : BaseObserver<ContentBean>() {
                override fun onSuccess(response: BaseResponse<ContentBean>) {
                    contentLsLd?.postValue(response.datas)
                }

            })
    }

    /**
     * 获取景区地图资源
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
        VenuesRepository.venuesService.getCommonMapRecInfo(params)
            .excute(object : BaseObserver<MapResBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MapResBean>) {
                    response.type = type
                    mapResLiveData.postValue(response)
                }
            })
    }

    /**
     * 获取场馆故事详情
     */
    fun getVenuesStories(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        var params: HashMap<String, String> = hashMapOf()
        params["pageSize"] = "4"
        params["resourceId"] = id
        params["resourceType"] = ResourceType.CONTENT_TYPE_VENUE
        VenuesRepository.venuesService.getCommonStories(params)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storeisLiveData.postValue(response.datas)

                    response.page?.let {
                        totalStoryLiveData.value = it.total
                    }
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
                .withInt("TAB_POS", 0)
                .withDouble("lat", venuesLat)
                .withDouble("lon", venuesLon)
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
                .withInt("TAB_POS", 1)
                .withDouble("lat", venuesLat)
                .withDouble("lon", venuesLon)
                .navigation()
        }
    }

    /**
     * 删除楼层
     */
    fun removeStickTopItem(name: String) {
        for (i in mDatasStickTop.indices) {
            val item = mDatasStickTop[i]
            if (item.name == name) {
                mDatasStickTop[i].id = 0
                break
            }
        }
    }

    /**
     * 修改楼层信息
     */
    fun updateStickTopItem(name: String, id: Int) {
        for (i in mDatasStickTop.indices) {
            val item = mDatasStickTop[i]
            if (item.name == name) {
                mDatasStickTop[i].id = id
                break
            }
        }
    }

    /**
     * 返回最新楼层信息
     */
    fun getStickTopItems(): MutableList<ValueIdKeyBean> {
        var temps: MutableList<ValueIdKeyBean> = mutableListOf()
        for (item in mDatasStickTop) {
            if (item.id != 0) {
                temps.add(item)
            }
        }
        return temps
    }

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
        CommentRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_VENUE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("点赞成功~")
                        thumbLiveData.postValue(response)
                    }

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
        CommentRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_VENUE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消点赞成功~")
                        canceThumbLiveData.postValue(response)
                    }


                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue("取消点赞失败~")
                }
            })
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_VENUE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("收藏成功~")
                        collectLiveData.postValue(response)
                    }

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue("取消点赞失败~")
                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String) {
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_VENUE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消收藏成功~")
                        canceCollectLiveData.postValue(response)
                    }

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue("取消收藏失败~")
                    canceCollectLiveData.postValue(response)
                }
            })
    }
    /**
     * 获取下单地址信息
     * @param id 资源id
     */
    fun getOrderAddressInfo(id: String) {
        mPresenter?.value?.loading=false
        VenuesRepository.venuesService.getOrderAddressInfo(ResourceType.CONTENT_TYPE_VENUE, id)
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
     * 获取资源栏目列表
     */
    fun getResourceChannelList(id: String){
        mPresenter?.value?.loading=false
        VenuesRepository
            .venuesService
            .getResourceChannelList(id,ResourceType.CONTENT_TYPE_VENUE)
            .excute(object : BaseObserver<ResourceChannel>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ResourceChannel>) {
                    response.datas?.let {
                        // 固定3个资源
                        val yszy = BaseApplication.context.resources.getString(R.string.venue_art_resources)
                        val xszl = BaseApplication.context.resources.getString(R.string.venue_online_exhibition)
                        val jdsx = BaseApplication.context.resources.getString(R.string.venue_classic_appreciation)
                        val fixedResource = arrayListOf<String>(yszy, xszl, jdsx)
                        // 取两个集合并集
                        fixedResource.retainAll(it.map { it.name })
                        // 获取该栏目数据
                        fixedResource.forEach {
                            val channelCode = when(it){
                                yszy-> "yszy"
                                xszl-> "xszl"
                                jdsx-> "jdsx"
                                else -> ""
                            }
                            getContentListByChannelCode(id,channelCode,it)
                        }
                    }
                }
            })
    }


    /***
     * 通过栏目代码获取资讯 LiveData
     */
    val contentListByChannelCodeLiveData by lazy { MutableLiveData<HashMap<String,Any>>() }
    /**
     * 通过栏目代码获取内容列表
     */
    fun getContentListByChannelCode(id:String,code:String,name: String){
        mPresenter?.value?.loading=false

        VenuesRepository.venuesService.getContentLs("publishTime", ResourceType.CONTENT_TYPE_VENUE, id,pageSize = "5",channelCode = code)
            .excute(object : BaseObserver<ContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ContentBean>) {
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            val map = hashMapOf<String,Any>(
                                "title" to name,
                                "data" to it
                            )
                            contentListByChannelCodeLiveData.value = map
                        }                     }
                }
            })
    }


    /**
     * 社团列表 liveData
     */
    val societiesListLiveData = MutableLiveData<MutableList<SocietiesBean>>()
    /**
     * 获取社团列表
     */
    fun getSocietiesList(id: String){
        mPresenter?.value?.loading=false
        VenuesRepository
            .venuesService
            .getSocietiesList(id)
            .excute(object : BaseObserver<SocietiesBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SocietiesBean>) {
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            societiesListLiveData.value = it
                        }
                    }
                }
            })
    }
}