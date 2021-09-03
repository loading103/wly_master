package com.daqsoft.provider.network.net

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.NetStatus
import com.daqsoft.baselib.utils.SPUtils
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * 小电商的观察者
 *
 * 用于网络请求，如RxJava+Retrofit进行网络请求时，接收发射源的观察者
 * 如无特殊处理，请统一使用该Observer进行网络数据回调处理
 *
 * @author PuHua
 * @version 1.0.0
 */

fun <T> Observable<BaseResponse<T>>.excut(baseObserver: ElectronicObserver<T>) =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(baseObserver)


abstract class ElectronicObserver<T> : Observer<BaseResponse<T>> {
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

    private var netStatus: NetStatus? = null
    private var mPresenter: MutableLiveData<NetStatus>? = null

    /**
     * 关键请求
     * true : 显示错误布局视图
     * false: 显示正常布局视图
     */
    private var importantRequest: Boolean? = null

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
        if (t.code == 1) {
            onSuccess(t)
        } else if (t.code == 2) {
            // 跳转登录
            SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
            netStatus?.login = true
            netStatus?.errorMessage = t.msg ?: ""
            mPresenter?.postValue(netStatus)
            onFailed(t)
        } else {
            //netStatus?.errorMessage = t.msg ?: ""
            if (t.message == "数据不存在") t.message = ""
            netStatus?.errorMessage = t.message ?: ""
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
        netStatus?.error = importantRequest ?: false
        Timber.tag("NetError").e(e)
        // 网络错误统一回调处理
        when (e) {
            is JsonParseException,
            is JSONException,
            is JsonSyntaxException -> netStatus?.errorMessage = "数据格式解析错误"
            is ConnectException -> netStatus?.errorMessage = "连接失败"
            is TimeoutException -> netStatus?.errorMessage = "连接超时"
            is HttpException -> netStatus?.errorMessage = "网络错误"
            is SocketTimeoutException -> netStatus?.errorMessage = "连接超时"
//            else -> netStatus?.errorMessage = "未知错误"
            else -> netStatus?.errorMessage = ""
        }
        mPresenter?.postValue(netStatus)
    }
}