package com.daqsoft.travelCultureModule.onLineClick.net

import com.daqsoft.baselib.base.BaseApplication
import io.reactivex.Observable
import retrofit2.http.QueryMap
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickBean
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickClassifyBean
import retrofit2.http.GET

/**
 * desc :网红打卡
 * @author 江云仙
 * @date 2020/4/20 13:43
 */
class OnLineClickRepository {
    companion object {
        val service: OnLineClickInfo = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(OnLineClickInfo::class.java)
//        val electronicService: CountryInfo = RetrofitFactory.Builder()
//            .setBaseUrl(BaseApplication.electronicUrl)
//            .addInterceptor(HeaderInterceptor())
//            .addConvertFactory(FaultToleranceConvertFactory.create())
//            .build(CountryInfo::class.java)
    }

}

interface OnLineClickInfo {
    /**
     *网红打卡分类
     * @param map
     */
    @GET(OnLineApi.ONLINE_CLICK_CLASSIFY)
    fun getChannelSubset(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<OnLineClickClassifyBean>>
    /**
     *网红打卡列表
     * @param map
     */
    @GET(OnLineApi.ONLINE_CLICK_LIST)
    fun getOnLineClickList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<OnLineClickBean>>
}
