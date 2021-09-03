package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActivityCanlenderIndexBinding
import com.daqsoft.provider.bean.ActivityCalenderBean
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import java.util.*

/**
 * @Description
 * @ClassName   ActivityCanlenderInAdapter
 * @Author      luoyi
 * @Time        2020/6/19 17:16
 */
class ActivityCanlenderInAdapter : RecyclerViewAdapter<ItemActivityCanlenderIndexBinding, ActivityCalenderBean> {

    var mContext: Context? = null

    var selectTime: String? = ""

    var onActCalItemClickListener: OnActCalItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_activity_canlender_index) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemActivityCanlenderIndexBinding, position: Int, item: ActivityCalenderBean) {
        item?.let {
            if (selectTime == it.dateTime) {
                mBinding.root.backgroundResource = R.drawable.shape_select_act_canlender
                mBinding.tvCalenderTime.textColor = mContext!!.resources.getColor(R.color.white)
                mBinding.tvCalenderWeek.textColor = mContext!!.resources.getColor(R.color.white)
                mBinding.vCalenderTip.backgroundResource = R.drawable.shape_cirle_s4_white
            } else {
                mBinding.root.backgroundResource = R.drawable.shape_unselect_act_canlender
                mBinding.tvCalenderTime.textColor = mContext!!.resources.getColor(R.color.color_333)
                mBinding.tvCalenderWeek.textColor = mContext!!.resources.getColor(R.color.color_333)
                mBinding.vCalenderTip.backgroundResource = R.drawable.shape_cirle_s4_ff9e05
            }
            var dateTime = it.dateTime
            if (dateTime != null && dateTime != "全部") {
                var date = DateUtil.getFormatDateByString("yyyy-MM-dd", dateTime)
                if (DateUtil.isSameDate(date, Date())) {
                    mBinding.tvCalenderWeek.text = "今天"
                } else {
                    var week: String? = DateUtil.getDayOfWeekV2(date)
                    mBinding.tvCalenderWeek.text = "${week}"
                }
                var dateInfo: String? = DateUtil.getDqDateString("MM.dd", date)
                mBinding.tvCalenderTime.text = "${dateInfo}"
            }else  if (dateTime != null && dateTime == "全部"){
                mBinding.tvCalenderWeek.text =dateTime
            }

            if (it.activityCount > 0) {
                mBinding.vCalenderTip.visibility = View.VISIBLE
            } else {
                mBinding.vCalenderTip.visibility = View.INVISIBLE
            }
            mBinding.root.onNoDoubleClick {
                selectTime = item.dateTime
                notifyDataSetChanged()
                onActCalItemClickListener?.selectItem(item)
            }
        }


    }

    interface OnActCalItemClickListener {
        fun selectItem(item: ActivityCalenderBean)
    }

}