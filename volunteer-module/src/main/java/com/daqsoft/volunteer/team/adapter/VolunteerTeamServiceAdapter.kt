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
import com.daqsoft.volunteer.bean.ServiceRecordBean
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
import com.daqsoft.volunteer.databinding.VolunteerListItemBinding
import com.daqsoft.volunteer.databinding.VolunteerServiceRecordListItemBinding
import com.daqsoft.volunteer.databinding.VolunteerTeamListItemBinding
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.volunteer.net
 *@date:2020/6/3 11:16
 *@author: caihj
 *@des:志愿服务（志愿风采）
 **/
class VolunteerTeamServiceAdapter(context: Context):RecyclerViewAdapter<VolunteerServiceRecordListItemBinding,ServiceRecordBean>(R.layout.volunteer_service_record__list_item) {
    val mContext = context

    @SuppressLint("ResourceAsColor")
    override fun setVariable(
        mBinding: VolunteerServiceRecordListItemBinding,
        position: Int,
        item: ServiceRecordBean
    ) {

        var imageUrl = ""
        var size = 0
        if(!item.images.isNullOrEmpty()){
            imageUrl = item.images.split(",")[0]
            size = item.images.split(",").size
        }

        if(imageUrl.isEmpty()&&!item.coverVideos.isNullOrEmpty()){
            imageUrl = item.coverVideos
            size +=1
        }

        if(size != 0){
            mBinding.tvImageNumber.visibility = View.VISIBLE
            mBinding.tvImageNumber.text = "${size}图"
        }else{
            mBinding.tvImageNumber.visibility = View.GONE
        }
        if(item.coverVideos.isNullOrEmpty()){
            mBinding.ivVideo.visibility = View.GONE
        }else{
            mBinding.ivVideo.visibility = View.VISIBLE
        }
        if(item.activity!=null){
            mBinding.tvLocationName.visibility = View.VISIBLE
            mBinding.tvLocationName.text = item.activity.regionNameStr
        }else{
            mBinding.tvLocationName.visibility = View.GONE
        }
        mBinding.tvContent.text = item.content
        mBinding.tvUser.text = item.volunteerName
        mBinding.tvLike.text = item.resourceNum.likeNum.toString()
        Glide.with(mContext).load(item.volunteerHead).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
        Glide.with(mContext).load(imageUrl).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.image)


        mBinding.root.onNoDoubleClick {
            JumpUtils.gotoVolunteerTeamDetail(item.id)
        }
    }
}