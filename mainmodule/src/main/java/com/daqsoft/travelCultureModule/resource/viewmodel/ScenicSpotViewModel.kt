package com.daqsoft.travelCultureModule.resource.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.bean.ScenicDetailBean
import com.daqsoft.provider.bean.Spots
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 景点详情
 * @ClassName   ScenicSpotViewModel
 * @Author      luoyi
 * @Time        2020/4/3 14:16
 */
class ScenicSpotViewModel : BaseViewModel() {

    /**
     * 景点详情
     */
    val spot = MutableLiveData<Spots>()
    /**
     * 相关故事列表
     */
    val storyList = MutableLiveData<MutableList<StoreBean>>()
    /**
     * 品牌
     */
    val scenicBrandListLiveData = MutableLiveData<MutableList<HomeBranchBean>>()
    /**
     * 景区详情
     */
    val scenicDetail = MutableLiveData<ScenicDetailBean>()
    var  storyNumber:String ?=""
    /**
     * 获取景点详情
     */
    fun getScenicSpotsDetail(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getScenicSpotsDetail(id).excute(object : BaseObserver<Spots>() {
            override fun onSuccess(response: BaseResponse<Spots>) {

                spot.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<Spots>) {
                mError.postValue(response)
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
                storyNumber = response.page?.total?.toString()
                storyList.postValue(response.datas)
            }
        })
    }

    /**
     * @param id 景区id
     */
    fun getScenicBrandList(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getScenicSpotBrandList("10", id, ResourceType.CONTENT_TYPE_SCENERY).excute(object : BaseObserver<HomeBranchBean>() {

            override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                scenicBrandListLiveData.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<HomeBranchBean>) {
                scenicBrandListLiveData.postValue(null)
            }
        })
    }

    /**
     * 获取景区详情
     */
    fun getScenicDetail(id: String) {
        mPresenter?.value?.loading = false
        MainRepository.service.getScenicDetail(id).excute(object : BaseObserver<ScenicDetailBean>() {
            override fun onSuccess(response: BaseResponse<ScenicDetailBean>) {
                scenicDetail.postValue(response.data)
            }
        })
    }
}