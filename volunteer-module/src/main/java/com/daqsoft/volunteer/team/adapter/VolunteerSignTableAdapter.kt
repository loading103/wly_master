package com.daqsoft.volunteer.team.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerTeamSignTableBean
import com.daqsoft.volunteer.databinding.VolunteerTeamServiceTimeTableBottomBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamServiceTimeTableContentBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamServiceTimeTableTimeBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamServiceTimeTableTitleBinding

/**
 *@package:com.daqsoft.volunteer.team.adapter
 *@date:2020/6/9 16:46
 *@author: caihj
 *@des:团队签到表格适配器
 **/
class VolunteerSignTableAdapter(val context: Context): MultipleRecyclerViewAdapter<ViewDataBinding, VolunteerTeamSignTableBean>() {

    val mContext = context


    override fun setVariable(
        mBinding: ViewDataBinding,
        position: Int,
        item: VolunteerTeamSignTableBean
    ) {
        when(mBinding){
            is VolunteerTeamServiceTimeTableTitleBinding ->{}
            is VolunteerTeamServiceTimeTableContentBinding -> {
                val img = when(position){
                    1-> R.mipmap.volunteer_team_details_schedule_morning
                    2 ->R.mipmap.volunteer_team_details_schedule_afternoon
                    else -> R.mipmap.volunteer_team_details_schedule_evening
                }
                val defaultImg = R.mipmap.volunteer_team_details_schedule_forbidden
                if(item.datas[0].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek1)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek1)
                }
                if(item.datas[1].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek2)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek2)
                }
                if(item.datas[2].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek3)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek3)
                }
                if(item.datas[3].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek4)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek4)
                }
                if(item.datas[4].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek5)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek5)
                }
                if(item.datas[5].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek6)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek6)
                }
                if(item.datas[6].toInt() > -1){
                    Glide.with(mContext).load(img).into(mBinding.ivWeek7)
                }else{
                    Glide.with(mContext).load(defaultImg).into(mBinding.ivWeek7)
                }
            }
            is VolunteerTeamServiceTimeTableTimeBinding ->{
                if(item.datas[0].isEmpty()){
                    mBinding.tvMorning.visibility = View.GONE
                }else{
                    mBinding.tvMorning.text = item.datas[0]
                }
                if(item.datas[1].isEmpty()){
                    mBinding.tvAfternoon.visibility = View.GONE
                }else{
                    mBinding.tvAfternoon.text = item.datas[1]
                }
                if(item.datas[2].isEmpty()){
                    mBinding.tvNight.visibility = View.GONE
                }else{
                    mBinding.tvNight.text = item.datas[2]
                }
                if(item.datas[3].isEmpty()){
                    mBinding.tvAllday.visibility = View.GONE
                }else{
                    mBinding.tvAllday.text = item.datas[3]
                }
            }
            is VolunteerTeamServiceTimeTableBottomBinding ->{
                mBinding.tvApply.onNoDoubleClick {
                        signApplyListener?.onClickSignApply()
                }
            }

        }
    }

    var signApplyListener :SignApplyListener? = null

    interface SignApplyListener{
        fun onClickSignApply()
    }
}