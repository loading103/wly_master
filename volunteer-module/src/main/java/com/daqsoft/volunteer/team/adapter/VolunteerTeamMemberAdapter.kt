package com.daqsoft.volunteer.team.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerMemberBean
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
import com.daqsoft.volunteer.databinding.TeamMemberItemBinding
import com.daqsoft.volunteer.databinding.VolunteerListItemBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamListItemBinding
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:TODO
 **/
class VolunteerTeamMemberAdapter(context: Context):RecyclerViewAdapter<TeamMemberItemBinding,VolunteerMemberBean>(R.layout.team_member_item) {
    val mContext = context

    @SuppressLint("ResourceAsColor")
    override fun setVariable(
        mBinding: TeamMemberItemBinding,
        position: Int,
        item: VolunteerMemberBean
    ) {
        Glide.with(mContext).load(item.head).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.rvHead)
        if(item.manage){
            mBinding.tvLabel.visibility = View.VISIBLE
        }else{
            mBinding.tvLabel.visibility = View.GONE
        }
        mBinding.tvName.text = item.name
        val colors = mContext.resources.getIntArray(R.array.volunteer_module_level_color)
        val titles = mContext.resources.getStringArray(R.array.volunteer_module_level_title)
        mBinding.tvLevel.setTextColor(colors[item.level ])
        mBinding.tvLevel.text = titles[item.level ]
        val services = mutableListOf<String>()
        if(item.serviceNum != 0){
            services.add("服务${item.serviceNum}次")
        }
        if(item.serviceTime != 0){
            services.add("服务${item.serviceTime}小时")
        }
        if(item.cumulativeIntegral != 0){
            services.add("公益积分${item.cumulativeIntegral}分")
        }
        mBinding.tvNum.text = services.joinToString("  |  ")

        mBinding.root.onNoDoubleClick {
            JumpUtils.gotoVolunteerDetail(item.id)
        }
    }
}