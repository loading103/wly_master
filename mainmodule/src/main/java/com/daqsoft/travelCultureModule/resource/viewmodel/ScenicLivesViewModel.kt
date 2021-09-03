package com.daqsoft.travelCultureModule.resource.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.Spots
import com.daqsoft.provider.bean.SpotsLiveBean
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.resource.bean.LiveListBean

/**
 * @Description
 * @ClassName   ScenicLivesViewModel
 * @Author      luoyi
 * @Time        2020/4/26 9:43
 */
class ScenicLivesViewModel : BaseViewModel() {
    /**
     * 景点直播列表
     */
    var spotLivesLiveData: MutableLiveData<MutableList<LiveListBean>> = MutableLiveData()
    var  scenicId:String=""
    /**
     * 获取景点直播数据
     */
    fun getSpotLives() {
//        MainRepository.service.getScenicSpotLives().excute(object : BaseObserver<LiveListBean>(mPresenter) {
//            override fun onSuccess(response: BaseResponse<LiveListBean>) {
//                spotLivesLiveData?.postValue(response.datas)
//            }
//
//            override fun onFailed(response: BaseResponse<LiveListBean>) {
//                mError.postValue(response)
//            }
//
//        })
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val param = HashMap<String, String>()
        param["scenicId"] = scenicId
        MainRepository.service.getScenicZb(param).excute(object : BaseObserver<LiveListBean>() {
            override fun onSuccess(response: BaseResponse<LiveListBean>) {
                spotLivesLiveData?.postValue(response.datas)
            }
            override fun onFailed(response: BaseResponse<LiveListBean>) {
                mError.postValue(response)
            }
        })
    }
}