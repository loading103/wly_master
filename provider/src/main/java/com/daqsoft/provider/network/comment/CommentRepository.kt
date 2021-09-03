package com.daqsoft.provider.network.comment

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.network.RetrofitFactory
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.CommentTagsBean
import com.daqsoft.provider.network.comment.beans.CommtentTagBean
import com.daqsoft.provider.network.comment.beans.ReplyBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @des 互动管理的网络接口工具-- 转评赞
 * @author PuHua
 * @date  1121
 */
class CommentRepository {

    companion object {
        val service: CommentService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(CommentService::class.java)
    }

}

/**
 * @des 互动管理的接口
 * @author PuHua
 * @Date 2019/12/27 11:24
 */
interface CommentService {

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
     * 新增浏览记录
     */
    @FormUrlEncoded
    @POST(CommentApi.ADD_RECORD)
    fun postAddRecord(
        @Field("resourceId") resourceId: String,
        @Field("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 上传评论信息
     */
    @POST(CommentApi.ADD_COMMENT)
    fun postAddComment(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("content") content: String,
        @Query("image") image: String = "",
        @Query("video") video: String = "",
        @Query("orderId") orderId: String? = "",
        @Query("star") star: Int = 5,
        @Query("emoticonsIds") emoticonsIds: String? = ""
    ): Observable<BaseResponse<Any>>

    /**
     * 上传评论信息
     */
    @POST(CommentApi.ADD_COMMENT)
    fun postAddComment(
        @QueryMap param: HashMap<String, String>
    ): Observable<BaseResponse<Any>>

    /**
     * 上传评论信息
     */
    @POST(CommentApi.ADD_COMMENT)
    fun postAddComment(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("content") content: String,
        @Query("emoticonsIds") emoticonsIds: String? = ""
    ): Observable<BaseResponse<Any>>


    /**
     * 上传回复信息
     */
    @POST(CommentApi.ADD_COMMENT_REPLY)
    fun postReplyComment(
        @Body body: RequestBody
    ): Observable<BaseResponse<Any>>

    /**
     * 上传回复信息
     */
    @GET(CommentApi.REPLY_LIST)
    fun getReplyList(
        @Query("commentId") commentId: String,
        @Query("currPage") currPage: String,
        @Query("pageSize") pageSize: String
    ): Observable<BaseResponse<ReplyBean>>


    /**
     * 关注资源
     */
    @POST(CommentApi.ATTENTION_RESOURCE)
    fun attentionResource(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 取消关注资源
     */
    @POST(CommentApi.ATTENTION_RESOURCE_CANCLE)
    fun attentionResourceCancle(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String
    ): Observable<BaseResponse<Any>>

    /**
     * 获取评论标签统计信息（分组）
     */
    @GET(CommentApi.COMMENT_TAG_COUNT_INFO)
    fun getCommentTagCountInfo(
        @Query("resourceType") resourceType: String, @Query("resourceId")
        resourceId: String, @Query("group") group: Boolean
    ): Observable<BaseResponse<HashMap<String, CommentTagsBean>>>

    /**
     * 获取评论标签统计信息（分组）
     */
    @GET(CommentApi.COMMENT_TAG_COUNT_INFO)
    fun getCommentTag(
        @Query("resourceType") resourceType: String, @Query("resourceId")
        resourceId: String
    ): Observable<BaseResponse<CommtentTagBean>>

    /**
     * 增加点评
     */
    @POST(CommentApi.COMMENT_ADD)
    fun postAddComment(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("content") content: String,
        @Query("level") level: String,
        @Query("image") image: String,
        @Query("commentTag") commentTag: String,
        @Query("video") video: String = "",
        @Query("orderId") orderId: String? = "",
        @Query("emoticonsIds") emoticonsIds: String? = ""
    ): Observable<BaseResponse<String>>

    @POST(CommentApi.COMMENT_ADD)
    fun postAddCommentNew(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("content") content: String,
        @Query("level") level: String,
        @Query("image") image: String,
        @Query("commentTag") commentTag: String,
        @Query("video") video: String = "",
        @Query("orderId") orderId: String? = "",
        @Query("emoticonsIds") emoticonsIds: String? = "",
        @Query("star") star: String? = ""
    ): Observable<BaseResponse<String>>
    /**
     * 增加点评
     */
    @POST(CommentApi.COMMENT_ADD)
    fun postAddComment(
        @Query("resourceId") resourceId: String,
        @Query("resourceType") resourceType: String,
        @Query("content") content: String,
        @Query("image") image: String,
        @Query("commentTag") commentTag: String,
        @Query("video") video: String = "",
        @Query("orderId") orderId: String? = "",
        @Query("emoticonsIds") emoticonsIds: String? = ""
    ): Observable<BaseResponse<String>>
    /**
     * 评论表情获取
     */
    @GET(CommentApi.COMMENT_EMOT_LIST)
    fun getEmotList(
        @Query("currPage") currPage: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<BaseResponse<EmoticonsBean>>

    /**
     * 获取平均评论星级
     */
    @GET(CommentApi.COMMENT_START)
    fun getCommentStart(
        @Query("resourceType") resourceType: String,
        @Query("resourceId") resourceId: String
    ): Observable<BaseResponse<String>>

}