package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   LectureHall
 * @Author      luoyi
 * @Time        2020/6/15 13:32
 */
data class LectureHall(
    /**
     * 学习时长（单位秒）
     */
    var duration: Int,
    /**
     * 学习状态
     * 0 未学习 1学习中 2已学完
     */
    var studyStatus: Int,
    /**
     * 图片
     */
    var image: String,
    /**
     * 用户数
     */
    var userNum: String,
    /**
     * 课程总学时秒
     */
    var totalDuration: Int,
    /**
     * 章节数
     */
    var chapterNum: Int,
    /**
     * 创建时间
     */
    var createTime: String,
    /**
     * 	序号
     */
    var sort: Int,
    /**
     * 类型id
     */
    var typeId: Int,
    /**
     * 课程名
     */
    var name: String,
    /**
     * 数据id
     */
    var id: Int,
    /**
     * 面向对象（标签）
     */
    var objectOriented: String
)

/**
 * 课程详情
 */
data class LectureHallDetailBean(
    var orgId: String,
    var siteId: Int,
    /**
     * 	章节数
     */
    var chapterNum: Int,
    /**
     * 	置顶
     */
    var top: Int,
    /**
     * 推荐至频道首页
     */
    var recommendChannelHomePage: Int,
    var recommendHomePage: Int,
    /**
     * 用户数
     */
    var userNum: String,
    /**
     * 观看次数
     */
    var pv: Int,
    /**
     * 创建时间
     */
    var createTime: String,
    /**
     * 状态
     */
    var status: Int,
    /**
     * 面向对象-标签
     */
    var objectOriented: String,
    /**
     *  思考问题
     */
    var thinkingProblem: String,
    /**
     * 主要内容
     */
    var content: String,
    /**
     * 课程目标
     */
    var courseAims: String,
    /**
     * 简介
     */
    var introduction: String,
    /**
     * 排序
     */
    var sort: Int,
    /**
     * 图片
     */
    var image: String,
    /**
     * 讲师名字
     */
    var lecturerName: String,
    /**
     * 讲师id
     */
    var lecturerId: Int,
    /**
     *  类型名字
     */
    var typeName: String,
    /**
     * 类型id
     */
    var typeId: Int,
    /**
     *  课程名字
     */
    var name: String,
    /**
     *  数据id
     */
    var id: Int,
    /**
     * 收藏数
     */
    var collections: Int,
    /**
     * 讲师概述
     */
    var lecturerOverview: String,
    /**
     *  讲师头像
     */
    var lecturerImage: String,
    /**
     *  收藏状态
     */
    var collectionStatus: Boolean
)

/**
 * 课程章节实体
 */
data class LectureHallChapterBean(
    var ctcSchoolChapterVOS: MutableList<LectureHallChapter>,
    var sort: Int,
    /**
     * 章节类型名
     */
    var name: String,
    var id: Int

)

/**
 * 课程章节
 */
data class LectureHallChapter(
    /**
     * 用户学习时间秒
     */
    var userDuration: Int,
    /**
     * 课程地址
     */
    var address: String,
    /**
     * 	播放量
     */
    var playNum: String,
    /**
     * 课时时间秒
     */
    var duration: Int,
    /**
     * 排序
     */
    var sort: Int,
    /**
     * 章节名
     */
    var name: String,
    /**
     * 章节id
     */
    var id: String,
    /**
     * 章节号
     */
    var chapterNumber: String,
    /**
     * 是否用户最新章节
     */
    var userLatestChapter: Boolean
)

/**
 * 课程问题实体
 */
data class LectureRequestion(
    /**
     * 创建时间
     */
    var createTime: String,
    /**
     *状态，都是1
     */
    var status: Int,
    /**
     * 回复人id
     */
    var replyUserID: String,
    /**
     * 回复时间
     */
    var replyTime: String,
    /**
     * 回复内容
     */
    var reply: String,
    /**
     * 问题
     */
    var question: String,
    /**
     *  章节id
     */
    var chapterId: Int,
    /**
     * 课程名字
     */
    var courseName: String,
    /**
     * 课程id
     */
    var courseId: String,
    /**
     *  用户头像
     */
    var userHead: String,
    /**
     * 用户名字
     */
    var userName: String,
    /**
     * 用户id
     */
    var userId: Int,
    /**
     *  数据id
     */
    var id: Int
)