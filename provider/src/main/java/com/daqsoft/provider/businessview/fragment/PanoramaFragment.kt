package com.daqsoft.provider.businessview.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.OssUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemPanoramaImageBinding
import com.daqsoft.provider.utils.GaosiUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   PanoramaFragment
 * @Author      luoyi
 * @Time        2020/3/31 19:14
 */
class PanoramaFragment : BaseFragment<ItemPanoramaImageBinding, BaseViewModel>() {

    var pannoramaUrl: String? = null
    var coverUrl: String? = null
    var name: String? = null

    //    constructor(pannoramaUrl: String, coverUrl: String, name: String) {
//        this.coverUrl = coverUrl
//        this.pannoramaUrl = pannoramaUrl
//        this.name = name
//    }
    companion object {
        const val PANNORAMA_URL = "pannorama_url"
        const val COVER_URL = "cover_url"
        const val NAME = "name"
        fun newInstance(pannoramaUrl: String, coverUrl: String?, name: String): PanoramaFragment {
            var frag = PanoramaFragment()
            var bundle = Bundle()
            bundle.putString(PANNORAMA_URL, pannoramaUrl)
            bundle.putString(COVER_URL, coverUrl)
            bundle.putString(NAME, name)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_panorama_image
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        getParams()
        if (coverUrl.isNullOrEmpty()) {
            Glide.with(context!!)
                .load(R.mipmap.img_panaro_16_9)
                .centerCrop()
                .into(mBinding.imgItemPanorama)
        } else {
            Glide.with(context!!)
                .load(OssUtil.getImageUrlWatermark(coverUrl))
                .placeholder(R.mipmap.img_panaro_16_9)
                .centerCrop()
                .into(mBinding.imgItemPanorama)
        }
        RxView.clicks(mBinding.cvItemPanorama)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", name)
                    .withString("html", pannoramaUrl)
                    .navigation()
            }
    }

    private fun getParams() {
        try {
            coverUrl = arguments?.getString(COVER_URL)
            pannoramaUrl = arguments?.getString(PANNORAMA_URL)
            name = arguments?.getString(NAME)
        } catch (e: Exception) {
        }

    }

    override fun initData() {
    }
}