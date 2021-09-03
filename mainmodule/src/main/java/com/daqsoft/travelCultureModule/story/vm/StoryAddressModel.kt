package com.daqsoft.travelCultureModule.story.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ItemAddressBean

/**
 * @Description 时光的添加地点页面ViewModel
 * @ClassName   StoryAddressActivity
 * @Author      HuangXi
 * @Time        2020/2/19 13:47
 */
class StoryAddressModel : BaseViewModel() {

    /**
     * 数据集
     */
    val datas by lazy { MutableLiveData<BaseResponse<ItemAddressBean>>() }


    /**
     * 获取添加地点（搜索周边）
     * @param pageSize 页面大小
     * @param keyword 关键字
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离
     * @param currPage 当前页
     */
    fun getSearchAround(pageSize: Int, keyword: String, longitude: Double, latitude: Double, distance: Int, currPage: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        HomeRepository.service.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            .excute(object : BaseObserver<ItemAddressBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ItemAddressBean>) {
                    datas.postValue(response)
                }

            })
    }


}