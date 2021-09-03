package com.daqsoft.volunteer.volunteer.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.databinding.VolunteerListItemBinding
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:TODO
 **/
class VolunteerAdapter(context: Context):RecyclerViewAdapter<VolunteerListItemBinding,VolunteerBean>(R.layout.volunteer_list_item) {
    val mContext = context
    @SuppressLint("ResourceType")
    override fun setVariable(
        mBinding: VolunteerListItemBinding,
        position: Int,
        item: VolunteerBean
    ) {
        mBinding.item = item
        Glide.with(mContext).load(item.head).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivHeader)
        mBinding.tvName.text = item.name
        mBinding.tvAddress.text = item.serviceRegionName
        mBinding.tvTeam.text = item.getTeamName()
        mBinding.tvServiceCount.text = item.serviceNum.toString()
        mBinding.tvServiceScore.text = item.cumulativeIntegral.toString()
        mBinding.tvServiceTime.text = item.serviceTime.toString()
        if(item.level != 0){
        val logos = mContext.resources.obtainTypedArray(R.array.volunteer_module_level_logo)
        val colors = mContext.resources.getIntArray(R.array.volunteer_module_level_color)
        val titles = mContext.resources.getStringArray(R.array.volunteer_module_level_title)
            val drawable = mContext.resources.getDrawable(logos.getResourceId(item.level+1,0))
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight);
            mBinding.tvLevel.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
            mBinding.tvLevel.compoundDrawablePadding = 4
            mBinding.tvLevel.setTextColor(colors[item.level+1])
            mBinding.tvLevel.text = titles[item.level+1]
        }
        mBinding.root.onNoDoubleClick {
            JumpUtils.gotoVolunteerDetail(item.id)
        }
    }
}