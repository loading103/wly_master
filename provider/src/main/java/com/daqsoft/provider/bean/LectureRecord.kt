package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   LectureRecord
 * @Author      luoyi
 * @Time        2020/6/18 14:43
 */
data class LectureRecord(
    /**
     * 总章节数
     */
    var chapterNum: Int,
    /**
     * 总课时秒
     */
    var totalDuration: Int
)