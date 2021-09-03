package com.daqsoft.usermodule.ui.order.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.OrderRefundBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut

/**
 * @ClassName    OrderRefundListViewModel
 * @Description  购物退款ViewModel
 * @Author       yuxc
 * @CreateDate   2020/12/1
 */
class OrderRefundListViewModel : BaseViewModel() {

    /**
     * 购物退款列表
     */
    val orderRefundList by lazy { MutableLiveData<OrderRefundBean>() }

    fun getOrderRefundList(currPage: String) {
        mPresenter.value?.loading = false
        ElectronicRepository.electronicService.getOrderRefund("10", currPage)
            .excut(object : ElectronicObserver<OrderRefundBean>() {
                override fun onSuccess(response: BaseResponse<OrderRefundBean>) {
                    orderRefundList.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderRefundBean>) {
                    orderRefundList.postValue(null)
                }
            })
    }
}