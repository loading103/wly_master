package com.daqsoft.provider.view.convenientbanner.holder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.provider.utils.StringUtils
import daqsoft.com.baselib.R

/**
 * @Description
 * @ClassName   BaseBannerImageHolder
 * @Author      luoyi
 * @Time        2020/3/21 14:36
 */
class BaseBannerImageHolder : Holder<String> {

    private var imageView: ImageView? = null

    constructor(itemView: View) : super(itemView) {
    }

    override fun initView(itemView: View?) {
        imageView = itemView?.findViewById(R.id.img_base)
        imageView?.let {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun updateUI(data: String?) {
        if (!data.isNullOrEmpty()) {
            if (imageView != null && imageView!!.context != null) {
                Glide.with(imageView!!.context).load(data).placeholder(R.drawable.placeholder_img_fail_240_180).into(imageView!!)
            }
        }

    }
}