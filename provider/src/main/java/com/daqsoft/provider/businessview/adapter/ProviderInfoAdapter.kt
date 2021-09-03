package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.InformationBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ItemProivderInfoBinding
import com.daqsoft.provider.databinding.ItemProivderStoriesBinding
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/23 16:12
 */
class ProviderInfoAdapter : RecyclerViewAdapter<ItemProivderInfoBinding, InformationBean> {
    var context: Context? = null

    constructor(context: Context) : super(R.layout.item_proivder_info) {
        this.context = context
    }

    @SuppressLint("CheckResult", "SimpleDateFormat")
    override fun setVariable(mBinding: ItemProivderInfoBinding, position: Int, item: InformationBean) {
        val imageUrls = item.imageUrls
        var imageUrl = ""

        if (!imageUrls.isNullOrEmpty()) {
            imageUrl = StringUtil.getImageUrl(imageUrls[0].url,214,160)
        }
        GlideModuleUtil.loadDqImageWaterMark(imageUrl,context!!,mBinding.imgProviderInfo)
        mBinding.title = item.title
        if (item.publishTime.split(" ").size > 1) {
            mBinding.time = item.publishTime.split(" ")[0]
        }else{
            mBinding.time = item.publishTime
        }
        if (!item.getVideoInfo().url.isNullOrEmpty()) {
            mBinding.imgVideoPlay.visibility = View.VISIBLE
        } else {
            mBinding.imgVideoPlay.visibility = View.GONE
        }

        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withInt("type", 1)
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }

    }
}