package com.daqsoft.travelCultureModule.country.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.Utils.dip2px
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemFwRecommendBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.country.bean.TravelGuideBean
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * desc :风物推荐
 * @author 江云仙
 * @date 2020/4/18 17:30
 */
class CountryFWRecommendAdapter : RecyclerViewAdapter<ItemFwRecommendBinding, TravelGuideBean> {

    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_fw_recommend) {
        this.mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemFwRecommendBinding, position: Int, item: TravelGuideBean) {
        mBinding.title = item.title
        if (position == 0) {
            mBinding.tvRecommend.visibility = View.VISIBLE
        } else {
            mBinding.tvRecommend.visibility = View.GONE
        }
        if (position == 0 || position == 1) {
            val lp: ViewGroup.LayoutParams = mBinding.imgHead.layoutParams
            lp.height = dip2px(BaseApplication.context, 120f).toInt()
            mBinding.imgHead.layoutParams = lp
            mBinding.tvTitle.textSize = 15f
            val params: RelativeLayout.LayoutParams = mBinding.tvTitle.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            mBinding.tvTitle.layoutParams = params
            mBinding.tvTitle.setPadding(0, 0, 0, dip2px(BaseApplication.context, 13f).toInt())
        } else {
            mBinding.tvTitle.textSize = 13f
            val params: RelativeLayout.LayoutParams = mBinding.tvTitle.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            mBinding.tvTitle.layoutParams = params
        }
        when (position) {
            0 -> {
                mBinding.imgHead.setCornerTopLeftRadius(dip2px(BaseApplication.context, 5f))
            }
            1 -> {
                mBinding.imgHead.setCornerTopRightRadius(dip2px(BaseApplication.context, 5f))
            }
            2 -> {
                mBinding.imgHead.setCornerBottomLeftRadius(dip2px(BaseApplication.context, 5f))
            }
            4 -> {
                mBinding.imgHead.setCornerBottomRightRadius(dip2px(BaseApplication.context, 5f))
            }
        }
//        val imageUrls = item.imageUrls
//        if (imageUrls.isNotEmpty()) {
        mBinding.urls = item.getContentCoverImageUrl()
//        }
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", item.id.toString())
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }


    }
}