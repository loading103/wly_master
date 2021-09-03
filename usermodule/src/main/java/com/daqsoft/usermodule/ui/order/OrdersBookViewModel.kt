package com.daqsoft.usermodule.ui.order

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ActivityBookBean
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.HelathSetingBean
import com.daqsoft.provider.bean.OrderDetail
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.venues.VenuesRepository

/**
 * @Author：      邓益千
 * @Create by：   2020/7/9 19:07
 * @Description：
 */
class OrdersBookViewModel : BaseViewModel() {

    val ordersBook = MutableLiveData<MutableList<ActivityBookBean>>()

    /**订单详情*/
    val orderDetail = MutableLiveData<OrderDetail>()

    /**健康信息*/
    val helathInfo = MutableLiveData<HelathInfoBean>()

    /**核销结果*/
    val result = MutableLiveData<Boolean>()
    /**
     * 健康码设置信息
     */
    val healthSetingLd: MutableLiveData<HelathSetingBean> = MutableLiveData()

    /**预订列表*/
    fun getOrderBook(status: String, currPage: Int, orderType: String) {
        mPresenter.value?.loading = true
        val map = HashMap<String, Any>()
        if (status != "0") {
            map["status"] = status
        }
        map["pageSize"] = 10
        map["currPage"] = currPage
        UserRepository().userService.getOrderBook(map, orderType)
            .excute(object : BaseObserver<ActivityBookBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBookBean>) {
                    ordersBook.postValue(response.datas)
                }
            })
    }

    /**订单详情*/
    fun getOrderDetail(orderId: String) {
        val map = HashMap<String, Any>()
        UserRepository().userService.getOrderDetail(map, orderId)
            .excute(object : BaseObserver<OrderDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderDetail>) {
                    orderDetail.postValue(response.data)
                }
            })
    }

    /**核销详情*/
    fun getWriteOffDetail(orderId: String) {
        UserRepository().userService.getWriteOffDetail(orderId)
            .excute(object : BaseObserver<OrderDetail>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderDetail>) {
                    orderDetail.postValue(response.data)
                }
            })
    }

    /**获取健康信息*/
    fun getUserHealthInfo(orderId: String) {
        UserRepository().userService.getUserHealthInfo(orderId)
            .excute(object : BaseObserver<HelathInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HelathInfoBean>) {
                    helathInfo.postValue(response.data)
                }
            })
    }

    /**
     * 取消订单
     * @param orderCode 订单编号
     * @param canceNum 取消数量
     * @param operateIds 随行人数据id
     * @param operateExcuse 随行人取消方式
     * @param cancelNum 取消数量
     */
    fun postWriteOff(
        orderCode: String,
        writerOffNum: Int = 0,
        operateType: String = "CONTAIN",
        operateIds: String? = ""
    ) {

        var param: HashMap<String, String> = hashMapOf()


        mPresenter?.value?.loading = false
        param["code"] = orderCode
        param["operateType"] = operateType
        if (!operateIds.isNullOrEmpty()) {
            param["attachedIds"] = operateIds
        }
        if (writerOffNum > 0) {
            param["validNum"] = "$writerOffNum"
        }
        UserRepository().userService.postWriteOff(param)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    result.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    result.postValue(false)
                }
            })
    }

    /**取消*/
    fun postCancelOrder(code: String) {
        UserRepository().userService.postCancelOrder(code)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    result.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    result.postValue(false)
                }
            })
    }

    /**
     * 获取健康码配置信息
     */
    fun getHealthSetingInfo() {
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        VenuesRepository.venuesService.getHealthSetingInfo(siteId)
            .excute(object : BaseObserver<HelathSetingBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HelathSetingBean>) {
                    healthSetingLd.postValue(null)
                }
            })
    }

}