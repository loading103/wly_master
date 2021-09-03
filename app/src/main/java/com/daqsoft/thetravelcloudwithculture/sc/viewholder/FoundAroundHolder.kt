package com.daqsoft.thetravelcloudwithculture.sc.viewholder

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import org.jetbrains.anko.support.v4.toast
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.thetravelcloudwithculture.sc.viewholder
 *@date:2020/5/20 14:42
 *@author: caihj
 *@des:TODO
 **/
class FoundAroundHolder : Holder<FoundAroundBean> {

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
        GlideModuleUtil.loadDqImageWaterMark(data.image,imageView!!)
//        Glide.with(imageView!!.context).load(data.image).placeholder(R.drawable.placeholder_img_fail_240_180).into(imageView!!)
        title?.text = data.name
        val typeStr = ResourceType.getName(data.resourceType)
        val distance = AddressUtil.getLocationDistanceCh(currentPostion!!,LatLng(data.latitude,data.longitude))
        val spb = SpannableStringBuilder()
        val sb = StringBuilder()
        val text = DividerTextUtils.convertDotString(sb,"离你最近",typeStr,distance)
        spb.append(text)
        var colorSpan = ForegroundColorSpan(imageView!!.context.resources.getColor(com.daqsoft.legacyModule.R.color.color_ff9e05))
        spb.setSpan(colorSpan,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        content?.text = spb
    }
}