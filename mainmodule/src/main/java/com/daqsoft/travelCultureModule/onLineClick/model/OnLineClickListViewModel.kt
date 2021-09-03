package com.daqsoft.travelCultureModule.onLineClick.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.onLineClick.ONLINE_CLICK_CODE
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickBean
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickClassifyBean
import com.daqsoft.travelCultureModule.onLineClick.bean.Subset
import com.daqsoft.travelCultureModule.onLineClick.net.OnLineClickRepository

/**
 * desc :网红打卡viewModel
 * @author 江云仙
 * @date 2020/4/20 13:55
 */
class OnLineClickListViewModel :BaseViewModel(){
    var channelCode=""
    /**
     * 分页页码
     */
    var mCurrPage = 1
    /**
     * 获取页码大小
     */
    var mPageSize = 10
    /**
     * 获取网红打卡分类
     */
    var onLineClickClassifyBean = MutableLiveData<MutableList<Subset>>()
    /**
    *网红打卡列表
    */
    var onLineClickBean = MutableLiveData<MutableList<OnLineClickBean>>()
    fun getChannelSubset() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        var params: HashMap<String, Any> = hashMapOf()
        params["channelCode"] = ONLINE_CLICK_CODE
        OnLineClickRepository.service.getChannelSubset(params)
            .excute(object : BaseObserver<OnLineClickClassifyBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OnLineClickClassifyBean>) {
                    onLineClickClassifyBean.postValue(response.data?.subset)
                }
            })
    }
    /**
    *网红打卡列表
    */
    fun getOnLineClickList() {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        var params: HashMap<String, Any> = hashMapOf()
        params["channelCode"] = channelCode
        params["currPage"] = mCurrPage
        params["pageSize"] = mPageSize
        OnLineClickRepository.service.getOnLineClickList(params)
            .excute(object : BaseObserver<OnLineClickBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OnLineClickBean>) {
                    onLineClickBean.postValue(response.datas)
                }
            })
    }
}