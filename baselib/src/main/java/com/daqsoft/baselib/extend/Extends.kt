package com.daqsoft.baselib.extend

import android.content.res.Resources
import android.util.TypedValue
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

/**
 * 常用的一些Kotlin扩展
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/19
 * @since JDK 1.8
 */

/**
 * 统一处理数据解密
 */
inline fun <reified T> Observable<BaseResponse<String>>.excuteDecrypt(baseObserver:
                                                                      BaseObserver<T>) =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Function<BaseResponse<String>, BaseResponse<T>> {
                    val gson = Gson()
                    val decryptData = BaseResponse<T>()
                    decryptData.responseTime = it.responseTime
                    decryptData.page = it.page
                    decryptData.code = it.code
                    if (it.data != null) {
                        val data = gson.fromJson<T>(AESOperator.decrypt(it.data as String, AESOperator.KM_), object : TypeToken<T>() {}.type)
                        decryptData.decryptData = data
                    } else if (it.datas != null) {
                        val datas = Utils.jsonStringToList(AESOperator.decrypt(it.datas as
                                String, AESOperator.KM_), T::class.java)
                        decryptData.decryptDatas = datas
                    }
                    return@Function decryptData
                })
                .subscribe(baseObserver)


fun <T> Observable<BaseResponse<T>>.excute(baseObserver: BaseObserver<T>) =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseObserver)

fun Observable<ResponseBody>.excute(observer: Observer<ResponseBody>) =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer)


/**
 * 将 px 值转换成 dp 值
 */
val Int.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
