package com.daqsoft.provider.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.PersonInfoTemplate
import com.daqsoft.provider.bean.StyleTemplate
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Description 布局模板请求
 * @ClassName   TemplateRepository
 * @Author      luoyi
 * @Time        2020/10/9 10:11
 */
class TemplateRepository {

    val service: TemplateService by lazy {
        RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(TemplateConvertFactory())
            .build(TemplateService::class.java)
    }

    companion object {
        val instance: TemplateRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TemplateRepository()
        }

    }

    /**
     * 通用模板service
     */
    interface TemplateService {

        /** 获取通用模板
         *@param moduleLocation    模块位置	string	【必填】
         *@param clientType    客户端类型	string	【必填】
         */
        @GET(TemplateApi.GET_TEMPLATE)
        fun getTemplate(@Query("moduleLocation") moduleLocation: String, @Query("clientType") clientType: String = TemplateApi.CLIENT_TYPE)
                : Observable<BaseResponse<StyleTemplate>>

        /**
         * 获取个人中心模板
         */
        @GET(TemplateApi.GET_PERSONAL_TEMPLATE)
        fun getPersonTemplate(@Query("clientType") clientType: String = TemplateApi.CLIENT_TYPE)
                : Observable<BaseResponse<PersonInfoTemplate>>

        /**
         * 获取底部菜单模板
         */
        @GET(TemplateApi.GET_BOTTOM_MENU)
        fun getIndexBottomMenuTemplate(@Query("clientType") clientType: String = TemplateApi.CLIENT_TYPE): Observable<BaseResponse<StyleTemplate>>

    }
}