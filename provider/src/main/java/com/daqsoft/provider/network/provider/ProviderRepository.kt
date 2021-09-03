package com.daqsoft.provider.network.provider

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.ResourceOrderBean
import com.daqsoft.provider.bean.TimeAppointBean
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.home.HomeService
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Description
 * @ClassName   ProviderRepository
 * @Author      luoyi
 * @Time        2020/5/26 13:51
 */
class ProviderRepository {

    companion object {
        val service: ProviderService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(ProviderService::class.java)

        /**
         * 景区下单列表
         */
        const val GET_RESOURCE_ORDER_LIST = "res/api/external/resourceOrderList"

        /**
         * 获取授权码
         */
        const val GET_RESOURCE_IDENTITIY = "act/api/external/getIdentity"
        /**
         * 分时预约地址
         */
        const val GET_RESOURCE_TIME_LIST = "res/api/venue/orderTimeList"
    }

    interface ProviderService {
        /**
         * 获取景区订单列表
         * @param resourceId 资源id
         * @param resourceType 资源类型
         */
        @GET(GET_RESOURCE_ORDER_LIST)
        fun getResourceOrderList(
            @Query("resourceId") resourceId: String, @Query("resourceType") resourceType: String,
            @Query("pageSize") pageSize: String = "2"
        ):
                Observable<BaseResponse<ResourceOrderBean>>

        /**
         * 获取下单授权码
         */
        @GET(GET_RESOURCE_IDENTITIY)
        fun getResourceIdentity(): Observable<BaseResponse<String>>

        /**
         * 获取分时预约资源数据
         */
        @GET(GET_RESOURCE_TIME_LIST)
        fun getResourceTimeList(
            @Query("resourceType") resourceType: String,
            @Query("startDate") startDate: String,
            @Query("dayNum") dayNum: String,
            @Query("resourceId") resourceId: String
        ): Observable<BaseResponse<TimeAppointBean>>
    }
}