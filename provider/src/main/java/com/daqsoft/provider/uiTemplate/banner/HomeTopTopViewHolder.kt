package com.daqsoft.thetravelcloudwithculture.ui.viewholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardBean

/**
 *@package:com.daqsoft.thetravelcloudwithculture.ui.viewholder
 *@date:2020/5/12 19:18
 *@author: caihj
 *@des:TODO
 **/
class HomeTopTopViewHolder : Holder<AdInfo> {

    var mContext: Context? = null
    var cityCover: ImageView? = null
    var appIndexLog: ImageView? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        if (itemView != null) {
            cityCover = itemView.findViewById(R.id.city_image)
        }

    }

    override fun updateUI(data: AdInfo?) {
        if (data != null && mContext != null) {
            mContext?.let { it1 ->
                GlideModuleUtil.loadDqImageWaterMark(data.imgUrl, it1, cityCover!!)
            }

        }
    }

}