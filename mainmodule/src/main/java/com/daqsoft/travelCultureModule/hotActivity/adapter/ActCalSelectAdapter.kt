package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActCalSelectBinding
import com.daqsoft.provider.bean.ActivityCalenderBean
import org.jetbrains.anko.textColor
import java.util.*

/**
 * @Description
 * @ClassName   ActCalSelectAdapter
 * @Author      luoyi
 * @Time        2020/6/22 11:19
 */
class ActCalSelectAdapter : RecyclerViewAdapter<ItemActCalSelectBinding, ActivityCalenderBean> {

    var context: Context? = null

    var selectActCalDateTime: String? = ""
    var onSelectActCalListener: OnSelectActCalListener? = null

    constructor(context: Context) : super(R.layout.item_act_cal_select) {
        this.context = context
    }

    override fun setVariable(
        mBinding: ItemActCalSelectBinding,
        position: Int,
        item: ActivityCalenderBean
    ) {
        item?.let {
            mBinding.vActCalIndtor.visibility = View.GONE
            var dateTime = it.dateTime
            if (!dateTime.isNullOrEmpty()) {
                var date = DateUtil.getFormatDateByString("yyyy-MM-dd", dateTime)
                if (DateUtil.isSameDate(date, Date())) {
                    mBinding.tvActCalSelect.text = "今天"
                    mBinding.vActCalIndtor.visibility = View.GONE
                    mBinding.tvActCalSelect.textColor =
                        context!!.resources.getColor(R.color.color_333)
                } else {
                    var cal = Calendar.getInstance()
                    cal.time = date
                    var day = cal.get(Calendar.DAY_OF_MONTH)
                    mBinding.tvActCalSelect.text = "${day}"
                    // 2020-09-11 产品经理@栾建 修改未之前时间也可以点击
//                    if (DateUtil.isBeforeNowV2(dateTime)) {
//                        mBinding.tvActCalSelect.textColor = context!!.resources.getColor(R.color.color_999)
//                        mBinding.vActCalIndtor.visibility = View.GONE
//                    } else {
//                        mBinding.tvActCalSelect.textColor = context!!.resources.getColor(R.color.color_333)
//                        mBinding.vActCalIndtor.visibility = View.VISIBLE

//                    }
                }
                if (it.activityCount > 0) {
                    mBinding.vActCalIndtor.visibility = View.VISIBLE
                    mBinding.tvActCalSelect.textColor = context!!.resources.getColor(R.color.color_333)
                } else {
                    mBinding.tvActCalSelect.textColor = context!!.resources.getColor(R.color.color_999)
                    mBinding.vActCalIndtor.visibility = View.GONE
                }
                if (!selectActCalDateTime.isNullOrEmpty() && selectActCalDateTime == it.dateTime) {
                    mBinding.vActCalIndtor.visibility = View.GONE
                    mBinding.tvActCalSelect.textColor = context!!.resources.getColor(R.color.white)
                    mBinding.tvActCalSelect.setBackgroundResource(R.drawable.shape_select_act_cal_circle)
                } else {
                    mBinding.tvActCalSelect.setBackgroundResource(R.drawable.shape_select_act_cal_white_circle)
                }
                mBinding.root.onNoDoubleClick {
                    //                    if (!DateUtil.isBeforeNowV2(dateTime)) {
                    selectActCalDateTime = dateTime
                    notifyDataSetChanged()
                    var outPos: Int = position + 4
                    var size: Int = getData().size
                    var maxPos: Int = if (outPos < size) {
                        outPos
                    } else {
                        size - 1
                    }
                    var datas: MutableList<ActivityCalenderBean> = mutableListOf()
                    for (pos in position..maxPos) {
                        datas.add(getData()[pos])
                    }
                    onSelectActCalListener?.onselectActCall(datas)
                }
            }
             else {
                mBinding.tvActCalSelect.text = ""
                mBinding.vActCalIndtor.visibility = View.GONE
            }
        }
    }

    interface OnSelectActCalListener {
        fun onselectActCall(datas: MutableList<ActivityCalenderBean>)
    }
}