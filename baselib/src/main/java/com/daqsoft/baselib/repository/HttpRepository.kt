package com.daqsoft.baselib.repository

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.repository.api.HttpService
import com.daqsoft.provider.network.RetrofitFactory
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 接口的仓库
 * @author 黄熙
 * @date 2019/7/25 0025
 * @version 1.0.0
 * @since JDK 1.8
 */
class HttpRepository {

    /**
     * 网络请求的对象
     */

    val httpService: HttpService = RetrofitFactory.Builder()
        .setBaseUrl(BaseApplication.baseUrl)
        .addInterceptor(HeaderInterceptor())
        .addConvertFactory(FaultToleranceConvertFactory.create())
        .build(HttpService::class.java)


    /**
     * 初始化
     */
    constructor() {
    }

    /**
     * APP版本检测
     *
     * @param appId   app唯一标识
     * @param method  方法AppVersion
     * @param token   daqsoft
     * @param appType 1
     * @param version 版本号
     * @return
     */
    fun checkVersion(@Path(value = "url", encoded = true) url: String,
                     @Query("AppId") appId: String, @Query("Method") method: String,
                     @Query("token") token: String, @Query("AppType") appType: String,
                     @Query("VersionCode") version: String) = httpService.checkVersion(url, appId, method, token, appType, version)

}