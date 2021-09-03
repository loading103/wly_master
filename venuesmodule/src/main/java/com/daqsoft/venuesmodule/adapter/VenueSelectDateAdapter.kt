package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueSelectDateBinding
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor
import java.lang.Exception
import java.util.*

/**
 * @Description
 * @ClassName   VenueSelectDateAdapter
 * @Author      luoyi
 * @Time        2020/7/8 9:23
 */
class VenueSelectDateAdapter : RecyclerViewAdapter<ItemVenueSelectDateBinding, VenueDateInfo> {

    var mContext: Context? = null
    /**
     * 默认选择第一个
     */
    var selectPos: Int = -1
    /**
     * 场馆id
     */
    var venueId: String = ""

    /**
     * 场馆选择日期
     */
    var onVenueSelectDateListener: OnVenueSelectDateListener? = null

    constructor(context: Context, venueId: String) : super(R.layout.item_venue_select_date) {
        this.mContext = context
        emptyViewShow = false
        this.venueId = venueId
    }

    override fun setVariable(mBinding: ItemVenueSelectDateBinding, position: Int, item: VenueDateInfo) {
        item?.let {
            mBinding.tvVenueSelectDate.text = "" + DateUtil.formatDateByString("MM月dd日", "yyyy-MM-dd", item.date)
            mBinding.tvVenueDateInfo.text = getDateInfo(item.date)
            mBinding.tvVenueStock.text = "余" + item.num

            if (selectPos == position) {
                mBinding.tvVenueStock.textColor = mContext!!.resources.getColor(R.color.c_36cd64)
                mBinding.tvVenueDateInfo.textColor = mContext!!.resources.getColor(R.color.c_36cd64)
                mBinding.tvVenueSelectDate.textColor = mContext!!.resources.getColor(R.color.c_36cd64)
                mBinding.root.backgroundDrawable = mContext!!.getDrawable(R.drawable.shape_select_date)
            } else {
                mBinding.tvVenueStock.textColor = mContext!!.resources.getColor(R.color.color_333)
                mBinding.tvVenueDateInfo.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvVenueSelectDate.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.root.backgroundDrawable = mContext!!.getDrawable(R.drawable.shape_default_date)
            }
            if (it.openStatus != 1 || it.num == 0) {
                mBinding.tvVenueStock.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvVenueDateInfo.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvVenueSelectDate.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.root.backgroundDrawable = mContext!!.getDrawable(R.drawable.shape_forbit_date)
                if (it.num == 0) {
                    mBinding.tvVenueStock.text = "约满"
                }
                if (it.openStatus == 0) {
                    mBinding.tvVenueStock.text = "闭馆"
                }
                if (it.openStatus == -1) {
                    mBinding.tvVenueStock.text = "不可预约"
                }
            }
            if (it.type == 0) {
                mBinding.llvDateInfo.visibility = View.VISIBLE
                mBinding.rvMoreDate.visibility = View.GONE
            } else {
                mBinding.llvDateInfo.visibility = View.INVISIBLE
                mBinding.rvMoreDate.visibility = View.VISIBLE
                mBinding.tvVenueStock.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvVenueDateInfo.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvVenueSelectDate.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.root.backgroundDrawable = mContext!!.getDrawable(R.drawable.shape_default_date)
            }
        }
        mBinding.root.onNoDoubleClick {
            if (item.type == 1) {
                onVenueSelectDateListener?.onToSelectDate()
            } else {
                if (selectPos != position) {
                    if (item.openStatus == 1) {
                        if (item.num == 0) {
                            ToastUtils.showMessage("库存不足，无法预约")
                        } else {
                            selectPos = position
                            notifyDataSetChanged()
                            onVenueSelectDateListener?.onChangedDate(item)
                        }
                    } else {
                        when (item.openStatus) {
                            0 -> {
                                ToastUtils.showMessage("该日闭馆，无法预约")
                            }
                            2 -> {
                                onVenueSelectDateListener?.onErrorTip(2)
                            }
                            3 -> {
                                onVenueSelectDateListener?.onErrorTip(3)
                            }
                            -1 -> {
                                ToastUtils.showMessage("未找到预约信息~")
                            }

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

    interface OnVenueSelectDateListener {
        fun onErrorTip(code: Int)
        fun onChangedDate(item: VenueDateInfo)
        fun onToSelectDate()
    }

}