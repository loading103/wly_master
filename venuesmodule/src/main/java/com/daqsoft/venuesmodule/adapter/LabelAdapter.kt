package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.databinding.ItemLabelDetailsBinding
import com.daqsoft.provider.network.venues.bean.LabelBean
import com.daqsoft.venuesmodule.R

/**
 * @Description
 * @ClassName   LabelAdapter
 * @Author      luoyi
 * @Time        2020/3/25 9:30
 */
class LabelAdapter : RecyclerViewAdapter<ItemLabelDetailsBinding, LabelBean> {
    var mContext: Context? = null

    constructor(context: Context, labels: MutableList<LabelBean>) : super(R.layout.item_label_details, labels) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemLabelDetailsBinding, position: Int, item: LabelBean) {
        mBinding.item = item
        when (// 支付方式
            item.type) {
            0 -> {
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.venue_b_grey_stroke_null_round_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.txt_black))
            }
            1 -> {
                // 博物馆类型
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.venue_b_36cd64_stroke_1_round_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.colorPrimary))
            }
            2 -> {
                // 标签
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.venue_b_ff9e05_stroke_1_round_2)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.ff9e05))
            }
        }
    }

}