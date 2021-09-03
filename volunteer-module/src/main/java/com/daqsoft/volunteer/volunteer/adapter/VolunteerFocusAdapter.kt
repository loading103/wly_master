package com.daqsoft.volunteer.volunteer.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerTeamBean
import com.daqsoft.volunteer.databinding.VolunteerFcItemBinding
import com.daqsoft.volunteer.databinding.VolunteerListItemBinding
import com.daqsoft.volunteer.utils.StringUtils

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:我的关注适配器
 **/
class VolunteerFocusAdapter(context: Context):RecyclerViewAdapter<VolunteerFcItemBinding, VolunteerActivityBean>(R.layout.volunteer_fc_item) {
    val mContext = context
    @SuppressLint("ResourceType", "SetTextI18n")
    override fun setVariable(
        mBinding: VolunteerFcItemBinding,
        position: Int,
        item: VolunteerActivityBean
    ) {


    }
}