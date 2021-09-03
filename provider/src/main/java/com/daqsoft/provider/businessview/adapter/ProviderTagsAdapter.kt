package com.daqsoft.provider.businessview.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemProviderTagsBinding
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean

/**
 * @Description
 * @ClassName   ProviderTagsAdapter
 * @Author      luoyi
 * @Time        2020/4/7 14:17
 */
class ProviderTagsAdapter :RecyclerViewAdapter<ItemProviderTagsBinding,ScenicLabelBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_provider_tags) {
        this.mContext = context
    }
    override fun setVariable(mBinding: ItemProviderTagsBinding, position: Int, item: ScenicLabelBean) {
        mBinding.item = item
        when (
            item.type) {
            // level
            1 -> {
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.provider_b_36cd64_stroke_1_round_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.colorPrimary))
            }
            2 -> {
                // 诚信分
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.provider_b_36cd64_stroke_1_round_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.colorPrimary))
            }
            3 -> {
                // 其它标签
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.provider_b_ff9e05_stroke_1_round_2)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.ff9e05))
            }
        }
    }
}