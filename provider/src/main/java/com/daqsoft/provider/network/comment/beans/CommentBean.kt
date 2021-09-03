package com.daqsoft.provider.network.comment.beans

import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.R
import kotlinx.android.parcel.Parcelize

/**
 * @des 互动 评论的详情
 * @author PuHua
 * @Date 2019/12/27 11:31
 */
@Parcelize
data class CommentBean(
    // 评论标签
    val commentTag: List<String>,

    val commentReplyPageData: CommentReplyBean,
    // 评论时间
    val commentTime: String,
    // 评论内容
    val content: String,
    // 数据id
    val id: String,
    // 评论图片
    val image: List<String>,
    // 评论等级
    val level: String,
    // 视频地址
    val video: List<String>,
    // 视频图片
    val videoCover: List<String>,
    // 会员头像
    val vipHead: String,
    // 会员昵称
    val vipNickName: String,
    // 志愿者状态 大于0 标识为志愿者
    val volunteerStatus: Int,
    val star: Int,
    var emoticonsUrl: MutableList<String>? = mutableListOf()
): Parcelable


/**
 * 评论标签分组
 */
@Parcelize
data class CommentReplyBean(
    var totalCount: Int,
    val totalPage: Int,
    val currPage: Int,
    val pageSize: Int,
    val rows: MutableList<ReplyBean>
): Parcelable{
    fun  getTotleContent():String{
        return "全部评论($totalCount)"
    }
}

@Parcelize
data class ReplyBean(
    val commentId: String,
    val createTime: String,
    val head: String,
    val id: String,
    var name: String,
    val parentHead: String,
    var parentName: String,
    val replyContent: String,
    val source: String,
    var emoticonsUrl: MutableList<String>? = mutableListOf()
): Parcelable{
    fun  getContent():SpannableString{
        if(name.isNullOrBlank()){
            name="";
        }
        if(parentName.isNullOrBlank()){
            parentName="";
        }
        if(name==parentName){
            val haha= "${name}回复:$replyContent"
            return SpannableString(haha)
        }

        val content = "${name}回复$parentName:$replyContent"

        var spanStr = SpannableString(content)

        // name=平台 管理端回复
        if(source=="MANAGE"){
            spanStr.setSpan(ForegroundColorSpan(BaseApplication.context.resources.getColor(R.color.colorPrimary)), 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            // name=平台 用户端回复
            spanStr.setSpan(ForegroundColorSpan(BaseApplication.context.resources.getColor(R.color.colorPrimary)),  name.length+3, name.length+3+parentName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanStr
    }
    fun  getReplyContent():SpannableString{
        if(parentName.isNullOrBlank()){
            parentName="";
        }

        if(name==parentName){
            val haha= "回复:$replyContent"
            return SpannableString(haha)
        }

        val content = " 回复$parentName:$replyContent"
        var spanStr = SpannableString(content)

        // name=平台 用户端回复
//        if(source!="MANAGE"){
        // name=平台 用户端回复
        spanStr.setSpan(ForegroundColorSpan(BaseApplication.context.resources.getColor(R.color.colorPrimary)),  3, 3+parentName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        return spanStr
    }
}
/**
 * 评论标签分组
 */
data class CommentGroupsBean(
    val bean: CommentTagsBean,
    val commentNum: Int,
    val tagList: MutableList<CommtentTagBean>
)

/**
 * 评论标签分组
 */
data class CommentTagsBean(
    val commentType: Int,
    val commentNum: Int,
    val tagList: MutableList<CommtentTagBean>
)

/**
 * 评论标签
 */
data class CommtentTagBean(
    val id: Int,
    val name: String,
    val commentType: Int,
    var commentNum: Int
)