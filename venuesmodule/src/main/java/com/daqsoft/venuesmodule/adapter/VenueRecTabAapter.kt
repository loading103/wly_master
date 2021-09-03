package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueRecommendTabBinding
import org.jetbrains.anko.textColor

/**
 * @Description 场馆推荐tab适配器
 * @ClassName   VenueRecTabAapter
 * @Author      luoyi
 * @Time        2020/3/27 17:54
 */
class VenueRecTabAapter : RecyclerViewAdapter<ItemVenueRecommendTabBinding, ValueKeyBean> {

    var context: Context? = null
    var selectPos: Int = 0
    var onItemClickListener: OnItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_venue_recommend_tab) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemVenueRecommendTabBinding, position: Int, item: ValueKeyBean) {
        mBinding.name = item.name
        if (selectPos == position) {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_rec_tab_12_selected)
            mBinding.txtTabRecommend.textColor = context!!.resources.getColor(R.color.white)
        } else {
            mBinding.txtTabRecommend.setBackgroundResource(R.drawable.shape_provider_rec_tab_12_unselect)
            mBinding.txtTabRecommend.textColor = context!!.resources.getColor(R.color.txt_black)
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