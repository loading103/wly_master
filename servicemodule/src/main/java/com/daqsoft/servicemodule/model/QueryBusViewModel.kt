package com.daqsoft.servicemodule.model

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.bean.BusAddressBean
import com.daqsoft.servicemodule.bean.InputTipsBean
import com.daqsoft.servicemodule.bean.NearBusBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :公交查询viewModel
 * @author 江云仙
 * @date 2020/4/3 15:37
 */
class QueryBusViewModel : BaseViewModel() {
    /**
     * 当前地址
     */
    var startAddress = ""
    var endAddress = ""
    var isStartJump = false
    var isEndJump = false
    var nextAddress = ""
    var keyword=""
    /**
     *公交路线列表数据
     */
    var result = MutableLiveData<MutableList<NearBusBean>>()
    var startAddressDatas = MutableLiveData<MutableList<BusAddressBean>>()
    var endAddressDatas = MutableLiveData<MutableList<BusAddressBean>>()
    /**
     * 根据地址获取经纬度
     */
    fun getBusAddress(type: String, address: String) {
        mPresenter?.value?.loading = true
        val map = HashMap<String, Any>()
        // 经纬度
        map["address"] = address
        ServiceRepository().service.getBusAddress(map)
            .excute(object : BaseObserver<BusAddressBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BusAddressBean>) {
                    if (type == "start") {
                        isStartJump = true
                        startAddressDatas.postValue(response.datas)
                        getBusAddress("end",nextAddress)
                    } else {
                        isEndJump = true
                        endAddressDatas.postValue(response.datas)
                        if (startAddressDatas.value != null  && isStartJump && isEndJump) {
                            isStartJump = false
                            isEndJump = false
                            if (startAddressDatas.value!!.size==0){
                                ToastUtils.showMessage("未找到起点")
                                return
                            }
                            if (response.datas!!.size==0){
                                ToastUtils.showMessage("未找到终点")
                                return
                            }
                            ARouter.getInstance()
                                .build(ARouterPath.ServiceModule.SERVICE_BUS_LINE_ACTIVITY)
                                .withParcelable("startAddressBean", if (startAddressDatas.value!!.size>0){startAddressDatas.value!![0]}else{null})
                                .withString("endAddress", endAddress)
                                .withString("startAddress", startAddress)
                                .withParcelable("endAddressBean", if (response.datas!!.size>0){response.datas!![0]}else{null})
                                .navigation()
                        }
                    }



                }

            })
    }
    /**
     * 用户输入提示
     */
    var inputTips = MutableLiveData<MutableList<InputTipsBean>>()
    fun getInputTips() {
        val map = HashMap<String, String>()
        // 关键字
        map["keyword"] = keyword
        ServiceRepository().service.getInputTips(map)
            .excute(object : BaseObserver<InputTipsBean>() {
                override fun onSuccess(response: BaseResponse<InputTipsBean>) {
                    inputTips.postValue(response.datas)
                }

            })
    }
    var startInputTips = MutableLiveData<MutableList<InputTipsBean>>()
    fun getStartInputTips() {
        val map = HashMap<String, String>()
        // 关键字
        map["keyword"] = keyword
        ServiceRepository().service.getInputTips(map)
            .excute(object : BaseObserver<InputTipsBean>() {
                override fun onSuccess(response: BaseResponse<InputTipsBean>) {
                    startInputTips.postValue(response.datas)
                }

            })
    }
}