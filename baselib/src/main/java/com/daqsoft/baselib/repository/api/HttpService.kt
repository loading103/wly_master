package com.daqsoft.baselib.repository.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 文化旅游云模块网络仓库
 * @author 黄熙
 * @date 2019/7/23 0023
 * @version 1.0.0
 * @since JDK 1.8
 */
interface HttpService {
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
    @GET("{url}")
    fun checkVersion(
            @Path(value = "url", encoded = true) url: String,
            @Query("AppId") appId: String,
            @Query("Method") method: String,
            @Query("token") token: String,
            @Query("AppType") appType: String,
            @Query("VersionCode") version: String
    ): Call<ResponseBody>


}