package com.daqsoft.travelCultureModule.redblack.viewmodle

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.redblack.bean.*

/**
 * @Description 红黑榜
 */
class RedBlackListViewModel : BaseViewModel() {
    /**
     * 获取背景顶部图片
     */
    var bgBean = MutableLiveData<RedBgBean>()
    fun getBgUrlData(type:String) {
        mPresenter.value?.loading = false
        MainRepository.service.getRedBlackBgUrl(type).excute(object : BaseObserver<RedBgBean>() {
            override fun onSuccess(response: BaseResponse<RedBgBean>) {
                bgBean.postValue(response.data)
            }
        })
    }

    /**
     * 获取地区数据
     */
    val pageManager = PageManager(10)
    var resoureListBean = MutableLiveData<MutableList<ResoureListBeanItem>>()
    fun getResoureListData(region: String,type :String,sortType: String,showloading: Boolean) {
        mPresenter.value?.loading = showloading
        mPresenter.value?.isNeed=showloading
        MainRepository.service.getReSoureListData(region,type,sortType,currPage = pageManager.pageIndex.toString(),pageSize = pageManager.pageSize.toString() )
            .excute(object : BaseObserver<ResoureListBeanItem>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ResoureListBeanItem>) {
                    resoureListBean.postValue(response.datas)
                }
            })
    }

}