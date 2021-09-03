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
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
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
class VolunteerTeamAdapter(context: Context):RecyclerViewAdapter<VolunteerTeamListItemBinding,VolunteerTeamListBean>(R.layout.volunteer_team_list_item) {
    val mContext = context

    @SuppressLint("ResourceAsColor")
    override fun setVariable(
        mBinding: VolunteerTeamListItemBinding,
        position: Int,
        item: VolunteerTeamListBean
    ) {
        Glide.with(mContext).load(item.teamIcon).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivHeader)
        mBinding.tvName.text = item.teamName
        if(item.openSignIn){
            mBinding.tvSign.visibility = View.VISIBLE
        }else{
            mBinding.tvSign.visibility = View.GONE
        }
        mBinding.tvTeamNum.text = item.teamMemberNum.toString()
        mBinding.tvServiceCount.text = item.teamServiceNum.toString()
        mBinding.tvServiceTime.text = item.teamServiceTime.toString()
        if(item.activityName.isNotEmpty()){
            mBinding.tvActivity.visibility = View.VISIBLE
            val str = "活动：${item.activityName}"
            val spannableString = SpannableString(str)
            val fontSpan = ForegroundColorSpan(mContext.resources.getColor(R.color.volunteer_module_tip_txt))
            spannableString.setSpan(fontSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            mBinding.tvActivity.text = spannableString
        }else{
            mBinding.tvActivity.visibility = View.GONE
        }
        var res = ""
        if(item.level != "0"){
             res = StringUtils.getVolunteerTeamLevelStr(item.level)
        }
        if(res.isNotEmpty()){
           mBinding.tvAddress.text = DividerTextUtils.convertString(StringBuilder(),res,item.teamRegionName)
        }else{
            mBinding.tvAddress.text = item.teamRegionName
        }

        mBinding.root.onNoDoubleClick {
            JumpUtils.gotoVolunteerTeamDetail(item.id)
        }
    }
}