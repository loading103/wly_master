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
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardBean

/**
 *@package:com.daqsoft.thetravelcloudwithculture.ui.viewholder
 *@date:2020/5/12 19:18
 *@author: caihj
 *@des:TODO
 **/
class HomeTopViewHolder : Holder<CityCardBean> {

    var mContext: Context? = null
    var weather: TextView? = null
    var cityCover: ImageView? = null
    var cityName: TextView? = null
    var cityNameEn: TextView? = null
    var citySummary: TextView? = null
    var cityCard: TextView? = null
    var cityPosition: TextView? = null
    var tryRun: TextView? = null
    var appIndexLog: ImageView? = null
    var screenWidth: Int = 0

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
        screenWidth = Utils.getWidthInPx(context)
    }

    override fun initView(itemView: View?) {
        if (itemView != null) {
            cityCover = itemView.findViewById(R.id.city_image)
            weather = itemView.findViewById(R.id.tv_weather)
            cityName = itemView.findViewById(R.id.tv_card_name)
            cityNameEn = itemView.findViewById(R.id.tv_card_name_english)
            citySummary = itemView.findViewById(R.id.tv_card_summary)
            cityCard = itemView.findViewById(R.id.tv_card)
            cityPosition = itemView.findViewById(R.id.tv_position)
            tryRun = itemView.findViewById(R.id.tv_test)
            appIndexLog = itemView.findViewById(R.id.img_app_index_logo)
        }

    }

    override fun updateUI(data: CityCardBean?) {
        if (data != null && mContext != null) {
            Glide.with(mContext!!)
                .asBitmap()
                .load(data.weather.pic)
                .centerCrop()
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        var drawable = BitmapDrawable(resource)
                        weather?.setCompoundDrawablesWithIntrinsicBounds(
                            drawable,
                            null,
                            null,
                            null
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            weather?.text = mContext?.getString(
                R.string.home_weather_str, data.weather.min + "~" + data
                    .weather.max
            )
            mContext?.let { it1 ->
                GlideModuleUtil.loadDqImageWaterMark(data.coverImage, it1, cityCover!!)
            }
            mContext?.let { it1 ->
                if (screenWidth > 0) Glide.with(it1)
                    .load(StringUtil.getImageUrl(data.coverImage, screenWidth, screenWidth))
                    .skipMemoryCache(true).into(cityCover!!)
            }
            cityNameEn?.text = data.english
            citySummary?.text = data.summary
            if (data.tryRunFlag) {
                tryRun?.visibility = View.VISIBLE
            }
            cityName?.text = data.name
            if (data.appIndexLog == null || data.appIndexLog!!.adInfo.isNullOrEmpty()) {
                hideAppLogo()
            } else {
                if (!data.appIndexLog!!.adInfo.isNullOrEmpty()) {
                    var adInfo = data.appIndexLog!!.adInfo[0]
                    if (adInfo != null) {
                        showAppLogo()
                        appIndexLog?.let {
                            Glide.with(mContext!!)
                                .load(adInfo.imgUrl)
                                .into(it)
                        }

                    } else {
                        hideAppLogo()
                    }
                }

            }
            /* if (data.positionFlag) {
                 cityPosition?.visibility = View.VISIBLE
                 cityPosition?.text = data.regionName
             }*/
            cityCard?.setOnClickListener {
                var siteRegion: String? = data.region
                if (siteRegion.isNullOrEmpty()) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CURRENT_CITY_INFO)
                        .withString("id", data.siteId.toString())
                        .navigation()
                } else {
                    if (siteRegion.endsWith("0000")) {
                        // 省级站点
                        ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CURRENT_CITY_INFO)
                            .withString("id", data.siteId.toString())
                            .navigation()
                    } else {
                        //市级站点
                        ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                            .withString("id", data.siteId.toString())
                            .navigation()
                    }
                }

            }
        }
    }

    private fun showAppLogo() {
        citySummary?.visibility = View.GONE
        cityNameEn?.visibility = View.GONE
        cityName?.visibility = View.GONE
        appIndexLog?.visibility = View.VISIBLE
    }

    private fun hideAppLogo() {
        citySummary?.visibility = View.VISIBLE
        cityNameEn?.visibility = View.VISIBLE
        cityName?.visibility = View.VISIBLE
        appIndexLog?.visibility = View.GONE
    }
}