package com.daqsoft.integralmodule.repository.api

import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.integralmodule.repository.bean.*
import com.daqsoft.provider.bean.MineMessageBean
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardDetail
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
interface IntegralService {

    @GET(IntegralApi.POINT_COUNT)
    fun pointCount(): Observable<BaseResponse<PointCountBean>>

    /**
     * C端用户任务列表
     *
     * userId         传文化云/小电商用户ID
     * cruxValue      小电商传店铺code/文化云传站点ID
     * publishChannel 传常量
     * code           站点编码/店铺编码
     * jumpType       默认H5(h5网页) SMALL_PROGRAM(小程序) APP(app端)
     *
     */
    @GET(IntegralApi.GET_API_TASK_LIST)
    fun getApiTaskList(
        @Query("params") params: String
    ): Observable<BaseResponse<TaskListBean>>

    /** 完成任务
     * @param taskId 任务id
     * @param userId 用户id
     */
    @FormUrlEncoded
    @POST(IntegralApi.POS_COMPLETE_TASK)
    fun completeTast(@Field("taskId") taskId: String, @Field("userId") userId: String): Observable<BaseResponse<CompleteTaskBean>>

    /**
     * 接取任务
     * @param taskId 任务id
     * @param userId 用户id
     */
    @FormUrlEncoded
    @POST(IntegralApi.PICK_TASK)
    fun pickTask(@Field("taskId") taskId: String, @Field("userId") userId: String): Observable<BaseResponse<Any>>

    /**
     * 站点详情
     */
    @GET(IntegralApi.SITE_INFO)
    fun siteInfo(): Observable<BaseResponse<SiteInfoBean>>


    /**
     * 用户积分详情
     * @param type 类型(1:消费，0:获取)
     * @param time 日期（格式yyyy-MM）
     */
    @GET(IntegralApi.DETAIL)
    fun detail(
        @Query("type") type: Int,
        @Query("time") time: String
    ): Observable<BaseResponse<DetailBean>>

    /**
     * 积分配置规则
     */
    @GET(IntegralApi.POINT_CONFIG_INFO)
    fun pointConfigInfo(): Observable<BaseResponse<ConfigInfoBean>>

    @GET(IntegralApi.CITY_CARD)
    fun getVisitingCard(): Observable<BaseResponse<CityCardDetail>>

    @GET(IntegralApi.NO_READ_NUMBER)
    fun getNoReadMessage(): Observable<BaseResponse<MineMessageBean>>

    @GET(IntegralApi.NO_READ_NUMBER_T)
    fun getNoReadMessage1(): Observable<BaseResponse<MineMessageBean>>

    @GET(IntegralApi.NO_READ_LIST)
    fun getNoReadMessageList(): Observable<BaseResponse<MineMessageBean>>
}