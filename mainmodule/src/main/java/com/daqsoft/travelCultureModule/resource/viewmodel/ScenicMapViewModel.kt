package com.daqsoft.travelCultureModule.resource.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Classify
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 活动地图模式的viewModel
 * @ClassName   HotActivitiesMapViewModel
 * @Author      PuHua
 * @Time        2020/1/7 14:42
 */
class ScenicMapViewModel:BaseViewModel() {

    /**
     * 当前选中的分类
     */
    var currentClassifyId = ""

    /**
     * 活动分类列表
     */
    val activityClassifies = MutableLiveData<MutableList<Classify>>()
    /**
     * 活动列表
     */
    val activities = MutableLiveData<MutableList<ActivityBean>>()


    /**
     * 获取活动分类列表
     */
    fun getActivityClassify() {
        mPresenter.value?.loading = true
        MainRepository.service.getActivityClassify().excute(object : BaseObserver<Classify>() {
            override fun onSuccess(response: BaseResponse<Classify>) {
                activityClassifies.postValue(response.datas)
            }
        })
    }

    /**
     * 获取活动列表
     */
    fun getScenicList(latitude:String,longitude:String) {
        val param = HashMap<String, String>()
        // orderType  为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
        param["orderType"] = "2"
        param["latitude"] =latitude
        param["longitude"] =longitude

        // 活动类型id
        if (currentClassifyId != "")
            param["classifyId"] = currentClassifyId
        mPresenter.value?.loading = true
        MainRepository.service.getActivityList(param).excute(object : BaseObserver<ActivityBean>() {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {
                activities.postValue(response.datas)
            }
        })
    }
}