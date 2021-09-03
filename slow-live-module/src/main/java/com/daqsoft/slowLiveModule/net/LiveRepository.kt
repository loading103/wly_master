package com.daqsoft.slowLiveModule.net

import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.comment.CommentApi
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.slowLiveModule.bean.*
import com.daqsoft.slowLiveModule.bean.LiveAroundBean
import com.daqsoft.slowLiveModule.bean.LiveDetailBean
import com.daqsoft.slowLiveModule.bean.LiveContentBean
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.io.IOException
import java.util.*

/**
 * @des    慢直播的网络工具
 * @author Wongxd
 * @date   2020/4/16 16:00
 */
internal class LiveRepository {

    companion object {
        val service: LiveService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)   //13340951622
            .addInterceptor(HeaderInterceptor())
//            .setBaseUrl("http://ctc-api.test.daqsoft.com/v2/")
//            .addInterceptor(DevInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(LiveService::class.java)
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
        httpUrlBuilder?.addQueryParameter("siteCode", "site333474")



        request = request.newBuilder().url(httpUrlBuilder?.build()).build()
        val response = chain.proceed(request)
        val responseBody = response.peekBody(1024 * 1024.toLong())
        logI("LiveRepository-  headers" + request.headers().toString() + "\n  ,  url:" + response.request().url() + "\n  ,  resp:" + responseBody.string())
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
 * @des      慢直播的网络api
 * @author   Wongxd
 * @Date     2020/4/16 16:19
 */
internal interface LiveService {

    /**
     * 周边推荐
     */
    @GET(LiveApi.LIVE_AROUND_NEW)
    fun getLiveAround(
        @Query("scenicSpotsId") scenicSpotsId: String,
        @Query("scenicId") scenicId: String
    ): Observable<BaseResponse<LiveAroundBean>>


    /**
     * 直播列表
     * http://ctc-api.test.daqsoft.com/v2/res/api/scenic/liveTop?token=&source=android&siteCode=site488314
     *  http://ctc-api.test.daqsoft.com/v2/res/api/content/channelSubset?channelCode=mzb&token=&source=android&siteCode=site488314
     *  http://ctc-api.test.daqsoft.com/v2/res/api/scenic/liveList?pageSize=10&currPage=1&token=&source=android&siteCode=site488314
     *   http://ctc-api.test.daqsoft.com/v2/res/api/live/liveTop?token=&source=android&siteCode=site488314
     */
    @GET(LiveApi.LIVE_LIST_NEW)
    fun getLiveList(
        @Query("pageSize") pageSize: String,
        @Query("currPage") currPage: String
    ): Observable<BaseResponse<LiveListBean>>


    /**
     * 直播详情
     */
    @GET(LiveApi.LIVE_DETAIL_NEW)
    fun getLiveDetail(@Query("scenicSpotsId") scenicSpotsId: String): Observable<BaseResponse<LiveDetailBean>>


    /**
     * 置顶直播
     */
    @GET(LiveApi.LIVE_TOP_NEW)
    fun getLiveTop(): Observable<BaseResponse<LiveTopBean>>


    /**
     * 获取评论列表
     * commentTagId	评论标签id	number
     * commentLevel	评论等级	number	 0：好评 1：差评 2：一般
     * currPage	    当前页	    number	 默认 1
     * pageSize	    每页数量	number	 默认 10
     * resourceType	资源类型	string	【必填】
     * resourceId	资源id	    number	【必填】
     */
    @GET(CommentApi.COMMENT_LIST)
    fun getCommentList(@QueryMap map: HashMap<String, String>): Observable<BaseResponse<CommentBean>>


    /**
     * 点赞
     * @param resourceType 取值来源{@ResourceType}
     */
    @POST(CommentApi.THUMB_UP)
    fun postThumbUp(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 取消点赞
     * @param resourceType 取值来源{@ResourceType}
     */
    @POST(CommentApi.THUMB_CANCELL)
    fun postThumbCancel(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 收藏
     */
    @POST(CommentApi.COLLECTTION)
    fun posClloection(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 取消收藏
     */
    @POST(CommentApi.COLLECTION_CANCEL)
    fun posCollectionCancel(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>


    /**
     * 获取资讯分类
     */
    @GET(LiveApi.LIVE_CONTENT_TYPE)
    fun getContentType(@Query("channelCode") id: String): Observable<BaseResponse<LiveContentTypeBean>>


    /**
     * 获取资讯列表
     * @param orderType sort：按序号排序 publishTime：按发布时间排序（精确到秒） publishDate:按发布时间排序（精确到天）
     */
    @GET(LiveApi.CONTENT_LIST)
    fun getContentList(
        @Query("areaSiteSwitch") areaSiteSwitch: String,
        @Query("linksResourceId") linksResourceId: String,
        @Query("linksResourceType") linksResourceType: String,
        @Query("orderType") orderType: String,
        @Query("pageSize") pageSize: String,
        @Query("currPage") currPage: String,
        @Query("region") region: String,
        @Query("channelCode") channelCode: String
    ): Observable<BaseResponse<LiveContentBean>>


    /**
     * 获取资讯详情
     */
    @GET(LiveApi.CONTENT_DETAIL)
    fun getContentDetail(@Query("id") id: String): Observable<BaseResponse<LiveContentInfoBean>>


    /**
     * 站点下级区域(两层)
     */
    @GET(LiveApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>


}