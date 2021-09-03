package com.daqsoft.thetravelcloudwithculture.ui.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R

/**
 * @Description
 * @ClassName   SplashAdsHolder
 * @Author      luoyi
 * @Time        2020/6/28 9:21
 */
class SplashAdsHolder : Holder<AdInfo> {
    var mContext: Context? = null
    var imgAds: ImageView? = null
    var screenWidth: Int = 0
    var screenHeight: Int = 0

    constructor(itemView: View?, context: Context) : super(itemView) {
        this.mContext = context
        screenHeight = Utils.getHeightInPx(context)
        screenWidth = Utils.getWidthInPx(context)
    }

    override fun initView(itemView: View?) {
        if (itemView != null) {
            imgAds = itemView.findViewById(R.id.img_splash_ads)
            imgAds?.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    override fun updateUI(data: AdInfo?) {
        if (data != null) {
            if (imgAds != null && mContext != null) {
                if (screenWidth > 0 && screenHeight > 0) {
                    GlideModuleUtil.loadDqImage(
                        data.imgUrl,
                        screenWidth,
                        screenHeight,
                        mContext!!,
                        imgAds!!,
                        -1,
                        -1
                    )
                } else {
                    GlideModuleUtil.loadDqImage(data.imgUrl, mContext!!, imgAds!!,-1,-1)
                }
            }
        }
    }
}