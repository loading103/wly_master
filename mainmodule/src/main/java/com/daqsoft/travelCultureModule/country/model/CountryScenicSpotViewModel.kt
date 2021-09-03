package com.daqsoft.travelCultureModule.country.model


import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.travelCultureModule.country.net.CountryRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * 景观点详情ViewModel
 */
class CountryScenicSpotViewModel : BaseViewModel() {

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
    val scenicDetail = MutableLiveData<CountryDetailBean>()

    /**
     * 获取景点详情
     */
    fun getScenicSpotsDetail(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        CountryRepository.service.getApiRuralSpotsInfo(id).excute(object : BaseObserver<Spots>() {
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
     * @param id 景区id
     */
    fun getScenicBrandList(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getScenicSpotBrandList("10", id, ResourceType.CONTENT_TYPE_COUNTRY).excute(object : BaseObserver<HomeBranchBean>() {

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
       CountryRepository.service.getApiRuralInfo(id).excute(object : BaseObserver<CountryDetailBean>() {
            override fun onSuccess(response: BaseResponse<CountryDetailBean>) {
                scenicDetail.postValue(response.data)
            }
        })
    }
}