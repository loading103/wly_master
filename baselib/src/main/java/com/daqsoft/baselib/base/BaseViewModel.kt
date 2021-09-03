package com.daqsoft.baselib.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daqsoft.baselib.net.NetStatus

/**
 * ViewModel的基类
 *
 * 建议所有自己写的ViewModel继承该BaseViewModel而非ViewModel
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/19
 * @since JDK 1.8
 */
open class BaseViewModel : ViewModel() {
    /**
     * 网络状态变量
     */
    var mPresenter = MutableLiveData<NetStatus>(NetStatus())
    /**
     * 主要网络是否请求错误
     * true ：表示错误
     * false ： 正常
     *
     * 用法，在重要网络请求成功后将其置为false,并在网路请求的时候作为参数传入BaseObserver中
     */
    var importRequestError: Boolean = true
    /**
     * 用于在Activity中弹出的toast
     */
    val toast = MutableLiveData<String>()
    /**
     * 是否销毁当前Activity
     * true : 销毁
     */
    val finish = MutableLiveData<Boolean>()

    val mError = MutableLiveData<BaseResponse<*>>()

}