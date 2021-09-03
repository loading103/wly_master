package com.daqsoft.travelCultureModule.food.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.FoodDetailBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   FoodInfoViewModel
 * @Author      luoyi
 * @Time        2020/4/11 9:20
 */
class FoodInfoViewModel : BaseViewModel() {


    /**
     * 餐厅详情
     */
    val foodDetailLiveData: MutableLiveData<FoodDetailBean> = MutableLiveData()

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
}