package com.daqsoft.volunteer.volunteer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.ServiceRecordBean
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerTeamBean
import com.daqsoft.volunteer.databinding.VolunteerFcItemBinding
import com.daqsoft.volunteer.databinding.VolunteerListItemBinding
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:志愿风采适配器
 **/
class VolunteerFengCaiAdapter(context: Context):RecyclerViewAdapter<VolunteerFcItemBinding, ServiceRecordBean>(R.layout.volunteer_fc_item) {
    val mContext = context
    @SuppressLint("ResourceType", "SetTextI18n")
    override fun setVariable(
        mBinding: VolunteerFcItemBinding,
        position: Int,
        item: ServiceRecordBean
    ) {

        Glide.with(mContext).load(item.volunteerHead).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
        mBinding.tvUser.text = item.volunteerName
        if(item.volunteerLevel != 0){
            val colors = mContext.resources.getIntArray(R.array.volunteer_module_level_color)
            val bg = mContext.resources.obtainTypedArray(R.array.volunteer_module_level_bg)
            mBinding.tvLevelLabel.setTextColor(colors[item.volunteerLevel-1])
            mBinding.tvLevelLabel.text = StringUtils.getVolunteerTeamLevelStr(item.volunteerLevel.toString())
            mBinding.tvLevelLabel.background = mContext.resources.getDrawable(bg.getResourceId(item.volunteerLevel-1,0))
        }
        mBinding.tvTime.text = item.createTime
        mBinding.tvTitleName.text = item.content
        if(item.activity.name.isNotEmpty()){
            mBinding.tvActivity.visibility = View.VISIBLE
            mBinding.tvActivity.text = item.activity.name
        }else{
            mBinding.tvActivity.visibility = View.GONE
        }
        if(item.activity.regionNameCityStr.isNotEmpty()){
            mBinding.tvLocation.visibility = View.VISIBLE
            mBinding.tvLocation.text = item.activity.regionNameCityStr
        }else{
            mBinding.tvLocation.visibility = View.GONE
        }
        mBinding.tvShow.text = item.resourceNum.showNum.toString()
        mBinding.tvLike.text = item.resourceNum.likeNum.toString()
        mBinding.tvComment.text = item.resourceNum.commentNum.toString()

        mBinding.miImages.setImages(item.images.split(",") as MutableList<String>,item.coverVideos)
        mBinding.root.onNoDoubleClick {
            JumpUtils.gotoServiceRecordDetail(item.id)
        }
    }
}