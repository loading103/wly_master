package com.dqsoft.votemodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.VoteTypeBean
import com.daqsoft.provider.databinding.ItemProviderRecommendTabBinding
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ItemVoteTypeBinding

/**
 * @Description
 * @ClassName   VoteTypeAdapter
 * @Author      luoyi
 * @Time        2020/11/13 17:20
 */
class VoteTypeAdapter : RecyclerViewAdapter<ItemVoteTypeBinding, VoteTypeBean> {

    var context: Context? = null
    var selectPos: Int = 0
    var onItemClickListener: OnItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_vote_type) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemVoteTypeBinding, position: Int, item: VoteTypeBean) {
        mBinding.name = item.name
        if (selectPos == position) {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_rec_tab_12_selected)
            mBinding.txtTabRecommend.setTextColor(context!!.resources.getColor(R.color.white))
        } else {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_act_tab_12_unselect)
            mBinding.txtTabRecommend.setTextColor(context!!.resources.getColor(R.color.txt_black))
        }
        mBinding.root.onNoDoubleClick {
            if (selectPos != position) {
                selectPos = position
                notifyDataSetChanged()
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClick(position, item)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int, item: VoteTypeBean)
    }
}