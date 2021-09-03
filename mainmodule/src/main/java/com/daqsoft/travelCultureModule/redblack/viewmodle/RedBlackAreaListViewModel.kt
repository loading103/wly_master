package com.daqsoft.travelCultureModule.redblack.viewmodle

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBean
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBeanItem
import com.daqsoft.travelCultureModule.redblack.bean.RedBgBean

/**
 * @Description 红黑榜
 */
class RedBlackAreaListViewModel : BaseViewModel() {

    /**
     * 获取背景顶部图片
     */
    var bgBean = MutableLiveData<RedBgBean>()
    fun getBgUrlData() {
        mPresenter.value?.loading = false
        MainRepository.service.getRedBlackBgUrl("regionRank").excute(object : BaseObserver<RedBgBean>() {
            override fun onSuccess(response: BaseResponse<RedBgBean>) {
                bgBean.postValue(response.data)
            }
        })
    }

    /**
     * 获取地区数据
     */
    val pageManager = PageManager(10)
    var areadatas = MutableLiveData<MutableList<AreaListBeanItem>>()
    fun getAreaListData(region: String,showloading: Boolean) {
        mPresenter.value?.loading = showloading
        mPresenter.value?.isNeed=showloading
        MainRepository.service.getAreaListData(region,pageSize = pageManager.pageSize.toString(), currPage = pageManager.pageIndex.toString())
            .excute(object : BaseObserver<AreaListBeanItem>(mPresenter) {
                override fun onSuccess(response: BaseResponse<AreaListBeanItem>) {
                    areadatas.postValue(response.datas)
                }
            })
    }

}