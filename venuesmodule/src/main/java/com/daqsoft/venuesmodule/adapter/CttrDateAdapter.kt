package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.GuideOrderTime
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemCommentatorLanguageBinding
import org.jetbrains.anko.textColor

/**
 * @Description 选择讲解日期段
 * @ClassName   CttrDateAdapter
 * @Author      luoyi
 * @Time        2020/7/6 16:36
 */
class CttrDateAdapter : RecyclerViewAdapter<ItemCommentatorLanguageBinding, GuideOrderTime> {
    var mContext: Context? = null

    var selectPos: Int = 0
    var onSelectDateListener: OnSelectDateListener? = null

    constructor(context: Context) : super(R.layout.item_commentator_language) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemCommentatorLanguageBinding, position: Int, item: GuideOrderTime) {
        item?.let {
            if (item.currTimeOrderStatus) {
                if (selectPos == position) {
                    mBinding.tvCommentatorLgName.textColor = mContext!!.resources.getColor(R.color.c_36cd64)
                    mBinding.tvCommentatorLgName.setBackgroundResource(R.drawable.shape_ctt_lg_selected_r3)
                } else {
                    mBinding.tvCommentatorLgName.textColor = mContext!!.resources.getColor(R.color.color_333)
                    mBinding.tvCommentatorLgName.setBackgroundResource(R.drawable.shape_ctt_lg_default_r3)
                }
            } else {
                mBinding.tvCommentatorLgName.textColor = mContext!!.resources.getColor(R.color.color_999)
                mBinding.tvCommentatorLgName.setBackgroundResource(R.drawable.shape_ctt_lg_default_r3)
            }
            mBinding.name = item.orderTimeSubStart + "-" + item.orderTimeSubEnd
        }
        mBinding.root.onNoDoubleClick {
            if (item.currTimeOrderStatus) {
                selectPos = position
                onSelectDateListener?.onSelectDate(item)
                notifyDataSetChanged()
            } else {
                ToastUtils.showMessage("非常抱歉，当前时段，不可预约")
            }
        }
    }

    interface OnSelectDateListener {
        fun onSelectDate(item: GuideOrderTime)
    }

    fun getSelectTimesId(): String {
        if (!getData().isNullOrEmpty() && selectPos < getData().size) {
            return getData()[selectPos].id.toString()
        }
        return ""
    }
}