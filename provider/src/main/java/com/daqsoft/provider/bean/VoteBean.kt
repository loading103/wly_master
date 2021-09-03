package com.daqsoft.provider.bean

import com.daqsoft.baselib.utils.DateUtil

/**
 * @Description
 * @ClassName   VoteBean
 * @Author      luoyi
 * @Time        2020/11/9 11:13
 */
class VoteBean {
    /**
     *投票类型
     */
    var voteType: String? = ""

    /**
     * 投票进行状态
     */
    var voteStatus: Int = 0

    /**
     * 标题
     */
    var title: String? = ""

    /**
     * 开始时间
     */
    var startTime: String = ""

    /**
     *    资源统计信息
     */
    var resourceCount: VoteResourceCount? = null

    /**
     * 主图
     */
    var mainImages: String? = ""

    /**
     * 数据id
     */
    val id: Int = 0

    /**
     *  结束时间
     */
    var endTime: String? = ""

    /**
     *  封面图(单张)
     */
    var coverImage: String? = ""

    /**
     * 首次投票时间
     */
    var firstVoteTime: String? = ""
    var uploadStatus: String? = ""
}

/**
 * 投票资源统计实体
 */
data class VoteResourceCount(
    /**
     * 票数
     */
    val ticketCount: String,
    /**
     * 作品数
     */
    val joinCount: String,
    /**
     * 浏览数
     */
    val showCount: String? = "0"
)

/**
 * @Description
 * @ClassName   VoteDetailBean
 * @Author      luoyi
 * @Time        2020/11/9 11:13
 */
class VoteDetailBean {
    /**
     *投票类型
     */
    var voteType: String? = ""

    /**
     * 投票进行状态
     */
    var voteStatus: Int = 0

    /**
     * 投票规则
     */
    var voteRule: String? = ""

    /**
     * 标题
     */
    var title: String? = ""

    /**
     * 开始时间
     */
    var startTime: String = ""

    /**
     * 距离开始剩余时间
     */
    var startSurplusTimestamp: Int = 0

    /**
     *    资源统计信息
     */
    var resourceCount: VoteResourceCount? = null

    /**
     * 主图
     */
    var mainImages: String? = ""

    /**
     * 数据id
     */
    val id: Int = 0

    /**
     *  结束时间
     */
    var endTime: String? = ""

    /**
     * 距离结束剩余时间
     */
    var endSurplusTimestamp: Int = 0

    /**
     *  封面图(单张)
     */
    var coverImage: String? = ""

    /**
     * 首次投票时间
     */
    var firstVoteTime: String? = ""

    /**
     * 承办单位
     */
    var undertakeUnit: String? = ""

    /**
     * 指导单位
     */
    var teachUnit: String? = ""

    /**
     * 主办单位
     */
    var mainUnit: String? = ""

    /**
     * 协办单位
     */
    var jointlyUnit: String? = ""

    /**
     * 简介
     */
    var intro: String? = ""

    /**
     * 手机号
     */
    var phone: String? = ""

    /**
     * 用户加入数量
     */
    var userJoinCount: Int = 0

    /**
     * 必填项字段
     */
    var requiredItem: String? = ""

    var uploadStatus: String? = ""

    // 距离上传开始时间
    var startUploadTimestamp: String? = ""

    var endUploadTimestamp: String? = ""

    var uploadEndTime: String? = ""

    var uploadStartTime: String? = ""



    var proSetting: VoteInPartSetingBean? = null


    fun getStatVoteTime(): String{
        if(startTime.isNullOrBlank() || startTime=="0"){
            return ""
        }
        return "投票时间：${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", startTime)} 至 " +
                "${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss",endTime)}"
    }

    fun getStatUpdateTime(): String{
        if(uploadStartTime.isNullOrBlank() || uploadStartTime=="0"){
            return ""
        }
        return "上传时间：${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", uploadStartTime)} 至 " +
                "${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss",uploadEndTime)}"
    }

    fun showWebView(): Boolean{
        return !voteRule.isNullOrBlank() && voteStatus==0
    }

}

/**
 * 投票类型
 */
class VoteTypeBean {
    var child: MutableList<VoteSubTypeBean>? = mutableListOf()

    /**
     * 分类名称
     */
    var name: String? = ""

    /**
     * 分类id
     */
    var id: Int = 0
}

/**
 * 投票子类型
 */
class VoteSubTypeBean {
    /**
     * 分类名称
     */
    var name: String? = ""

    /**
     * 分类id
     */
    var id: Int = 0
}

class VoteInPartSetingBean {
    /**
     * 视频状态
     */
    var videoStatus: Int = 0

    /**
     * 图片状态
     */
    var imageStatus: Int = 0

    /**
     * 图片数量
     */
    var imageCount: Int = 0

    /**
     * 必填参数
     */
    var requiredItem: String? = ""

    /**
     * 视频数量
     */
    var videoCount: Int = 0
}
