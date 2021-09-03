package com.daqsoft.travelCultureModule.lecturehall.model

import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean

/**
 * @Description
 * @ClassName   LectureHallModel
 * @Author      luoyi
 * @Time        2020/6/16 16:09
 */
object LectureHallModel {


    /**
     * 课程详情tab
     */
    val lectureTabs: MutableList<ValueKeyBean> = mutableListOf(
        ValueKeyBean("简介", "", false),
        ValueKeyBean("课程", "", false),
        ValueKeyBean("评论", "", false),
        ValueKeyBean("问答", "", false)
    )
}