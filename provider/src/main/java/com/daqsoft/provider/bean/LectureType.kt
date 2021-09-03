package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   LectureType
 * @Author      luoyi
 * @Time        2020/6/15 11:58
 */
data class LectureType(

    /**
     * 状态
     */
    var status: String,
    /**
     * 图片
     */
    var image: String,
    /**
     * 顺序
     */
    var sort: String,
    /**
     * 课程分类code
     */
    var code: String,
    /**
     * 类型名字
     */
    var name: String,

    /**
     * 类型id无用
     */
    var id: Int
)