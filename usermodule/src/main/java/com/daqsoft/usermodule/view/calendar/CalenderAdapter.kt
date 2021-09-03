package com.daqsoft.usermodule.view.calendar

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.usermodule.R
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import java.util.ArrayList
import java.util.HashMap

/**
 * @Description 日历的适配器
 * @ClassName   CalenderAdapter
 * @Author      PuHua
 * @Time        2019/11/23 17:36
 */
class RecyclerAdapter(
    results: List<CalendarBean>,
    val calendarListener: CalendarListener,
    val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val weeks = arrayOf("日", "一", "二", "三", "四", "五", "六")

    private val mHandler = Handler()
    private var results = ArrayList<CalendarBean>()
    private var priceMap = HashMap<String, String>()

    init {
        this.results = results as ArrayList<CalendarBean>
    }

    override fun getItemCount(): Int = results?.size

    fun updateForPrice(priceMap: HashMap<String, String>) {
        this.priceMap = priceMap
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.fragment_calendar_group_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        initItemView(holder, position, results[position])
    }

    private fun initItemView(holder: RecyclerView.ViewHolder, position: Int, bean: CalendarBean) {
        val calendarViewHolder = holder as CalendarViewHolder
        calendarViewHolder.subAdapter.showResult(bean.dateBeans!!, priceMap)
        var weekHint: TextView
        calendarViewHolder.weekLayout.removeAllViews()
        for (i in weeks.indices) {
            weekHint = TextView(mContext)
            weekHint.layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            weekHint.gravity = Gravity.CENTER
            weekHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            weekHint.setText(weeks[i])
            if (i == 0 || i == weeks.size - 1) {
                //周末-红色
                weekHint.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            } else {
                //工作日
                weekHint.setTextColor(mContext.resources.getColor(R.color.txt_black))
            }
            calendarViewHolder.weekLayout.addView(weekHint)
        }


    }

    internal inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recyclerView: RecyclerView = itemView.findViewById(R.id.month_recycler) as RecyclerView
        var subAdapter: SubRecyclerAdapter
        private var subResults: List<DateBean> = ArrayList()
        var weekLayout: LinearLayout

        init {
            val gridLayoutManager = GridLayoutManager(mContext, 7)
            recyclerView.layoutManager = gridLayoutManager
            subAdapter = SubRecyclerAdapter(subResults, calendarListener, mContext)
            recyclerView.adapter = subAdapter
            weekLayout = itemView.findViewById(R.id.week_layout) as LinearLayout
        }
    }

    companion object {

        private val TYPE_CONTENT = 1
    }
}

class SubRecyclerAdapter(
    results: List<DateBean>,
    calendarListener: CalendarListener,
    val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var results: List<DateBean>? = ArrayList()
    private var priceMap: HashMap<String, String>? = HashMap()
    private val calendarListener: CalendarListener? = calendarListener


    fun showResult(results: List<DateBean>, priceMap: HashMap<String, String>) {
        this.results = results
        this.priceMap = priceMap
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.calendar_child_item, parent, false)
        return SubCalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        initItemView(holder, position, results!![position])
    }

    override fun getItemCount(): Int {
        return results?.size ?: 0
    }

    private fun initItemView(holder: RecyclerView.ViewHolder, position: Int, bean: DateBean) {
        val subCalendarViewHolder = holder as SubCalendarViewHolder
        subCalendarViewHolder.mDay.setText(bean.getShownDay())
        if (priceMap != null && !TextUtils.isEmpty(priceMap!![bean.getFomartTag()])) {
            subCalendarViewHolder.mSubDay.setText("￥" + priceMap!![bean.getFomartTag()]!!)
        } else if (!TextUtils.isEmpty(bean.getSpecialDayTag())) {
            subCalendarViewHolder.mSubDay.setText(bean.getSpecialDayTag())
        } else {
            subCalendarViewHolder.mSubDay.setText(bean.getNongliDay())
        }
        // 法定节假日默认隐藏
        subCalendarViewHolder.mGovHolidayHint.setVisibility(View.INVISIBLE)
        if (!bean.isCanSelect()) {
            // 不可选
            subCalendarViewHolder.mDay.setTextColor(mContext.resources.getColor(R.color.txt_gray))
        } else if (bean.isGovHoliday()) {
            // 节假日
            subCalendarViewHolder.mGovHolidayHint.setVisibility(View.VISIBLE)
            subCalendarViewHolder.mGovHolidayHint.setText("休")
            subCalendarViewHolder.mGovHolidayHint.setTextColor(mContext.resources.getColor(R.color.txt_gray))
            subCalendarViewHolder.mDay.setTextColor(mContext.resources.getColor(R.color.txt_gray))
        } else if (bean.isGovHolidayWork()) {
            // 调休工作日
            subCalendarViewHolder.mGovHolidayHint.setVisibility(View.VISIBLE)
            subCalendarViewHolder.mGovHolidayHint.setText("班")
            subCalendarViewHolder.mGovHolidayHint.setTextColor(mContext.resources.getColor(R.color.txt_black))
            subCalendarViewHolder.mDay.setTextColor(mContext.resources.getColor(R.color.txt_black))
        } else if (bean.getDayOfWeek() === 1 || bean.getDayOfWeek() === 7) {
            //正常周末
            subCalendarViewHolder.mDay.setTextColor(mContext.resources.getColor(R.color.txt_gray))
        } else {
            //其他工作日
            subCalendarViewHolder.mDay.setTextColor(mContext.resources.getColor(R.color.txt_black))
        }
        if (bean.isCheck()) {
            //选中 blue
            subCalendarViewHolder.itemView.setBackgroundResource(R.drawable.calendar_check_bg)
        } else {
            subCalendarViewHolder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }
        subCalendarViewHolder.itemView.setOnClickListener {
            if (bean.isCanSelect()) {
                calendarListener!!.onDaySelect(bean)
            }
        }
    }
}

class SubCalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mDay: TextView
    var mSubDay: TextView
    var mGovHolidayHint: TextView

    init {
        mDay = itemView.findViewById(R.id.day) as TextView
        mSubDay = itemView.findViewById(R.id.subday) as TextView
        mGovHolidayHint = itemView.findViewById(R.id.gov_holiday_hint) as TextView
    }
}