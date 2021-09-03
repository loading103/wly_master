package com.daqsoft.baselib.net

import com.daqsoft.baselib.SiteInfoBean
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * @Author：      caihj
 * @Create by：   2020/6/10 10:43
 * @Description： 公共服务请求
 */
class CommonRepository {

    companion object{
        val service: CommonService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(CommonService::class.java)
    }

}

interface CommonService{

    /**
     * 站点详情
     */
    @GET(CommonApi.SITE_INFO)
    fun getSiteInfo(): Observable<BaseResponse<SiteInfoBean>>

}