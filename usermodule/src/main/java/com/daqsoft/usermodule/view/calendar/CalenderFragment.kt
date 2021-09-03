package com.daqsoft.usermodule.view.calendar


import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragmentConsumeCalenderBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.support.v4.toast
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @des 日历控件
 * @author PuHua
 * @date
 */
class CalenderFragment(startTime: Long, endTime: Long,cal:CalendarListener) : BaseFragment<FragmentConsumeCalenderBinding,
        CalenderFragmentViewModel>() {

    private val mStartTime = startTime
    private val mEndTime = endTime
    /**
     * 记录当前位置
     */
    private var currentPositon = 0


    private val canlenderListener = cal

//    override fun onDaySelect(bean: DateBean?) {
//        if (pickerMode == MODE.SINGLE.toNumber()) {
//            // 单程
//            val intent = Intent()
//            toast(bean.toString())
//            setCalendarEnableRange(bean!!)
//
//        }
//    }

    //取日期类型，1-单程 2-往返
    private var pickerMode: Int = 0

    private var groupAdapter: RecyclerAdapter? = null
    private val calendars = ArrayList<CalendarBean>()
    private val startToEndMonth = intArrayOf(0, 7)


    override fun getLayout(): Int = R.layout.fragment_consume_calender

    override fun injectVm(): Class<CalenderFragmentViewModel> = CalenderFragmentViewModel::class.java

    override fun initView() {
        val start = Calendar.getInstance()
        start.timeInMillis = mStartTime
        val sy = start.get(Calendar.YEAR)
        val sm = start.get(Calendar.MONTH)

        val startMonth = sy * 100 + sm

//        val bookingEndTime = TimeUtils.timeStamp2Date(it.bookingTimeEnd)
        val end = Calendar.getInstance()
        end.timeInMillis = mEndTime
        val ey = end.get(Calendar.YEAR)
        val em = end.get(Calendar.MONTH)
        val endMonth = ey * 100 + em

        val fms = endMonth - startMonth
//        setCalendarEnableRange()

    }

    override fun initData() {

        pickerMode = 1
        var d = Observable.just("")
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                init()
            }
            .subscribe {
                initViews()
            }

    }

    fun init() {

        val start = Calendar.getInstance()
        start.timeInMillis = mStartTime

        val end = Calendar.getInstance()
        end.timeInMillis = mEndTime
        start.time = Date()

        var calendarBean: CalendarBean? = null
        for ((groupIndex, i) in (startToEndMonth[0]..startToEndMonth[1]).withIndex()) {
            calendarBean = CalendarBean()
            val calendarClone = start.clone() as Calendar
            calendarClone.add(Calendar.MONTH, i)
            calendarClone.set(Calendar.DATE, 1)
            calendarBean.year = calendarClone.get(Calendar.YEAR)
            calendarBean.month = calendarClone.get(Calendar.MONTH) + 1
            calendarBean.shownTitle =
                context!!.getString(R.string.order_y_m, calendarBean.year.toString(), calendarBean.month.toString())

            // 1-星期天 7-星期六
            val dayOfWeek = calendarClone.get(Calendar.DAY_OF_WEEK)
            // 上月的最后几天展示在本月
            val emptyCount = dayOfWeek - 1
            calendarClone.roll(Calendar.DATE, -1)
            // 当月的最大天数
            val maxDays = calendarClone.get(Calendar.DATE)

            val daysList = ArrayList<DateBean>()
            var maxRows = 5
            if (emptyCount + maxDays > 35) {
                //当月有效日期+1号前空白日期个数>35,则当月需要6行
                maxRows = 6
            }
            for (j in 0 until maxRows * 7) {
                val dayItem = DateBean()
                // 用于控制定位
                dayItem.setGroupIndex(groupIndex)
                dayItem.setChildIndex(j)

                val calendarDayClone = calendarClone.clone() as Calendar
                if (j < emptyCount) {
                    dayItem.setCanSelect(false)
                    // 上月最后几天
                    calendarDayClone.add(Calendar.DATE, j - emptyCount)
                    dayItem.setShownDay("")
                    dayItem.setNongliDay("")
                    daysList.add(dayItem)

                    continue
                }
                if (j >= emptyCount + maxDays) {
                    dayItem.setCanSelect(false)
                    // 下月头几天
                    calendarDayClone.add(Calendar.MONTH, 1)
                    calendarDayClone.set(Calendar.DATE, j - (emptyCount + maxDays) + 1)
                    dayItem.setShownDay("")
                    dayItem.setNongliDay("")
                    daysList.add(dayItem)

                    continue
                }
                calendarDayClone.set(Calendar.DATE, j - emptyCount + 1)
                //显示的日期
                dayItem.setShownDay(calendarDayClone.get(Calendar.DATE).toString())
                //显示的下部文字
//                dayItem.setNongliDay(LunarDayUtil(calendarDayClone).toStringSimpleDay())
                //显示的上部文字
//                dayItem.setSpecialDayTag(SpecialDayUtil.getInstance().getHolidayName(calendarDayClone))

                dayItem.setYear(calendarDayClone.get(Calendar.YEAR))
                dayItem.setMonth(calendarDayClone.get(Calendar.MONTH) + 1)
                dayItem.setDay(calendarDayClone.get(Calendar.DATE))
                dayItem.setDayOfWeek(calendarDayClone.get(Calendar.DAY_OF_WEEK))
                dayItem.setGovHoliday(false)
                dayItem.setGovHolidayWork(false)

                when {
                    calendarDayClone.before(start) ->
                        //今天之前
                        dayItem.setCanSelect(false)
                    calendarDayClone == start -> {
                        // 今天
                        dayItem.setShownDay(context!!.getString(R.string.order_today))
                        dayItem.setCanSelect(true)
                    }
                    else ->
                        //今天之后
                        dayItem.setCanSelect(true)
                }
                dayItem.setSaverCalendar(calendarDayClone)

                daysList.add(dayItem)
            }
            calendarBean.dateBeans = daysList
            calendars.add(calendarBean)
        }
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.recyclerCalendar.layoutManager = linearLayoutManager
        groupAdapter = RecyclerAdapter(calendars, canlenderListener, context!!)
        mBinding.recyclerCalendar.adapter = groupAdapter
        mBinding.recyclerCalendar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var firstVisibleItemPosition = 0
                when (newState) {
                    // 判断RecyclerView滑动不同的状态

                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 获得当前显示在第一个item的位置

                        firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                        mBinding.tvCenter.text = calendars[firstVisibleItemPosition].shownTitle
                        currentPositon = firstVisibleItemPosition
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // 获得当前显示在第一个item的位置
                        firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                        mBinding.tvCenter.text = calendars[firstVisibleItemPosition].shownTitle
                        currentPositon = firstVisibleItemPosition
                    }
                }

            }
        })
        mBinding.tvCenter.text = context!!.getString(
            R.string.order_y_m, calendars[0].year.toString(), calendars[0].month
                .toString()
        )
        // 点击右边
        RxView.clicks(mBinding.tvRight)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    if (currentPositon < calendars.size - 1) {
                        mBinding.recyclerCalendar.smoothScrollToPosition(currentPositon + 1)
                    }
                }
            }
        // 点击左边
        RxView.clicks(mBinding.tvLeft)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                run {
                    if (currentPositon >0) {
                        mBinding.recyclerCalendar.smoothScrollToPosition(currentPositon - 1)
                    }
                }
            }
    }

    /**
     * 设置日历可选范围
     *
     * @param centerBean 临界日期
     */
    private fun setCalendarEnableRange(centerBean: DateBean) {
        for (monthBean in calendars) {
            for (dateBean in monthBean.dateBeans!!) {

                if (dateBean.getSaverCalendar() != null) {
                    if (dateBean.getSaverCalendar()!!.before(centerBean.getSaverCalendar())) {
                        dateBean.setCanSelect(false)
                    } else {
                        dateBean.setCanSelect(true)
                    }
                } else {
                    dateBean.setCanSelect(false)
                }

            }
        }
        groupAdapter!!.notifyDataSetChanged()
        (mBinding.recyclerCalendar.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            centerBean.getGroupIndex(), 0
        )
    }

}

/**
 * @des 日历控件的控制器
 * @author PuHua
 * @date
 */
class CalenderFragmentViewModel : BaseViewModel() {


}

enum class MODE private constructor(iNum: Int) {
    // 单选
    SINGLE(1),
    // 多选
    ROUND(2);


    private var iNum = 0

    init {
        this.iNum = iNum
    }

    fun toNumber(): Int {
        return this.iNum
    }

}
