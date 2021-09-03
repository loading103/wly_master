package com.daqsoft.usermodule.ui.order.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.OrderRefundDetailBean
import com.daqsoft.provider.bean.QRCodeBean
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut

/**
 * @ClassName    OrderRefundDetailViewModel
 * @Description  购物详情ViewModel
 * @Author       yuxc
 * @CreateDate   2020/12/2
 */
class OrderRefundDetailViewModel : BaseViewModel() {

    /**
     * 购物退款详情
     */
    val orderRefundDetail by lazy { MutableLiveData<OrderRefundDetailBean>() }

    fun getOrderRefundDetail (refundId :String){
        ElectronicRepository.electronicService.getOrderRefundDetail(refundId)
            .excut(object : ElectronicObserver<OrderRefundDetailBean>() {
                override fun onSuccess(response: BaseResponse<OrderRefundDetailBean>) {
                    orderRefundDetail.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderRefundDetailBean>) {
                    orderRefundDetail.postValue(null)
                }
            })
    }

    /**
     * 二维码
     */
    val qRCodeBean by lazy { MutableLiveData<QRCodeBean>() }

    fun getTalentSubscribe(){
        ElectronicRepository.electronicService.getTalentSubscribe()
            .excut(object : ElectronicObserver<QRCodeBean>() {
                override fun onSuccess(response: BaseResponse<QRCodeBean>) {
                    qRCodeBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<QRCodeBean>) {
                    qRCodeBean.postValue(null)
                }
            })
    }
}