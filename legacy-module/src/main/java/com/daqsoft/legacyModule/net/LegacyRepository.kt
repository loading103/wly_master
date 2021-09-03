package com.daqsoft.legacyModule.net

import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.legacyModule.home.bean.HomeAdInfoBean
import com.daqsoft.legacyModule.home.bean.HomeTopImgBean
import com.daqsoft.legacyModule.media.bean.LegacyMediaListBean
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.legacyModule.bean.*
import com.daqsoft.legacyModule.bean.LegacyHeritageExperienceBaseListBean
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.bean.LegacyStoryTagListBean
import com.daqsoft.legacyModule.home.bean.LegacyFoodBean
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.network.RetrofitFactory
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.*
import java.io.IOException
import java.util.*

/**
 * @des    品非遗 里的 网路请求
 * @author Wongxd
 * @Date   2020/4/20  20:07
 */
internal class LegacyRepository {

    companion object {
        val service: LegacyService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)   //13340951622
            .addInterceptor(HeaderInterceptor())
//            .setBaseUrl("https://www.daqctc.com/api/")
//            .addInterceptor(DevInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(LegacyService::class.java)

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

        httpUrlBuilder?.addQueryParameter("token", "a5e38595938140cfa0ec514d885ba72c")
//        httpUrlBuilder?.addQueryParameter("siteCode", "site688790")
        httpUrlBuilder?.addQueryParameter("siteCode", "site333474")




        request = request.newBuilder().url(httpUrlBuilder?.build()).build()
        val response = chain.proceed(request)
        val responseBody = response.peekBody(1024 * 1024.toLong())
        logI(" headers" + request.headers().toString() + "\n  ,  url:" + response.request().url() + "\n  ,  resp:" + responseBody.string())
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

        Log.e(tag, msg.substring(0, max_str_length))

        msg = msg.substring(max_str_length)

    }

    //剩余部分

    Log.e(tag, msg)

}

/**
 * @des      品非遗的网络 api
 * @author   Wongxd
 * @Date     2020/4/20 20:19
 */
internal interface LegacyService {


    /**
     * 站点下级区域(两层)
     */
    @GET(LegacyApi.SITE_CHILD_REGION)
    fun getChildRegions(): Observable<BaseResponse<ChildRegion>>


    /**
     * 首页顶部图片 和 文字描述
     */
    @GET(LegacyApi.TOP_HERITAGE_ITEM)
    fun getHomeTopImg(): Observable<BaseResponse<HomeTopImgBean>>


    /**
     * 获取非遗美食
     */
    @GET(LegacyApi.FOOD_LIST)
    fun getFoodList(
        @Query("currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 12,
        @Query("channelCode") channelCode: String = "sjsdms"
    ): Observable<BaseResponse<LegacyFoodBean>>

    /**
     * 首页AD
     */
    @GET(LegacyApi.HOME_AD)
    fun getHomeAd(
        @Query(value = "publishChannel") publishChannel: String,
        @Query("adCode") adCode: String
    ): Observable<BaseResponse<HomeAdInfoBean>>


    /**
     * 非遗故事 tag list
     */
    @GET(LegacyApi.STORY_TAG_LIST)
    fun getStoryHotTagList(
        @Query("recommend") recommend: Int = 1,
        @Query("size") size: Int = 6,
        @Query("minStoryNum") minStoryNum: Int = 1,
        @Query(value = "tagName") tagName: String = "非遗"
    ): Observable<BaseResponse<LegacyStoryTagListBean>>


    /**
     * 非遗故事  list
     */
    @GET(LegacyApi.STORY_LIST)
    fun getStoryList(
        @Query("currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("ich") ich: Int = 1,
        @Query("orderType") orderType: String = ""
    ): Observable<BaseResponse<LegacyStoryListBean>>


    /**
     * 非遗故事  list
     */
    @GET(LegacyApi.STORY_LIST)
    fun getStoryListNew(
        @Query("currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("ich") ich: Int = 1
    ): Observable<BaseResponse<HomeStoryBean>>


    /**
     * 获取详情页面故事详情
     */
    @GET(LegacyApi.DETAIL_STORY_LIST)
    fun getDetailStoryList(
        @Query("ichTermId") ichTermId: String,
        @Query("pageSize") pageSize: Int = 6
    ): Observable<BaseResponse<LegacyStoryListBean>>

    /**
     * 非遗项目列表(首页-发现非遗)
     */
    @GET(LegacyApi.HERITAGE_ITEM_LIST)
    fun getHeritageItemList(
        @Query(value = "currPage") currPage: String = "1",
        @Query("pageSize") pageSize: String = "3",
        @Query("ids") ids: String = "",
        @Query("level") level: String = "",
        @Query("batch") batch: String = "",
        @Query("type") type: String = "",
        @Query("region") region: String = "",
        @Query("sortType") sortType: String = ""
    ): Observable<BaseResponse<LegacyHeritageItemListBean>>

    /**
     * 非遗传承人列表(首页-发现非遗)
     */
    @GET(LegacyApi.HERITAGE_PEOPLE_LIST)
    fun getHeritagePeopleList(
        @Query(value = "currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 0,
        @Query("nationality") nationality: String = "",
        @Query("heritageItemId") heritageItemId: String = "",
        @Query("region") region: String = "",
        @Query("gender") gender: String = "",
        @Query("areaSiteSwitch") areaSiteSwitch: String = ""
    ): Observable<BaseResponse<LegacyHeritagePeopleListBean>>

    /**
     * 非遗体验基地列表(首页-发现非遗)
     */
    @GET(LegacyApi.HERITAGE_EXPERIENCE_LIST)
    fun getHeritageExperienceList(
        @Query(value = "currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 3,
        @Query("region") region: String = "",
        @Query("level") level: String = "",
        @Query("sortType") sortType: String = "",
        @Query("areaSiteSwitch") areaSiteSwitch: String = ""
    ): Observable<BaseResponse<LegacyHeritageExperienceBaseListBean>>

    /**
     * 非遗传习基地列表(首页-发现非遗)
     */
    @GET(LegacyApi.HERITAGE_TEACHING_LIST)
    fun getHeritageETeachingList(
        @Query(value = "currPage") currPage: Int = 1,
        @Query("pageSize") pageSize: Int = 3,
        @Query("region") region: String = "",
        @Query("sortType") sortType: String = "",
        @Query("areaSiteSwitch") areaSiteSwitch: String = ""
    ): Observable<BaseResponse<LegacyHeritageExperienceBaseListBean>>

    /**
     * 视听非遗列表
     */
    @GET(LegacyApi.MEDIA_LIST)
    fun getMediaList(
        @Query(value = "currPage") currPage: String,
        @Query("pageSize") pageSize: String,
        @Query("mediaType") mediaType: String
    ): Observable<BaseResponse<LegacyMediaListBean>>

    /**
     * 非遗资讯分类
     */
    @GET(LegacyApi.NEWS_CATEGORY)
    fun getNewsCategory(@Query("channelCode") channelCode: String = "fyzx"): Observable<BaseResponse<NewsCategoryBean>>

    /**
     * 获取非遗统计数据
     */
    @GET(LegacyApi.LEGACY_NUM)
    fun getLegacyNum(): Observable<BaseResponse<LegacyNumCount>>

    /**
     * 获取非遗传承人作品列表
     */
    @GET(LegacyApi.LEGACY_WORKS)
    fun getLegacyWorks(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<LegacyStoryListBean>>

    /**
     * 获取我的非遗作品
     */
    @GET(LegacyApi.MINE_LEGACY_WORKS)
    fun getMineLegacyWorks(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<LegacyStoryListBean>>

    /**
     * 获取关注、粉丝列表
     */
    @GET(LegacyApi.MINE_FANS_LIST)
    fun getFansList(
        @Query("pageSize") pageSize: Int = 20,
        @Query("currPage") currPage: Int = 1,
        @Query("type") type: String = "fans"
    ): Observable<BaseResponse<FansBean>>

    /**
     * 置顶数据
     */
    @FormUrlEncoded
    @POST(LegacyApi.VIP_TOP)
    fun vipTop(@Field("top") top: Int = 1, @Field("id") id: Int): Observable<BaseResponse<String>>

    /**
     * 删除我的作品
     */
    @FormUrlEncoded
    @POST(LegacyApi.DELETE_VIP_WORKS)
    fun postDeleteWorks(@FieldMap map: Map<String, @JvmSuppressWildcards Any>)
            : Observable<BaseResponse<String>>

    /**
     * 我的关注
     */
    @GET(LegacyApi.WATCH_STORY)
    fun getWatchStory(): Observable<BaseResponse<LegacyWatchStoryListBean>>

    /**
     * 获取我的关注
     */
    @GET(LegacyApi.VIP_ATTENTION_STORY_LIST)
    fun getAttentionStoryList(@Query("pageSize") pageSize: Int = 200): Observable<BaseResponse<LegacyStoryListBean>>

    /**
     * 获取pk列表
     */
    @GET(LegacyApi.PK_WORKS_LIST)
    fun getPKWorksList(@Query("pkId") pkId: String): Observable<BaseResponse<LegacyStoryListBean>>

    /**
     * 获取我被pk的列表
     */
    @GET(LegacyApi.PK_PEOPLE_LIST)
    fun getPkPeopleList(@Query("type")type:String,@Query("currPage") currPage:Int,@Query("pageSize") pageSize:Int = 20):Observable<BaseResponse<FansBean>>



    /**
     * 非遗故事  list
     */
    @GET(LegacyApi.STORY_LIST)
    fun getIntangibleHeritageStory(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("ich") ich: Boolean = true,
        @Query("pageSize") pageSize: Int = 6
    ): Observable<BaseResponse<HomeStoryBean>>
}