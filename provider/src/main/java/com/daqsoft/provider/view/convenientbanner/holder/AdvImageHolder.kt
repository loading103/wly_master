package com.daqsoft.provider.view.convenientbanner.holder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.bean.HomeAd

/**
 * @Description
 * @ClassName   AdvImageHolder
 * @Author      luoyi
 * @Time        2020/5/29 15:37
 */
class AdvImageHolder : Holder<AdInfo> {
    private var imageView: ImageView? = null

    constructor(itemView: View?) : super(itemView) {

    }

    override fun initView(itemView: View?) {
        imageView = itemView?.findViewById(R.id.img_common_adv)
        imageView?.let {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun updateUI(data: AdInfo?) {
        if (data != null) {
            if (imageView != null && imageView!!.context != null) {
                Glide.with(imageView!!.context).load(data.imgUrl).placeholder(R.drawable.placeholder_img_fail_240_180).into(imageView!!)
            }
        }

    }
}