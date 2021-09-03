package com.daqsoft.legacyModule.home

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.bean.LegacyStoryTagListBean
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.home.bean.HomeAdInfoBean
import com.daqsoft.legacyModule.home.bean.HomeTopImgBean
import com.daqsoft.legacyModule.home.bean.LegacyFoodBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 * @des    品非遗首页 ViewModel
 * @class  LegacyHomeViewModel
 * @author Wongxd
 * @date   2020-4-20  19:50
 */
internal class LegacyHomeViewModel : BaseViewModel() {

    val topImgData = MutableLiveData<HomeTopImgBean>()

    fun getTopImgData() {
        LegacyRepository.service.getHomeTopImg()
            .excute(object : BaseObserver<HomeTopImgBean>() {
                override fun onSuccess(response: BaseResponse<HomeTopImgBean>) {
                    topImgData.postValue(response.data)
                }
            })
    }


    val homeAdData = MutableLiveData<HomeAdInfoBean>()

    fun getHomeAdInfo() {
        LegacyRepository.service.getHomeAd("MICRO_SITE", "app_intangible_center_adv")
            .excute(object : BaseObserver<HomeAdInfoBean>() {
                override fun onSuccess(response: BaseResponse<HomeAdInfoBean>) {
                    homeAdData.postValue(response.data)
                }
            })
    }


    val homeDiscoverList = MutableLiveData<List<LegacyHeritageItemListBean>>()

    fun getHomeDiscoverList() {
        LegacyRepository.service.getHeritageItemList("1", "3", "", "", "", "", "", "")
            .excute(object : BaseObserver<LegacyHeritageItemListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritageItemListBean>) {
                    homeDiscoverList.postValue(response.datas)
                }
            })
    }


    val homeStoryTagList = MutableLiveData<List<LegacyStoryTagListBean>>()

    fun getHomeStoryTagList() {
        LegacyRepository.service.getStoryHotTagList()
            .excute(object : BaseObserver<LegacyStoryTagListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryTagListBean>) {
                    homeStoryTagList.postValue(response.datas)
                }
            })
    }


    val homeStoryList = MutableLiveData<List<LegacyStoryListBean>>()

    fun getHomeStoryList() {
        LegacyRepository.service.getStoryList(pageSize = 10, currPage = 1)
            .excute(object : BaseObserver<LegacyStoryListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    homeStoryList.postValue(response.datas)
                }
            })
    }

    val homeFoodList = MutableLiveData<List<LegacyFoodBean>>()

    fun getHomeFoodList(){
        LegacyRepository.service.getFoodList()
            .excute(object :BaseObserver<LegacyFoodBean>(){
                override fun onSuccess(response: BaseResponse<LegacyFoodBean>) {
                    homeFoodList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<LegacyFoodBean>) {
                    super.onFailed(response)
                }

            })
    }


    val topicList = MutableLiveData<List<HomeTopicBean>>()

    fun getHomeTopic(){
        HomeRepository.service.getTopicList("10","非遗").excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                topicList.postValue(response.datas)
            }
        })
    }

    val watchStoryList = MutableLiveData<List<LegacyWatchStoryListBean>>()

    fun getWatchStory(){
        LegacyRepository.service.getWatchStory().excute(object :BaseObserver<LegacyWatchStoryListBean>(){
            override fun onSuccess(response: BaseResponse<LegacyWatchStoryListBean>) {
                watchStoryList.postValue(response.datas)
            }

        })
    }
    /**
     * 活动分类列表
     */
    val activityClassifies = MutableLiveData<MutableList<Classify>>()

    /**
     * 获取活动分类列表
     */
    fun getActivityClassify() {
        mPresenter.value?.loading = true
        HomeRepository.service.getActivityClassify()
            .excute(object : BaseObserver<Classify>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Classify>) {
                    activityClassifies.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<Classify>) {
                    activityClassifies.postValue(null)
                }
            })
    }


    /**
     * 非议故事 新
     */
    val intangibleHeritageStoryLivedData = MutableLiveData<List<HomeStoryBean>>()
    fun getIntangibleHeritageStory() {
        LegacyRepository.service.getStoryListNew()
            .excute(object : BaseObserver<HomeStoryBean>() {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    intangibleHeritageStoryLivedData.postValue(response.datas)
                }
            })
    }
}