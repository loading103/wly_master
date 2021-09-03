package com.daqsoft.provider.view.convenientbanner.holder

import android.view.View
import android.widget.ImageView
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.provider.R

/**
 * @Description
 * @ClassName   BaseImageHolder
 * @Author      luoyi
 * @Time        2020/11/13 10:01
 */
class BaseImageHolder : Holder<String> {
    private var imageView: ImageView? = null

    constructor(itemView: View?) : super(itemView) {

    }

    override fun initView(itemView: View?) {
        imageView = itemView?.findViewById(R.id.img_provider_holder)
        imageView?.let {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun updateUI(data: String?) {
        if (data != null) {
            if (imageView != null && imageView!!.context != null) {
                GlideModuleUtil.loadDqImage(data, imageView!!)
            }
        }
    }
}