package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.GuideExplainCost
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemCttrExhallRulesBinding
import org.jetbrains.anko.backgroundColor

/**
 * @Description
 * @ClassName   CttrExhallRulesAdapter
 * @Author      luoyi
 * @Time        2020/7/10 14:18
 */
class CttrExhallRulesAdapter : RecyclerViewAdapter<ItemCttrExhallRulesBinding, GuideExplainCost> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_cttr_exhall_rules) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: ItemCttrExhallRulesBinding, position: Int, item: GuideExplainCost) {
        item?.let {
            mBinding.tvExhallPersonNum.text = "${item.minNum}-${item.maxNum}人"
            mBinding.tvExhallCnCost.text = "${item.chnExplain}元/展厅"
            mBinding.tvExhallEnCost.text = "${item.engExplain}元/展厅"
            if (position % 2 == 0) {
                mBinding.root.backgroundColor = mContext!!.resources.getColor(R.color.color_fafafa)
            } else {
                mBinding.root.backgroundColor = mContext!!.resources.getColor(R.color.color_f5f5f5)
            }
        }
    }
}