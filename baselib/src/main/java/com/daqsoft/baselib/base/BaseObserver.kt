package com.daqsoft.baselib.base

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.net.NetStatus
import com.daqsoft.baselib.utils.SPUtils
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * 基类观察者
 *
 * 用于网络请求，如RxJava+Retrofit进行网络请求时，接收发射源的观察者
 * 如无特殊处理，请统一使用该Observer进行网络数据回调处理
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/19
 * @since JDK 1.8
 */
abstract class BaseObserver<T> : Observer<BaseResponse<T>> {


    private var netStatus: NetStatus? = null
    private var mPresenter: MutableLiveData<NetStatus>? = null
    /**
     * 请求flag
     */
    private var mRequestFlag: Int = 0
    /**
     * 关键请求
     * true : 显示错误布局视图
     * false: 显示正常布局视图
     */
    private var importantRequest: Boolean? = null

    /**
     * 空构造函数，可不传任何变量
     */
    constructor()

    /**
     * @param mPresenter 网络状态变量 用于视图切换
     */
    constructor(mPresenter: MutableLiveData<NetStatus>) : this() {
        this.mPresenter = mPresenter
        this.netStatus = this.mPresenter?.value!!
    }

    /**
     * @param mPresenter 网络状态变量 用于视图切换
     * @param importantRequest 关键网络请求，决定是否显示错误视图
     */
    constructor(mPresenter: MutableLiveData<NetStatus>, importantRequest: Boolean) {
        this.mPresenter = mPresenter
        this.netStatus = this.mPresenter?.value!!
        this.importantRequest = importantRequest
    }

    /**
     * @param mPresenter 网络状态变量 用于视图切换
     * @param flag 请求标识（用于判断请求唯一标识）
     */
    constructor(mPresenter: MutableLiveData<NetStatus>, flag: Int) {
        this.mPresenter = mPresenter
        this.mRequestFlag = flag
        this.netStatus = this.mPresenter?.value!!
    }

    /**
     * 事件处理完成
     */
    override fun onComplete() {
        netStatus?.loading = false
        mPresenter?.postValue(netStatus)
    }

    override fun onSubscribe(d: Disposable) {
        mPresenter?.postValue(netStatus)
    }

    /**
     * 处理网络回调事件
     * code = 0 则进行成功回调
     * code 为其他值时显示后台返回的message字段
     */
    override fun onNext(t: BaseResponse<T>) {
        t.requestFlag = mRequestFlag
        if (t.code == 0) {
            onSuccess(t)
        } else if (t.code == 2 || t.code == 3) {
            // 跳转登录
            SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
            netStatus?.login = true
            netStatus?.errorMessage = t.message ?: ""
            mPresenter?.postValue(netStatus)
            onFailed(t)
        } else {
            if (t.message == "数据不存在") t.message = ""
            // 避免乱码
            if(t.message?.length!! >30){
                netStatus?.errorMessage =""
            }else{
                netStatus?.errorMessage = t.message ?: ""
            }
            mPresenter?.postValue(netStatus)
            onFailed(t)
        }
    }

    /**
     * 成功回调
     */
    abstract fun onSuccess(response: BaseResponse<T>)

    /**
     * 失败回调
     */
    open fun onFailed(response: BaseResponse<T>) {

    }


    /**
     * RxJava错误回调
     * 此方法对RxJava接收数据源的错误进行了处理，网络异常或json解析出错时会走此方法
     */
    override fun onError(e: Throwable) {
        netStatus?.loading = false
        netStatus?.error = importantRequest ?: true
        Timber.tag("NetError").e(e)
        // 网络错误统一回调处理
        var  baseResponse =BaseResponse<T>()
        when (e) {
            is JsonParseException,
            is JSONException,
            is JsonSyntaxException ->{
                netStatus?.errorMessage = "数据格式解析错误"
                baseResponse.message= "数据格式解析错误"
            }
            is ConnectException ->{
                netStatus?.errorMessage = "连接失败"
                baseResponse.message= "连接失败"
            }
            is TimeoutException ->{
                netStatus?.errorMessage = "连接超时"
                baseResponse.message= "连接超时"
            }
            is HttpException ->{
                netStatus?.errorMessage = "网络错误"
                baseResponse.message= "网络错误"
            }
            is SocketTimeoutException -> {
                netStatus?.errorMessage = "连接超时"
                baseResponse.message= "连接超时"
            }
            else -> {
//                netStatus?.errorMessage = "未知错误"
//                baseResponse.message= "未知错误"
                netStatus?.errorMessage = ""
                baseResponse.message= ""
            }
        }
        onFailed(baseResponse)
        mPresenter?.postValue(netStatus)
    }
}