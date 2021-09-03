package com.daqsoft.provider.uiTemplate.titleBar.found.holder

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.thetravelcloudwithculture.sc.viewholder
 *@date:2020/5/20 14:42
 *@author: caihj
 *@des:TODO
 **/
class FoundAroundContentHolder : Holder<FoundAroundBean> {

    private var imageView: ImageView? = null
    private var title:TextView? =null
    private var content:TextView? = null
    private var currentPostion:LatLng? = null

    constructor(itemView: View,currentPostion:LatLng) : super(itemView) {
        this.currentPostion = currentPostion
    }

    override fun initView(itemView: View?) {
        imageView = itemView?.findViewById(R.id.av_img)
        title = itemView?.findViewById(R.id.tv_title)
        content = itemView?.findViewById(R.id.tv_content)
    }

    override fun updateUI(data: FoundAroundBean) {
        if(imageView!=null&&imageView!!.context!=null) {
            Glide.with(imageView!!.context).load(data.image)
                .placeholder(R.drawable.placeholder_img_fail_240_180).into(imageView!!)
            title?.text = data.name
            val typeStr = ResourceType.getName(data.resourceType)
            val distance = AddressUtil.getLocationDistanceCh(
                currentPostion!!,
                LatLng(data.latitude, data.longitude)
            )
            if(adapterPosition==5){
                val spb = SpannableStringBuilder()
                val sb = "离你最近·$typeStr·距你$distance"
                spb.append(sb)
                var colorSpan = ForegroundColorSpan(imageView!!.context.resources.getColor(R.color.color_ff9e05))
                spb.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                content?.text =spb
            }else{
                val sb = StringBuilder()
                content?.text = "$typeStr·距你$distance"
            }

        }
    }
}