package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemCommentatorLanguageBinding
import org.jetbrains.anko.textColor

/**
 * @Description
 * @ClassName   LaguageAdapter
 * @Author      luoyi
 * @Time        2020/7/6 16:28
 */
class CttrLanguageAdapter : RecyclerViewAdapter<ItemCommentatorLanguageBinding, ValueKeyBean> {
    var mContext: Context? = null
    var selectPos: Int = 0
    var onCttrLgListener:OnCttrLgListener?=null
    constructor(context: Context) : super(R.layout.item_commentator_language) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemCommentatorLanguageBinding, position: Int, item: ValueKeyBean) {
        item?.let {
            if (selectPos == position) {
                mBinding.tvCommentatorLgName.textColor = mContext!!.resources.getColor(R.color.c_36cd64)
                mBinding.tvCommentatorLgName.setBackgroundResource(R.drawable.shape_ctt_lg_selected_r3)
            } else {
                mBinding.tvCommentatorLgName.textColor = mContext!!.resources.getColor(R.color.color_333)
                mBinding.tvCommentatorLgName.setBackgroundResource(R.drawable.shape_ctt_lg_default_r3)
            }
            mBinding.name = item.name
        }
        mBinding.root.onNoDoubleClick {
            selectPos = position
            notifyDataSetChanged()
            onCttrLgListener?.onClick(item.value)
        }
    }
    interface OnCttrLgListener{
        fun onClick(key:String)
    }
}