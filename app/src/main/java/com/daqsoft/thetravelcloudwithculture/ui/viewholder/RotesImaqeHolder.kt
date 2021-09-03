package com.daqsoft.thetravelcloudwithculture.ui.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 精品线路
 */
class RotesImaqeHolder : Holder<HomeContentBean> {
    var mContext: Context? = null
    var imgAds: ImageView? = null

    constructor(itemView: View?, context: Context) : super(itemView) {
        this.mContext = context
    }

    override fun initView(itemView: View?) {
        if (itemView != null) {
            imgAds = itemView.findViewById(R.id.img_splash_ads)
            imgAds?.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun updateUI(data: HomeContentBean?) {
        if (data != null) {
            if (imgAds != null && mContext != null) {
                GlideModuleUtil.loadDqImage(data.getContentCoverImageUrl(), BaseApplication.context, imgAds!!)
            }
        }
    }
}