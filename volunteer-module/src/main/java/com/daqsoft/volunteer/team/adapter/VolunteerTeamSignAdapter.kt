package com.daqsoft.volunteer.team.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.SignInDateList
import com.daqsoft.volunteer.bean.SignTimeBean
import com.daqsoft.volunteer.bean.VolunteerTeamSignInBean
import com.daqsoft.volunteer.databinding.VolunteerTeamSignListItemBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamSignTimeItemBinding
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.volunteer.team.adapter
 *@date:2020/6/12 9:17
 *@author: caihj
 *@des:团队签到适配器
 **/
class VolunteerTeamSignAdapter(context: Context):RecyclerViewAdapter<VolunteerTeamSignListItemBinding, VolunteerTeamSignInBean>(
    R.layout.volunteer_team_sign_list_item) {
    val mContext = context
    override fun setVariable(
        mBinding: VolunteerTeamSignListItemBinding,
        position: Int,
        item: VolunteerTeamSignInBean
    ) {
        mBinding.item = item
        Glide.with(mContext).load(item.teamIcon).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivHeader)
        val timeAdapter = object :RecyclerViewAdapter<VolunteerTeamSignTimeItemBinding,SignTimeBean>(R.layout.volunteer_team_sign_time_item){
            override fun setVariable(
                mBinding: VolunteerTeamSignTimeItemBinding,
                position: Int,
                item: SignTimeBean
            ) {
                mBinding.tvTimeTitle.text = item.week
                mBinding.tvTime.text =item.time
            }
        }
        mBinding.rvServiceTime.layoutManager =GridLayoutManager(mContext,2)
        mBinding.rvServiceTime.adapter =timeAdapter
        val times = parseDateTime(item.signInDateList)
        if(times.isEmpty()){
            mBinding.rvServiceTime.visibility = View.GONE
        }else{
            timeAdapter.add(times)
        }
    }

    private fun parseDateTime(signInDateList:SignInDateList):MutableList<SignTimeBean>{
        val times = mutableListOf<SignTimeBean>()
        if(signInDateList.monday.isNotEmpty())
        times.add(SignTimeBean("周一:",parseTimeStr(signInDateList.monday)))
        if(signInDateList.tuesday.isNotEmpty())
        times.add(SignTimeBean("周二:",parseTimeStr(signInDateList.tuesday)))
        if(signInDateList.wednesday.isNotEmpty())
        times.add(SignTimeBean("周三:",parseTimeStr(signInDateList.wednesday)))
        if(signInDateList.thursday.isNotEmpty())
        times.add(SignTimeBean("周四:",parseTimeStr(signInDateList.thursday)))
        if(signInDateList.friday.isNotEmpty())
        times.add(SignTimeBean("周五:",parseTimeStr(signInDateList.friday)))
        if(signInDateList.saturday.isNotEmpty())
        times.add(SignTimeBean("周六:",parseTimeStr(signInDateList.saturday)))
        if(signInDateList.sunday.isNotEmpty())
        times.add(SignTimeBean("周日:",parseTimeStr(signInDateList.sunday)))
        return times
    }

    private fun parseTimeStr(days:List<String>):String{
        val strs = StringBuilder()
        for (index in days.indices){
            if(index+1 != days.size){
                strs.append(parseEn(days[index])+"、")
            }else{
                strs.append(parseEn(days[index]))
            }
        }
        return  strs.toString()
    }

    private fun parseEn(str:String):String = when(str){
        "morning" -> "上午"
        "afternoon" -> "下午"
        "night" -> "晚上"
        else -> "未知"
    }
}