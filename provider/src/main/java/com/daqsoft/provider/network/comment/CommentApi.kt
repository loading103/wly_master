package com.daqsoft.provider.network.comment

/**
 * @des 互动的接口
 * @author PuHua
 * @Date 2019/12/27 11:25
 */
object CommentApi {


    /**
     * 获取评论列表
     */
    const val COMMENT_LIST = "res/api/comment/getCommentList"

    /**
     * 点赞
     */
    const val THUMB_UP = "res/api/interactManage/like"

    /**
     * 取消点赞
     */
    const val THUMB_CANCELL = "res/api/interactManage/cancelLike"

    /**
     * 收藏
     */
    const val COLLECTTION = "res/api/interactManage/collection"

    /**
     * 取消收藏
     */
    const val COLLECTION_CANCEL = "res/api/interactManage/cancelCollection"

    /**
     * 新增浏览记录
     */
    const val ADD_RECORD = "res/api/interactManage/addBrowseRecord"

    /**
     * 保存评论信息
     */
    const val ADD_COMMENT = "res/api/comment/saveComment"
    /**
     * 保存评论信息
     */
    const val ADD_COMMENT_REPLY = "res/api/commentReply/reply"

    /**
     * 获取评论列表
     */
    const val REPLY_LIST = "res/api/commentReply/list"
    /**
     * 关注资源
     */
    const val ATTENTION_RESOURCE = "res/api/interactManage/focus"

    /**
     * 取消关注资源
     */
    const val ATTENTION_RESOURCE_CANCLE = "res/api/interactManage/cancelFocus"

    /**
     * 评论标签统计信息（分组
     */
    const val COMMENT_TAG_COUNT_INFO = "res/api/comment/commentTagCountInfo"

    /**
     * 增加点评
     */
    const val COMMENT_ADD = "res/api/comment/saveComment"

    /**
     * 表情包列表
     */
    const val COMMENT_EMOT_LIST = "res/api/comment/emoticonsList"
    /**
     * 获取平均评论星级
     */
    const val COMMENT_START = "res/api/comment/getAvgCommentStar"
}