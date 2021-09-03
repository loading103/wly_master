package com.daqsoft.baselib.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * @Author：      邓益千
 * @Create by：   2020/5/9 10:43
 * @Description： 城市获取->公共调用
 */
class CityRepository {

    val service: CityService = RetrofitFactory.Builder()
        .setBaseUrl(BaseApplication.baseUrl)
        .addInterceptor(HeaderInterceptor())
        .addConvertFactory(FaultToleranceConvertFactory.create())
        .build(CityService::class.java)

}

interface CityService{
    @GET("res/api/region/cityRegionModule")
    fun getLocations(): Observable<BaseResponse<LocationData>>
}