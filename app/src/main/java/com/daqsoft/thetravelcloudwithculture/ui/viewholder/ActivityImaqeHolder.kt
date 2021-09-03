package com.daqsoft.thetravelcloudwithculture.ui.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * 活动
 */
class ActivityImaqeHolder : Holder<ActivityBean> {
    var mContext: Context? = null
    var iv_content_1: ImageView? = null
    var tv_activity_title: TextView? = null
    var tv_activity_info_desc: TextView? = null
    var tv_activity_price: TextView? = null
    var tv_activity_input: TextView? = null

    constructor(itemView: View?, context: Context) : super(itemView) {
        this.mContext = context
    }

    override fun initView(itemView: View?) {
        if (itemView != null) {
            iv_content_1 = itemView.findViewById(R.id.iv_content_1)
            tv_activity_title = itemView.findViewById(R.id.tv_activity_title)
            tv_activity_info_desc = itemView.findViewById(R.id.tv_activity_info_desc)
            tv_activity_price = itemView.findViewById(R.id.tv_activity_price)
            tv_activity_input = itemView.findViewById(R.id.tv_activity_input)
            iv_content_1?.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    override fun updateUI(data: ActivityBean?) {
        if (data != null) {
            if (iv_content_1 != null && mContext != null) {
                GlideModuleUtil.loadDqImage(data.images, BaseApplication.context, iv_content_1!!)
            }
            tv_activity_title?.text=data.name
            tv_activity_info_desc?.text=data.getWsDesc()
            tv_activity_price?.text=data.getPriceInfo()
            tv_activity_input?.text=data.getInpartStr()

        }
    }
}