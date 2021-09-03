package com.daqsoft.provider.businessview.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.SubChanelChildBean
import com.daqsoft.provider.bean.SubChannelBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.databinding.ItemProviderRecommendTabBinding

/**
 * @Description
 * @ClassName   ProviderActTabAdapter
 * @Author      luoyi
 * @Time        2020/6/9 14:46
 */
class ProviderActSubTabAdapter : RecyclerViewAdapter<ItemProviderRecommendTabBinding, SubChanelChildBean> {

    var context: Context? = null
    var selectPos: Int = 0
    var onItemClickListener: OnItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_provider_recommend_tab) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemProviderRecommendTabBinding, position: Int, item:SubChanelChildBean) {
        mBinding.name = item.name
        if (selectPos == position) {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_rec_tab_12_selected)
            mBinding.txtTabRecommend.setTextColor( context!!.resources.getColor(R.color.white))
        } else {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_act_tab_12_unselect)
            mBinding.txtTabRecommend.setTextColor(context!!.resources.getColor(R.color.txt_black))
        }
        mBinding.root.onNoDoubleClick {
            if (selectPos != position) {
                selectPos = position
                notifyDataSetChanged()
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }
}