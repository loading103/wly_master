package com.daqsoft.travelCultureModule.hotActivity.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.PopActCalSelectBinding
import com.daqsoft.provider.bean.ActivityCalenderBean
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActCalSelectAdapter
import java.util.*

/**
 * @Description
 * @ClassName   ActCalSelectPopWindow
 * @Author      luoyi
 * @Time        2020/6/22 10:33
 */
class ActCalSelectPopWindow : PopupWindow {

    var context: Context? = null
    var binding: PopActCalSelectBinding? = null
    private var mViewAppointment: View? = null
    private var actCalSelectAdapter: ActCalSelectAdapter? = null
    private var selectDateTime: String? = ""
    var onActCalOpeationListener: OnActCalOpeationListener? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_act_cal_select,
            null,
            false
        )
        mViewAppointment = binding!!.root
        initView()
        initPopWindow()
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);

        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }


    private fun initView() {
        actCalSelectAdapter = ActCalSelectAdapter(context!!)
        actCalSelectAdapter?.emptyViewShow = false
        actCalSelectAdapter?.onSelectActCalListener = object : ActCalSelectAdapter.OnSelectActCalListener {
            override fun onselectActCall(datas: MutableList<ActivityCalenderBean>) {
                onActCalOpeationListener?.onSelectDate(datas)
                dismiss()
            }

        }
        binding?.recyActSelectTimes?.adapter = actCalSelectAdapter
        binding?.recyActSelectTimes?.layoutManager = GridLayoutManager(
            context!!, 7,
            GridLayoutManager.VERTICAL, false
        )
        binding?.vNextMonth?.onNoDoubleClick {
            if (!selectDateTime.isNullOrEmpty()) {
                var date = DateUtil.getFormatDateByString("yyyy-MM-dd", selectDateTime)
                var nextDate: Date? = DateUtil.getNexMonth(date)
                if (nextDate != null) {
                    var dateTime: String = DateUtil.getDqDateString("yyyy-MM", nextDate)
                    onActCalOpeationListener?.onChangeMonth(dateTime)
                }

            }
        }
        binding?.vPreMonth?.onNoDoubleClick {
            if (!selectDateTime.isNullOrEmpty()) {
                var date = DateUtil.getFormatDateByString("yyyy-MM-dd", selectDateTime)
                var preDate: Date? = DateUtil.getPreMonth(date)
                if (preDate != null) {
                    var dateTime: String = DateUtil.getDqDateString("yyyy-MM", preDate)
                    onActCalOpeationListener?.onChangeMonth(dateTime)
                }

            }
        }
        binding?.root?.onNoDoubleClick {
            dismiss()
        }
    }

    fun setDataInfo(datas: MutableList<ActivityCalenderBean>, selTime: String?) {
        var dateTime: String = ""
        if (!datas.isNullOrEmpty() && datas[0] != null) {
            dateTime = datas[0].dateTime
        }

        if (!dateTime.isNullOrEmpty()) {
            var date = DateUtil.getFormatDateByString("yyyy-MM-dd", dateTime)
            binding?.txtActTimes?.text = "" + DateUtil.getDqDateString("yyyy年MM月", date)
            selectDateTime = dateTime
            var week = DateUtil.getDayOfWeekDate(date)
            var offsetDay = DateUtil.getDayOffest(week)
            var data: MutableList<ActivityCalenderBean> = mutableListOf()
            if (offsetDay > 1) {
                for (index in 1 until offsetDay) {
                    data.add(ActivityCalenderBean(0, ""))
                }
            }
            datas.addAll(0, data)
            var currCal = Calendar.getInstance()
            currCal.time = Date()
            var currMonth = currCal.get(Calendar.MONTH)
            currCal.time = date
            var dateMonth = currCal.get(Calendar.MONTH)
//            if(currMonth==dateMonth){
//                binding?.vPreMonth?.visibility=View.GONE
//            }else{
//                binding?.vPreMonth?.visibility=View.VISIBLE
//            }
            binding?.vNextMonth?.visibility = View.VISIBLE
        } else {
            binding?.vNextMonth?.visibility = View.GONE
            binding?.vPreMonth?.visibility = View.GONE
        }
        actCalSelectAdapter?.clear()
        if (!selTime.isNullOrEmpty()) {
            actCalSelectAdapter?.selectActCalDateTime = selTime
        }
        if (!datas.isNullOrEmpty()) {
            actCalSelectAdapter?.add(datas)
        }

    }

    interface OnActCalOpeationListener {

        fun onChangeMonth(dateTime: String)

        fun onSelectDate(datas: MutableList<ActivityCalenderBean>)
    }
}