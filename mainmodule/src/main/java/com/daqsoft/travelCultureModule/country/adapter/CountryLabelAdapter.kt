package com.daqsoft.travelCultureModule.country.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicLabelDetailsBinding
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean

/**
 * desc :标签适配器
 * @author 江云仙
 * @date 2020/4/15 20:17
 */
class CountryLabelAdapter : RecyclerViewAdapter<ItemScenicLabelDetailsBinding, ScenicLabelBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_scenic_label_details) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemScenicLabelDetailsBinding, position: Int, item: ScenicLabelBean) {
        mBinding.item = item
        when (
            item.type) {
            // 营业中
            1 -> {
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.country_bdbdbd_stroke_1_round_3)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.color_333))
            }
            2 -> {
                // 星级农家乐
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.country_ff9e05_stroke_1_round_2)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.ff9e05))
            }
            3 -> {
                // 其它标签
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.country_ff6b53_stroke_1_round_3)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.color_ff6b53))
            }
        }
    }

}