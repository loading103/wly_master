package com.daqsoft.provider.uiTemplate.titleBar.activity

import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.databinding.ItemHomePopularActivityImgBinding
import com.daqsoft.provider.getRealImageUrl
import com.daqsoft.provider.view.convenientbanner.listener.OnItemClickListener

/**
 * @Description
 * @ClassName   ActivitiesStyleFourAdapter
 * @Author      luoyi
 * @Time        2020/12/8 17:45
 */
class ActivitiesStyleFiveAdapter : RecyclerViewAdapter<ItemHomePopularActivityImgBinding, ActivityBean>(R.layout.item_home_popular_activity_img) {

    var onActivityItemClickListener: OnActivityItemClickListener? = null

    override fun setVariable(mBinding: ItemHomePopularActivityImgBinding, position: Int, item: ActivityBean) {
        item?.let {
            Glide.with(mBinding.imgActivity)
                .load(item.images.getRealImageUrl())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgActivity)
            mBinding.root.onNoDoubleClick {
                onActivityItemClickListener?.OnItemCLick(item,position)
            }
        }

    }

    interface OnActivityItemClickListener {
        fun OnItemCLick(item: ActivityBean,position: Int)
    }
}