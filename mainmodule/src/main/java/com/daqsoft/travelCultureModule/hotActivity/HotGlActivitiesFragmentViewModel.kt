package com.daqsoft.travelCultureModule.hotActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.ActivityCalenderBean
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.travelCultureModule.net.MainRepository
import java.lang.StringBuilder

/**
 * @Description 热门活动的viewModel
 * @ClassName   HotActivitiesFragmentViewModel
 * @Author      PuHua
 * @Time        2019/12/25 10:42
 */
class HotGlActivitiesFragmentViewModel : BaseViewModel() {
    /**
     * 当前选中的分类
     */
    var currentClassifyId:String? = ""

    /**
     * 活动分类列表
     */
    val activityClassifies = MutableLiveData<MutableList<Classify>>()
    /**
     * 活动列表
     */
    val activities = MutableLiveData<MutableList<ActivityBean>>()
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    val collectLiveData = MutableLiveData<Int>()

    val canceCollectLiveData = MutableLiveData<Int>()

    /**
     * 选中的地区
     */
    var currentArea = ""
    /**
     * 选中的排序方式
     */
    var currentSort = ""
    /**
     * 选中的的活动方式
     */
    var currentMethod = ""
    /**
     * 选中的月份
     */
    var currentMonth = ""
    /**
     * 当前经纬度
     */
    var currentLat = 0.0

    var currentLon = 0.0
    /**
     * 活动分页页码
     */
    var mActivityCurrPage = 1
    /**
     * 获取页码大小
     */
    var mActivityPageSize = 10

    var startTime: String? = ""

    var endTime: String? = ""
    /**
     *  搜索关键字
     */
    var mKeyWords: String? = ""

    var areaSiteSwitch: String? = ""
    /**
     * 活动顶部广高
     */
    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()
    /**
     * 去到地图模式
     */
    val gotoMap = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ACTIVITY_MAP)
                .navigation()
        }

    }

    /**
     * 获取活动分类列表
     */
    fun getActivityClassify() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        MainRepository.service.getActivityClassify()
            .excute(object : BaseObserver<Classify>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Classify>) {
                    activityClassifies.postValue(response.datas)
                }
            })
    }
    /**
     * 获取景区顶部广告
     */
    fun getActivityTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.ACTIVITY_INDEX_LIST_TOP_ADV)
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
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        MainRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }


    /**
     * 获取活动列表
     */
    fun getActivityList() {
        mPresenter?.value?.loading = false
        if (mActivityCurrPage == 1) {
            mPresenter?.value?.loading = false
        } else {
            mPresenter?.value?.isNeedRecyleView = false
        }
        val param = HashMap<String, String>()
        // orderType  为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
        param["orderType"] = currentSort
        // 当时距离优先时需要加入自己的经纬度
        if (currentSort == "2") {
            param["latitude"] = currentLat.toString()
            param["longitude"] = currentLon.toString()
        }

        if (currentArea != "") {
            param["region"] = currentArea
        }

        if (currentMethod != "") {
            param["methods"] = currentMethod
        }

        if (currentMonth != "") {
            param["monthValue"] = currentMonth
        }
        param["currPage"] = mActivityCurrPage.toString()
        param["pageSize"] = mActivityPageSize.toString()
        if (!startTime.isNullOrEmpty()) {
            param["startTime"] = startTime!!
        }
        if (!endTime.isNullOrEmpty()) {
            param["endTime"] = endTime!!
        }


        if (!mKeyWords.isNullOrEmpty() &&! mKeyWords!!.isNotBlank()) {
            param["keyword"] = mKeyWords!!
        }
        // 活动类型id
        if (!currentClassifyId.isNullOrEmpty()) {
            param["classifyId"] = currentClassifyId!!
        }
        if (!areaSiteSwitch.isNullOrEmpty()) {
//            param["areaSiteSwitch"] = areaSiteSwitch!!
        }

        MainRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    mError.postValue(response)
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
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
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

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                }
            })
    }
}