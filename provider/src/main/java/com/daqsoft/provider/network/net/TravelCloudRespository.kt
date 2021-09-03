package com.daqsoft.provider.network.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.UploadBean
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * @Description
 * @ClassName   TravelCloudRespository
 * @Author      luoyi
 * @Time        2020/5/12 16:41
 */
class TravelCloudRespository {
    val ossService: OssService = RetrofitFactory.Builder()
        .setBaseUrl(BaseApplication.baseUrl)
        .addInterceptor(HeaderInterceptor())
        .addConvertFactory(FaultToleranceConvertFactory.create())
        .build(OssService::class.java)

    interface OssService {

        /**
         * 只有一个地址 ，单独写在这里了
         */
        @GET("config/ued/getUploadFileKey2")
        fun getOssKeys(): Observable<BaseResponse<String>>
    }
}