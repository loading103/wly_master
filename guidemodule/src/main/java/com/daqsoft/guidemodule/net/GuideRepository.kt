package com.daqsoft.guidemodule.net

import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.guidemodule.bean.*
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.io.IOException
import java.util.*

/**
 * @des Guide的网络工具
 * @author Wongxd
 * @date   2020/4/1 17:50
 */
internal class GuideRepository {

    companion object {
        val service: GuideService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)   //13340951622
            .addInterceptor(HeaderInterceptor())
//            .setBaseUrl("http://ctc-api.test.daqsoft.com/v2/")        //13340951622
//            .addInterceptor(DevInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(GuideService::class.java)
    }


}

internal class DevInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        var request = chain.request()

        val httpUrlBuilder = Objects.requireNonNull(
            request.url()
                .newBuilder(request.url().toString())
        )

        httpUrlBuilder?.addQueryParameter("token", "f78bec7a705f4e33a479bf01783e96cf")
        httpUrlBuilder?.addQueryParameter("siteCode", "site688790")

        request = request.newBuilder().url(httpUrlBuilder?.build()).build()
        logI(request.headers().toString() + "\n" + request.url() + "\n")
        val response = chain.proceed(request)
        val responseBody = response.peekBody(1024 * 1024.toLong())
        logI(request.headers().toString() + "\n" + response.request().url() + "\n" + responseBody.string())
        return response
    }

}


internal fun logI(info: String, tag: String = "wongxd") {  //信息太长,分段打印

    var msg = info

    //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，

    //  把4*1024的MAX字节打印长度改为2001字符数

    val max_str_length = 2001 - tag.length

    //大于4000时

    while (msg.length > max_str_length) {

        Log.i(tag, msg.substring(0, max_str_length))

        msg = msg.substring(max_str_length)

    }

    //剩余部分

    Log.i(tag, msg)

}

/**
 * @des Guide的网络api
 * @author Wongxd
 * @Date 2020/4/1 17:50
 */
internal interface GuideService {

    /**
     * 查询站点标签（云端+站点）
     *
     */
    @GET(GuideApi.SELECT_LABEL)
    fun getSelectLabel(
        @Query("labelType") labelType: String, @Query("resourceType")
        resourceType: String
    )
            : Observable<BaseResponse<ResourceTypeLabel>>

    /**
     * 导览景点-景点列表
     *
     * resourceType	 资源类型	string    CONTENT_TYPE_SCENERY 景区，CONTENT_TYPE_SCENIC_SPOTS 景点，CONTENT_TYPE_TOILET  厕所，CONTENT_TYPE_PARKING  停车场，CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE 非遗（包含CONTENT_TYPE_HERITAGE_TEACHING_BASE，CONTENT_TYPE_HERITAGE_PROTECT_BASE）
     * tourId        导览id	    string
     * lon	         经度	    string
     * lat	         纬度	    string
     * token	     令牌      	string
     *
     */
    @GET(GuideApi.GUIDE_SCENIC_LIST)
    fun getGuideScenicList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<GuideScenicListBean>>

    /**
     * type	场馆分类type	number	资源类型为场馆CONTENT_TYPE_VENUE时，传入类型可查询博物馆，文化馆等小分类
     * resourceType	资源类型	string
     * pageSize	分页大小	number
     * currPage	当前页	number
     * name	名字	string
     * tourId	导览id	number
     * lon		number
     * lat		number
     * token	令牌	string
     */
    @GET(GuideApi.GUIDE_SEARCH_RESOUCE_NEW)
    fun searchGuideListNewAll(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<GuideScenicListBean>>

    /**
     * 导游导览 资源列表
     * tourId	导游导览id	number	【必填】
     * 	type	1全域，2景区	number	空值默认为全域版
     */
    @GET(GuideApi.GUIDE_RESOURCE_LIST)
    fun getGuideResourceList(@Query("tourId") tourId: String,@Query("type")type:Int): Observable<BaseResponse<GuideResouceBean>>


    /**
     * 导览首页列表
     *
     * crowd	 适合人群	string
     * theme     景区主题id	string
     * level	 景区等级	string
     * token	 令牌      	string
     * lon	     经度	string
     * lat	     纬度	string
     * recommendSort	 推荐排序	 bool
     * distanceSort	    距离排序	 bool
     *
     */
    @GET(GuideApi.GUIDE_LIST)
    fun getGuideList(@QueryMap param: HashMap<String, Any>): Observable<BaseResponse<GuideHomeListBean>>


    /**
     * 导览详情
     */
    @GET(GuideApi.GUIDE_DETAIL)
    fun getGuideDetail(@Query("id") id: String): Observable<BaseResponse<GuideScenicDetailBean>>


    /**
     * 导览景点-线路列表
     *
     * tourId        导览id	    string
     * lon	         经度	    string
     * lat	         纬度	    string
     * token	     令牌      	string
     *
     */
    @GET(GuideApi.GUIDE_ROUTE)
    fun getGuideRoute(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<GuideLineBean>>


    /**
     * 导览搜索
     */
    @GET(GuideApi.GUIDE_RESOURCE_SEARCH)
    fun searchAll(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("tourId") tourId: String, //	导览id
        @Query("name") name: String
    ): Observable<BaseResponse<GuideSearchBean>>

    /**
     * 导览搜索
     */
    @GET(GuideApi.GUIDE_RESOURCE_NEW_SEARCH)
    fun searchNewAll(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("tourId") tourId: String, //	导览id
        @Query("name") name: String
    ): Observable<BaseResponse<GuideSearchBean>>
}