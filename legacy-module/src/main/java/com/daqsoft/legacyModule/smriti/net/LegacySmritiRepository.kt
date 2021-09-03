package com.daqsoft.legacyModule.smriti.net
import com.daqsoft.baselib.base.BaseApplication
import io.reactivex.Observable
import retrofit2.http.QueryMap
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.legacyModule.smriti.bean.*
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.RetrofitFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * desc :非遗传承
 * @author 江云仙
 * @date 2020/4/21 16:19
 */
class LegacySmritiRepository {
    companion object {
        val service: LegacySmritiInfo = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(LegacySmritiInfo::class.java)
    }

}

interface LegacySmritiInfo {
    /**
     *非遗推荐列表
     * @param map
     */
    @GET(LegacySmritiApi.LEGACY_SMRITI_LIST)
    fun getRecommendList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LegacyRecommendBean>>
    /**
     *非遗推荐列表
     * @param map
     */
    @GET(LegacySmritiApi.LEGACY_BEHALF_LIST)
    fun getBehalfList(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LegacyBehalfBean>>
    /**
     *搜索分类
     * @param map
     */
    @GET(LegacySmritiApi.SEARCH_TYPE)
    fun getSearchType(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<TypeBean>>

    /**
     * 收藏
     */
    @POST(LegacySmritiApi.COLLECTTION)
    fun posClloection(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<Any>>

    /**
     * 取消收藏
     */
    @POST(LegacySmritiApi.COLLECTION_CANCEL)
    fun posCollectionCancel(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<Any>>
    /**
     * 站点下级区域(两层)
     */
    @GET(LegacySmritiApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>
    /**
     * 非遗项目详情
     */
    @GET(LegacySmritiApi.LEGACY_ITEM_DETAIL)
    fun getLegacyDetail(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LegacyDetailBean>>


    /**
     * 非遗传承人详情
     */
    @GET(LegacySmritiApi.LEGACY_PEOPLE_DETAIL)
    fun getLegacyPeopleDetail(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LegacyPeopleDetailBean>>

    /**
     * 非遗体验基地详情
     */
    @GET(LegacySmritiApi.LEGACY_EXPERIENCE_BASE_DETAIL)
    fun getLegacyExperienceBaseDetail(@Query("id") id:String):Observable<BaseResponse<LegacyBaseDetailBean>>


    /**
     * 非遗传习基地详情
     */
    @GET(LegacySmritiApi.LEGACY_TEACHING_BASE_DETAIL)
    fun getLegacyTeachingBaseDetail(@Query("id") id:String):Observable<BaseResponse<LegacyBaseDetailBean>>

    /**
     * 非遗传承人
     */
    @GET(LegacySmritiApi.LEGACY_PEOPLE)
    fun getLegacyPeople(@QueryMap map: HashMap<String, Any>): Observable<BaseResponse<LegacyPeopleBean>>


}
