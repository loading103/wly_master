package com.daqsoft.travelCultureModule.resource.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicLabelDetailsBinding
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean

/**
 * @Description 景区标签适配器
 * @ClassName   ScenicLabelAdapter
 * @Author      luoyi
 * @Time        2020/3/31 9:30
 */
class ScenicLabelAdapter : RecyclerViewAdapter<ItemScenicLabelDetailsBinding, ScenicLabelBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_scenic_label_details) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemScenicLabelDetailsBinding, position: Int, item: ScenicLabelBean) {
        mBinding.item = item
        when (
            item.type) {
            // level
            1 -> {
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.shape_label_primary_color_bg_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.colorPrimary))
            }
            2 -> {
                // 诚信分
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.shape_label_primary_color_bg_2)

                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.colorPrimary))
            }
            3 -> {
                // 其它标签
                mBinding.tvName.background = mContext!!.resources.getDrawable(R.drawable.shape_label_second_color_bg_2)
                mBinding.tvName.setTextColor(mContext!!.resources.getColor(R.color.btn_txt))
            }
        }
    }

}