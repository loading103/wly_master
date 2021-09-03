package com.daqsoft.usermodule.view.calendar

import java.util.*

/**
 * @Description 日期类型
 * @ClassName   DateBean
 * @Author      PuHua
 * @Time        2019/11/23 17:20
 */
class DateBean {
    // 组index
    private var groupIndex: Int = 0
    // 当前index
    private var childIndex: Int = 0
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var dayOfWeek: Int = 0
    // 日期
    private var shownDay: String? = null
    // 状态或加价金额（下部展示数据）
    private var belowShow: String? = null
    // 剩余可预订数(上部展示数据)
    private var topShow: String? = null
    //选中情况
    private var isCheck: Boolean = false
    // 可否选择
    private var canSelect: Boolean = false
    // 法定节假日
    private var isGovHoliday: Boolean = false
    // 节假日调休工作
    private var isGovHolidayWork: Boolean = false
    // 记录的日历
    private var saverCalendar: Calendar? = null

    fun getGroupIndex(): Int {
        return groupIndex
    }

    fun setGroupIndex(groupIndex: Int) {
        this.groupIndex = groupIndex
    }

    fun getChildIndex(): Int {
        return childIndex
    }

    fun setChildIndex(childIndex: Int) {
        this.childIndex = childIndex
    }

    fun getYear(): Int {
        return year
    }

    fun setYear(year: Int) {
        this.year = year
    }

    fun getMonth(): Int {
        return month
    }

    fun setMonth(month: Int) {
        this.month = month
    }

    fun getDay(): Int {
        return day
    }

    fun setDay(day: Int) {
        this.day = day
    }

    fun getDayOfWeek(): Int {
        return dayOfWeek
    }

    fun setDayOfWeek(dayOfWeek: Int) {
        this.dayOfWeek = dayOfWeek
    }

    fun getShownDay(): String? {
        return shownDay
    }

    fun setShownDay(shownDay: String) {
        this.shownDay = shownDay
    }

    fun getNongliDay(): String? {
        return belowShow
    }

    fun setNongliDay(nongliDay: String) {
        this.belowShow = nongliDay
    }

    fun getSpecialDayTag(): String? {
        return topShow
    }

    fun setSpecialDayTag(specialDayTag: String) {
        this.topShow = specialDayTag
    }

    fun isCheck(): Boolean {
        return isCheck
    }

    fun setCheck(check: Boolean) {
        isCheck = check
    }

    fun isCanSelect(): Boolean {
        return canSelect
    }

    fun setCanSelect(canSelect: Boolean) {
        this.canSelect = canSelect
    }


    fun isGovHoliday(): Boolean {
        return isGovHoliday
    }

    fun setGovHoliday(govHoliday: Boolean) {
        isGovHoliday = govHoliday
    }

    fun isGovHolidayWork(): Boolean {
        return isGovHolidayWork
    }

    fun setGovHolidayWork(govHolidayWork: Boolean) {
        isGovHolidayWork = govHolidayWork
    }

    fun getSaverCalendar(): Calendar? {
        return saverCalendar
    }

    fun setSaverCalendar(saverCalendar: Calendar) {
        this.saverCalendar = saverCalendar
    }

    /**
     * yyyy-MM-dd标准格式
     */
    fun getFomartTag(): String {
        return year.toString() + "-" + (if (month < 10) "0$month" else month.toString()) + "-" +
                if (day < 10) "0$day" else day.toString()
    }
}