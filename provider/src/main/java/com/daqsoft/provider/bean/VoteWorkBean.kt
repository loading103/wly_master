package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   VoteWorkBean
 * @Author      luoyi
 * @Time        2020/11/13 14:49
 */
class VoteWorkBean {

    /**
     * 投票站点ID
     */
    var voteSiteId: Int = 0

    /**
     * 投票按钮0 不可投票 1 可以投票 2 已投票 3 已结束
     */
    var voteButton: Int = -1

    /**
     *  视频
     */
    var video: String? = ""

    /**
     * 类型名称
     */
    var typeName: String? = ""

    /**
     * 子类型名称
     */
    var typeChildName: String? = ""

    /**
     *    资源统计信息
     */
    var resourceCount: VoteResourceCount? = null

    /**
     * 发布站点id
     */
    var publishSiteId: Int = -1

    /**
     * 作品名称
     */
    var name: String? = ""

    /**
     * 图片
     */
    var images: String? = ""

    var id: Int = -1

    var codeNum: String? = ""
}


class VoteWorkDetailBean {
    /**
     * 投票信息
     */
    var voteInfo: VoteWrokMinInfo? = null

    /**
     * 投票按钮状态
     */
    var voteButton: Int = 0

    /**
     * 审核信息
     */
    var auditInfo: VoteAuditInfo? = null

    /**
     * 投票ID
     */
    var voteId: Int = 0

    /**
     * 视频
     */
    var video: String? = null

    /**
     *  大分类名称
     */
    var typeName: String? = null

    /**
     * 小分类名称
     */
    var typeChildName: String? = null

    /**
     * 小分类ID
     */
    var typeChild: Int = 0

    /**
     *  大分类ID
     */
    var type: Int = 0

    /**
     * 手机号
     */
    var phone: String? = ""

    /**
     * 作品名称
     */
    var name: String? = ""

    /**
     * 简介
     */
    var intro: String? = ""

    /**
     * 主图
     */
    var images: String? = ""

    /**
     *  作品ID
     */
    var id: Int = 0

    /**
     *  数据状态
     */
    var dataStatus: Int = 0

    /**
     * 编号
     */
    var codeNum: String = ""

    /**
     * 作品
     */
    var author: String? = ""

    /**
     * 是否本人发布
     */
    var myselfFlag: Boolean = false


    var resourceCount: VoteWorkCountBean? = null

    var createTime: String? = ""

    /**
     * 身份证号码
     */
    var idCard: String? = ""


    var voteStatus: String? = ""

}

/**
 * 作品详情，简要投票信息
 */
class VoteWrokMinInfo {

    /**
     * 投票进行状态
     */
    var voteStatus: Int = -1
    var uploadStatus: Int = -1
    /**
     * 标题
     */
    var title: String? = ""

    /**
     * 开始时间
     */
    var startTime: String? = ""

    var mainImages: String? = ""

    var id: Int = -1

    var endTime: String? = ""

    var voteRule: String? = ""

    var teachUnit: String? = ""

    var mainUnit: String? = ""

    var jointlyUnit: String? = ""

    var undertakeUnit: String? = ""
}

class VoteAuditInfo {

    /**
     * 资源类型
     */
    var resourceType: String? = ""

    /**
     * 资源ID
     */
    var resourceId: Int = 0

    /**
     * 处理用户名
     */
    var handleUserName: String? = ""

    /**
     * 处理时间
     */
    var handleTime: String? = ""

    /**
     * 提交时间
     */
    var createTime: String? = ""

    /**
     * 审核状态 待审核(4) 审核通过(6) 回退/驳回(7) 撤回(8) 终止(9) 审核不通过(79)
     */
    var auditStatus: Int = 0

    /**
     *  审核结果
     */
    var auditResult: String? = ""

    /**
     *  审核ID
     */
    var auditManageId: Int = 0
}

/**
 * 投票返回结果
 */
class VoteResultBean {
    /**
     * 投票限制状态
     * - 0: 不限制 * - 1: 总投票数限制 * - 2: 每天投票限制 * - 3: 每项投票限制
     */
    var voteLimitStatus: Int = 0

    /**
     * 剩余投票数量
     * -1 不限制
     */
    var surplusCount: Int = 0

    var position: Int = -1

    var continueFlag: Boolean = false
}

class VoteWorkCountBean {
    /**
     * 浏览量
     */
    var showCount: String = ""

    /**
     * 票数
     */
    var ticketCount: String = ""

    /**
     * 排名
     */
    var rankCount: String = ""
}

/**
 * 我的作品实体
 */
class MineVoteWorkBean {
    var voteInfo: VoteWrokMinInfo? = null

    /**
     * 投票id
     */
    var voteId: Int = 0

    /**
     * 作品名称
     */
    var name: String = ""

    /**
     * 作品id
     */
    var id: Int = 0

    /**
     * 创建时间
     */
    var createTime: String = ""

    var images: String = ""

    var auditInfo: VoteAuditInfo? = null
}