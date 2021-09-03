package com.daqsoft.legacyModule.smriti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ItemLegacyBehalfBinding
import com.daqsoft.legacyModule.databinding.ItemLegacyRecommendBinding
import com.daqsoft.legacyModule.smriti.bean.LegacyBehalfBean
import com.daqsoft.legacyModule.smriti.bean.LegacyRecommendBean
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * desc :非遗推荐adapter
 * @author 江云仙
 * @date 2020/4/22 15:53
 */
class LegacyRecommendAdapter() : RecyclerViewAdapter<ItemLegacyRecommendBinding, LegacyRecommendBean>(R.layout.item_legacy_recommend) {


    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemLegacyRecommendBinding, position: Int, item: LegacyRecommendBean) {
        when (position) {
            0 -> {
                mBinding.imgHead.setCornerTopLeftRadius(Utils.dip2px(BaseApplication.context, 5f))
            }
            1 -> {
                mBinding.imgHead.setCornerTopRightRadius(Utils.dip2px(BaseApplication.context, 5f))
            }
            2 -> {
                mBinding.imgHead.setCornerBottomLeftRadius(Utils.dip2px(BaseApplication.context, 5f))
            }
            4 -> {
                mBinding.imgHead.setCornerBottomRightRadius(Utils.dip2px(BaseApplication.context, 5f))
            }
        }
        if (!item.name.isNullOrEmpty()) {
            mBinding.tvTitle.text = item.name
        }
        val imageUrls = item.images
        if (!imageUrls.isNullOrEmpty()) {
            val images = item.images.split(",")
            mBinding.urls = images[0]
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                        .withString("id", item.id.toString())
                        .navigation()


                }

        }


    }
}