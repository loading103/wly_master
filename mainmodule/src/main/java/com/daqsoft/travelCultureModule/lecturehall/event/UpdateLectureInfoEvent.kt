package com.daqsoft.travelCultureModule.lecturehall.event

import com.daqsoft.provider.bean.LectureHallDetailBean

/**
 * @Description
 * @ClassName   UpdateLectureInfoEvent
 * @Author      luoyi
 * @Time        2020/6/16 17:45
 */
class UpdateLectureInfoEvent {

    /**
     * 视频地址
     */
    var videoUrl: String = ""

    /**
     * 消息类型
     */
    var type: Int = 0

    var bean: LectureHallDetailBean? = null

    var chapterId: String? = "0"
    var userStudyDuration: Int = 0

    var duration: Int = 0
    var isAutoPlay:Boolean=false

    constructor(videoUrl: String, chapterId: String, studyDuration: Int, duration: Int) {
        type = 0
        this.videoUrl = videoUrl
        this.chapterId = chapterId
        this.userStudyDuration = studyDuration
        this.duration = duration
    }

    constructor(data: LectureHallDetailBean,isAutoPlay:Boolean) {
        type = 1
        this.isAutoPlay= isAutoPlay
        this.bean = data
    }
}