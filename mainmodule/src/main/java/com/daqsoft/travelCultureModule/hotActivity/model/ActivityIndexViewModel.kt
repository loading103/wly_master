package com.daqsoft.travelCultureModule.hotActivity.model

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.ActivityCollection
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.XJActivityCollectionActivity
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.XJActivityCollectionHome
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.net.MainRepository
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   ServiceIndexViewModel
 * @Author      luoyi
 * @Time        2020/6/9 9:42
 */
class ActivityIndexViewModel : BaseViewModel() {

    /**
     * 推荐类型数据
     */
    var recommentClassifyLd: MutableLiveData<MutableList<Classify>> = MutableLiveData()
    /**
     * 推荐活动列表
     */
    var recommentActivitiesLd: MutableLiveData<MutableList<ActivityBean>> = MutableLiveData()
    /**
     * 日历活动列表
     */
    var calenderActivitiesLd: MutableLiveData<MutableList<ActivityBean>> = MutableLiveData()
    /**
     * 精彩瞬间栏目
     */
    var activitySubSetLd: MutableLiveData<SubChannelBean> = MutableLiveData()
    /**
     * 活动概览数据
     */
    var activityOverViewLd: MutableLiveData<ActivityOverView> = MutableLiveData()
    /**
     * 活动顶部广高
     */
    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()
    /**
     * 收藏
     */
    val collectLiveData = MutableLiveData<Int>()
    /**
     * 取消收藏
     */
    val canceCollectLiveData = MutableLiveData<Int>()

    /**
     * 获取推荐活动类型
     */
    fun getRecommentClassify() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getActivityClassify("1").excute(object : BaseObserver<Classify>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Classify>) {
                recommentClassifyLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<Classify>) {
                recommentClassifyLd.postValue(null)
            }

        })
    }

    /**
     * 获取推荐活动列表
     */
    fun getRecommentActivities(classifyId: String) {
        var params: HashMap<String, String> = HashMap()
        if (!classifyId.isNullOrEmpty()) {
            params["classifyId"] = classifyId
        } else {
            params["classifyId"] = ""
        }
        params["currPage"] = "1"
        params["pageSize"] = "3"
        params["orderType"] = "1"
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getActivityList(params).excute(object : BaseObserver<ActivityBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {
                recommentActivitiesLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ActivityBean>) {
                recommentActivitiesLd.postValue(null)
            }

        })
    }

    /**
     * 活动日历
     * @param startime 开始时间
     * @param endTime 结束时间
     */
    fun getActivitiesBycalendar(startime: String, endTime: String,pageSize:String = "3",currPage:String = "1") {
        var params: HashMap<String, String> = HashMap()
        if (!startime.isNullOrEmpty()) {
            params["startTime"] = startime
        }
        if (!endTime.isNullOrEmpty()) {
            params["endTime"] = endTime
        }
        params["pageSize"] = pageSize
        params["currPage"] = currPage
        MainRepository.service.getActivityList(params).excute(object : BaseObserver<ActivityBean>() {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {
                calenderActivitiesLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ActivityBean>) {
                calenderActivitiesLd.postValue(null)
            }

        })
    }

    /**
     * 活动概览
     */
    fun getActivitiesOverView(startime: String, endTime: String) {
        var params: HashMap<String, String> = HashMap()
        if (!startime.isNullOrEmpty()) {
            params["startTime"] = startime
        }
        if (!endTime.isNullOrEmpty()) {
            params["endTime"] = endTime
        }
        params["totalCountFlag"] = "true"
        MainRepository.service.getActivityCalendar(params).excute(object : BaseObserver<ActivityOverView>() {
            override fun onSuccess(response: BaseResponse<ActivityOverView>) {
                activityOverViewLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<ActivityOverView>) {
                activityOverViewLd.postValue(null)
            }

        })
    }

    /**
     *获取精彩瞬间栏目信息
     */
    fun getActivitySubSet() {
        MainRepository.service.getActivitySubset("jcsj").excute(object : BaseObserver<SubChannelBean>() {
            override fun onSuccess(response: BaseResponse<SubChannelBean>) {
                activitySubSetLd.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<SubChannelBean>) {
                activitySubSetLd.postValue(null)
            }

        })
    }

    /**
     * 获取景区顶部广告
     */
    fun getActivityTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.ACTIVITY_LIST_TOP_ADV)
            .excute(object : BaseObserver<HomeAd>() {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(null)
                }
            })
    }

    /**
     * 获取咨询列表
     */
    var zixunList = MutableLiveData<MutableList<ClubZixunBean>>()

    fun getZixunList(
        channelCode: String
    ) {
        mPresenter.value?.loading = false
        MainRepository.service.getClubZixunList(
            "", "", "",
            "3", "1", "", channelCode, ""
        ).excute(object : BaseObserver<ClubZixunBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(null)
            }
        })
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏成功~")
                    collectLiveData.postValue(position)
                    EventBus.getDefault().post(XJActivityCollectionActivity(true))
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
    fun collectionCancel(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_ACTIVITY
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏成功~")
                    canceCollectLiveData.postValue(position)
                    EventBus.getDefault().post(XJActivityCollectionActivity(false))
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }
}