package com.daqsoft.provider.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * @Description 网络请求
 * @ClassName   ElectronicRetrofitFactory
 * @Author      PuHua
 * @Time        2019/11/21 13:58
 */
class RetrofitFactory {

    var builder: Builder? = null


    companion object {
        // RetrofitFactory工厂单例
        lateinit var instance: RetrofitFactory
    }

    class Builder {

        /**
         * 根地址
         */
        var baseUrl: String? = "http://www.baidu.com/"
        /**
         * OkHttpClient
         */
        var okHttpClient: OkHttpClient? = null
        /**
         * okHttpClient.builder
         */
        var okBuilder: OkHttpClient.Builder? = null
        /**
         * Retrofit.builder
         */
        var retrofitBuilder: Retrofit.Builder? = null
        /**
         * timeOut 连接超时时间 单位 秒
         */
        var timeOut: Long = 20
        /**
         * 读取超时时间 单位 秒
         */
        var readTimeOut: Long = 20
        /**
         * retrofit
         */
        var retrofit: Retrofit? = null
//        /**
//         * 数据解析工厂
//         */
//        var factory:Converter.Factory? =null

        /**
         * OkHttpClient.Builder的初始化放在工厂builder的初始化里面
         */
        init {

            okBuilder = OkHttpClient.Builder()

            retrofitBuilder = Retrofit.Builder()

        }

        /**
         * 设置数据解析工厂
         */
        fun addConvertFactory(factory: Converter.Factory): Builder {
            this.retrofitBuilder!!.addConverterFactory(factory)
            return this
        }

        /**
         * 设置拦截器
         * 注意室添加再builder里面的
         */
        fun addInterceptor(interceptor: Interceptor): Builder {
            okBuilder!!.addInterceptor(interceptor)
            return this
        }

        /**
         * 设置根地址
         */
        fun setBaseUrl(url: String): Builder {
            this.baseUrl = url
            return this
        }

        /**
         * 创建Api Service
         * @param service 待创建的apiService类型
         */
        fun <T> build(service: Class<T>): T {
            // 初始化okHttpClient
            okHttpClient = okBuilder!!
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .build()
            // 初始化retrofit
            retrofit = retrofitBuilder!!
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
            // 返回Api Service
            return retrofit!!.create(service)
        }
    }

}