package com.daqsoft.provider.network.vote

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.venues.VenuesApi
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @Description
 * @ClassName   VoteRespository
 * @Author      luoyi
 * @Time        2020/11/12 16:46
 */
class VoteRespository {


    companion object {
        val service: VoteService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(VoteService::class.java)

        /**
         * 投票列表
         */
        const val VOTE_LIST: String = "res/api/vote/list"

        /**
         * 我的投票列表
         */
        const val MINE_VOTE_LIST: String = "res/api/vote/listByUser"

        /**
         * 投票详情
         */
        const val VOTE_DETAIL: String = "res/api/vote/info"

        /**
         * 投票类型列表
         */
        const val VOTE_TYPE_LIST: String = "res/api/vote/type/list"

        /**
         * 作品列表
         */
        const val VOTE_WORK_LIST: String = "res/api/vote/pro/list"

        /**
         * 我的作品列表
         */
        const val MINE_VOTE_WORK_LIST: String = "res/api/vote/pro/uploadProList"

        /**
         * 保存作品
         */
        const val SAVE_VOTE_WORK: String = "res/api/vote/pro/save"

        /**
         * 投票
         */
        const val VOTE_WORK: String = "res/api/vote/pro/vote"

        /**
         * 投票作品详情
         */
        const val VOTE_WORK_INFO: String = "res/api/vote/pro/info"

        /**
         * 投票作品操作
         */
        const val VOTE_WORK_OPERTAION: String = "res/api/vote/pro/operateStatus"

    }

    interface VoteService {

        /**
         * @param pageSize 页面大小
         * @param currPage 当前页码
         */
        @GET(VOTE_LIST)
        fun getVoteList(@Query("pageSize") pageSize: Int, @Query("currPage") currPage: Int): Observable<BaseResponse<VoteBean>>

        /**
         * @param pageSize 页面大小
         * @param currPage 当前页码
         */
        @GET(VOTE_LIST)
        fun searchVoteList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<VoteBean>>

        /**
         * 投票详情
         * @param voteId 投票id
         */
        @GET(VOTE_DETAIL)
        fun getVoteDetail(@Query("voteId") voteId: String): Observable<BaseResponse<VoteDetailBean>>


        /**
         * 投票类型
         * @param voteId 投票id
         * @param queryMode 查询类型
         */
        @GET(VOTE_TYPE_LIST)
        fun getVoteTypeList(@Query("voteId") voteId: String, @Query("queryMode") queryMode: Int = 0):
                Observable<BaseResponse<VoteTypeBean>>


        /**
         * 我的投票列表
         */
        @GET(MINE_VOTE_LIST)
        fun getMineVoteList(@Query("pageSize") pageSize: Int, @Query("currPage") currPage: Int)
                : Observable<BaseResponse<VoteBean>>

        /**
         * 作品列表
         */
        @GET(VOTE_WORK_LIST)
        fun getVoteWorkList(
            @Query("pageSize") pageSize: Int,
            @Query("currPage") currPage: Int,
            @Query("voteId") voteId: String,
            @Query("orderMode") orderMode: String
        ): Observable<BaseResponse<VoteWorkBean>>


        /**
         * 作品列表
         */
        @GET(VOTE_WORK_LIST)
        fun searchVoteWorkList(
            @QueryMap param: HashMap<String, String>
        ): Observable<BaseResponse<VoteWorkBean>>

        /**
         * 我的作品列表
         */
        @GET(MINE_VOTE_WORK_LIST)
        fun getMineVoteWorkList(@QueryMap param: HashMap<String, String>): Observable<BaseResponse<MineVoteWorkBean>>

        /**
         * 作品详情
         */
        @GET(VOTE_WORK_INFO)
        fun getVoteWorkInfo(@Query("proId") proId: String): Observable<BaseResponse<VoteWorkDetailBean>>

        /**
         * 投票
         */
        @FormUrlEncoded
        @POST(VOTE_WORK)
        fun voteWork(@Field("proId") proId: String): Observable<BaseResponse<VoteResultBean>>

        /**
         * 保存投票作品
         */
        @FormUrlEncoded
        @POST(SAVE_VOTE_WORK)
        fun saveVoteWork(@FieldMap param: HashMap<String, String>): Observable<BaseResponse<Any>>

        /**
         * 操作作品
         */
        @FormUrlEncoded
        @POST(VOTE_WORK_OPERTAION)
        fun operationWork(@FieldMap param: HashMap<String, String>): Observable<BaseResponse<Any>>

        /**
         * 获取vip用户信息
         */
        @GET(VenuesApi.GET_VIP_INFO)
        fun getVipInfo(): Observable<BaseResponse<VipInfoBean>>

    }
}