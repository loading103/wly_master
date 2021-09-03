package com.daqsoft.travelCultureModule.itrobot.adapter

import android.content.Context
import android.text.Html
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemItRobotResourceBinding
import com.daqsoft.provider.bean.ItRobotDataBean
import com.daqsoft.provider.bean.ItRobotRequestBean
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.travelCultureModule.clubActivity.getRealImages

/**
 * @Description
 * @ClassName   ItRobotResourceAdapter
 * @Author      luoyi
 * @Time        2020/5/23 17:09
 */
class ItRobotResourceAdapter : RecyclerViewAdapter<ItemItRobotResourceBinding, ItRobotRequestBean> {
    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_it_robot_resource) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemItRobotResourceBinding, position: Int, item: ItRobotRequestBean) {
        item?.let {
            if (it.floorPrice != 0.0) {
                mBinding.tvResourcePrice.text = "" + it.floorPrice
                mBinding.vResourcePrice.visibility = View.VISIBLE
            } else {
                mBinding.vResourcePrice.visibility = View.GONE
            }
            var imageUrl: String = ""
            if (!it.image.isNullOrEmpty()) {
                imageUrl = it.image.getRealImages()
            }
            Glide.with(mContext!!)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgResource)
            if(!it.name.isNullOrBlank() && it.name.contains("<em>")){
                mBinding.tvResourceName.text = "" + Html.fromHtml( it.name)
            }else{
                mBinding.tvResourceName.text = "" + it.name
            }
            if (position == itemCount - 1) {
                mBinding.vLineRobot.visibility = View.GONE
            } else {
                mBinding.vLineRobot.visibility = View.VISIBLE
            }
            mBinding.root.onNoDoubleClick {
                MenuJumpUtils.jumpResourceTypePage(
                    item.resourceType, item.resourceId.toString(), item.siteId.toString(),
                    item.contentType, item.jumpUrl
                )
            }
        }
    }
}