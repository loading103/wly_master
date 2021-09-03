package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.GuideExhibition
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemCommentatorExhallBinding
import kotlin.math.max

/**
 * @Description
 * @ClassName   CttrExhallAdapter
 * @Author      luoyi
 * @Time        2020/7/6 18:57
 */
class CttrExhallAdapter : RecyclerViewAdapter<ItemCommentatorExhallBinding, GuideExhibition> {
    var mContext: Context? = null
    var maxSelect: Int = 1
    var onSelectExhallListener: OnSelectExhallListener? = null

    constructor(context: Context) : super(R.layout.item_commentator_exhall) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemCommentatorExhallBinding, position: Int, item: GuideExhibition) {
        var lastPos = getData().size - 1
        item?.let {
            mBinding.name = it.name
            if (item.isSelect) {
                mBinding.imgIsSelect.setBackground(mContext!!.resources!!.getDrawable(R.mipmap.venue_book_jieshuo_button_choose_selected))
            } else {
                mBinding.imgIsSelect.setBackground(mContext!!.resources!!.getDrawable(R.mipmap.venue_book_jieshuo_button_choose_normal))
            }
        }
        if (position == lastPos) {
            mBinding.lineCommentatorExhall.visibility = View.GONE
        } else {
            mBinding.lineCommentatorExhall.visibility = View.VISIBLE
        }
        if (item.recommend == 1) {
            mBinding.tvRecommendStatus.visibility = View.VISIBLE
        } else {
            mBinding.tvRecommendStatus.visibility = View.GONE
        }
        mBinding?.root?.onNoDoubleClick {
            var size = getSelectExhallSize()
            if (!item.isSelect) {
                if (size < maxSelect) {
                    getData()[position].isSelect = true
                    notifyDataSetChanged()
                    onSelectExhallListener?.onSelectExhallListener(getSelectExhallSize())
                } else {
                    ToastUtils.showMessage("最多选择${maxSelect}个展厅")
                }
            } else {
                // 取消
                if (size > 1) {
                    getData()[position].isSelect = false
                    notifyDataSetChanged()
                    onSelectExhallListener?.onSelectExhallListener(getSelectExhallSize())
                } else {
                    ToastUtils.showMessage("最少选择1个展厅")
                }

            }
        }
    }

    fun getSelectExhallSize(): Int {
        var selectSize = 0
        for (item in getData()) {
            if (item.isSelect) {
                selectSize += 1
            }
        }
        return selectSize
    }

    fun getSelectExhallList(): String {
        var selectGuideExHall: MutableList<String> = mutableListOf()
        for (item in getData()) {
            if (item.isSelect) {
                selectGuideExHall.add(item.id.toString())
            }
        }
        return DividerTextUtils.convertCommaString(selectGuideExHall)
    }

    interface OnSelectExhallListener {
        fun onSelectExhallListener(size: Int)
    }
}