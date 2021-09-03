package com.daqsoft.venuesmodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueActivityTagBinding
import org.jetbrains.anko.textColor

/**
 * @Description 标签适配器
 * @ClassName   VenueTagsAdapter
 * @Author      luoyi
 * @Time        2020/3/27 14:33
 */
class VenueTagsAdapter : RecyclerViewAdapter<ItemVenueActivityTagBinding, String> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_venue_activity_tag ) {
        this.mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemVenueActivityTagBinding, position: Int, item: String) {

        if (item == "诚信免审" || item == "诚信优享") {
            //诚信
            mBinding.tvVenueActivityTag.background =
                mContext!!.getDrawable(R.drawable.home_b_36cd64_stroke_null_round_2)
            mBinding.tvVenueActivityTag.textColor = mContext!!.resources.getColor(R.color.white)

            if (item == "诚信优享") {
                var d = mContext!!.resources.getDrawable(R.mipmap.venue_activity_enjoy)
                mBinding.tvVenueActivityTag.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
            } else {
                var d = mContext!!.resources.getDrawable(R.mipmap.venue_activity_exempt)
                mBinding.tvVenueActivityTag.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
            }
        } else if (item.startsWith("还剩")) {
            // 剩余
            mBinding.name = item
            mBinding.tvVenueActivityTag.background =
                mContext!!.getDrawable(R.drawable.home_b_ff9e05_stroke_null_round_2)
            mBinding.tvVenueActivityTag.textColor = mContext!!.resources.getColor(R.color.white)
        } else {
            // tag
            mBinding.name = item
            mBinding.tvVenueActivityTag.background =
                mContext!!.getDrawable(R.drawable.home_b_white_stroke_36cd64_round_2)
            mBinding.tvVenueActivityTag.textColor = mContext!!.resources.getColor(R.color.colorPrimary)
        }
    }
}