package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.TimeAppointBean
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.provider.databinding.ItemProviderTimeAppointBinding
import java.lang.Exception
import java.util.*

/**
 * @Description
 * @ClassName   ProviderTImeAppointAdapter
 * @Author      luoyi
 * @Time        2020/7/13 17:03
 */
class ProviderTImeAppointAdapter : RecyclerViewAdapter<ItemProviderTimeAppointBinding, TimeAppointBean> {
    var mContext: Context? = null
    var onSelectDateListener: OnSelectDateListener? = null

    constructor(context: Context) : super(R.layout.item_provider_time_appoint) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemProviderTimeAppointBinding, position: Int, item: TimeAppointBean) {
        item?.let {
            mBinding.tvVenueSelectDate.text = "" + DateUtil.formatDateByString("MM月dd日", "yyyy-MM-dd", item.date)
            mBinding.tvVenueDateInfo.text = getDateInfo(item.date)
            mBinding.tvVenueStock.text = "余" + item.maxNum

            if (it.openStatus != 1 || it.maxNum <= 0) {
                mBinding.tvVenueStock.setTextColor(mContext!!.resources.getColor(R.color.color_666))
                mBinding.tvVenueDateInfo.setTextColor(mContext!!.resources.getColor(R.color.color_666))
                mBinding.tvVenueSelectDate.setTextColor(mContext!!.resources.getColor(R.color.color_999))
                mBinding.root.background = mContext!!.getDrawable(R.drawable.shape_forbit_date)
                if (it.maxNum <= 0) {
                    mBinding.tvVenueStock.text = "约满"
                }
                if (it.openStatus == 0) {
                    mBinding.tvVenueStock.text = "闭馆"
                }
                if (it.openStatus == -1 || it.openStatus == 2 || it.openStatus == 3) {
                    mBinding.tvVenueStock.text = "不可预约"
                }
                mBinding.imgIsCanBook.visibility = View.GONE
            } else {
                mBinding.imgIsCanBook.visibility = View.VISIBLE
                mBinding.tvVenueStock.setTextColor(mContext!!.resources.getColor(R.color.color_666))
                mBinding.tvVenueDateInfo.setTextColor(mContext!!.resources.getColor(R.color.color_666))
                mBinding.tvVenueSelectDate.setTextColor(mContext!!.resources.getColor(R.color.color_666))
                mBinding.root.background = mContext!!.getDrawable(R.drawable.shape_default_date)
            }
        }
        mBinding.root.onNoDoubleClick {
            if (item.openStatus == 1) {
                onSelectDateListener?.onToSelectDate(item)
            } else {
                if (item.openStatus == 0) {
                    ToastUtils.showMessage("闭馆，无法预约")
                } else {
                    when (item.openStatus) {
                        2 -> {
                            ToastUtils.showMessage("非常抱歉，该时段不可以预约")
//                                    onVenueSelectDateListener?.onErrorTip(2)
                        }
                        3 -> {
                            ToastUtils.showMessage("非常抱歉，该时段不可以预约")
//                                    onVenueSelectDateListener?.onErrorTip(3)
                        }
                        -1 -> {
                            ToastUtils.showMessage("未找到预约信息~")
                        }
                    }
                }
            }
        }
    }

    private fun getDateInfo(date: String): CharSequence? {
        if (!date.isNullOrEmpty()) {
            try {
                var selectDate = DateUtil.getFormatDateByString("yyyy-MM-dd", date)
                var isTopDay = DateUtil.isSameDate(Date(), selectDate)
                if (isTopDay) {
                    return "今天"
                }
                var isNextDay = DateUtil.isSameDate(DateUtil.getNextDate(), selectDate)
                if (isNextDay) {
                    return "明天"
                }
                return DateUtil.getDayOfWeekV2(selectDate)
            } catch (e: Exception) {

            }
        }
        return ""


    }

    interface OnSelectDateListener {
        fun onErrorTip(code: Int)
        fun onChangedDate(item: VenueDateInfo)
        fun onToSelectDate(item: TimeAppointBean)
    }
}