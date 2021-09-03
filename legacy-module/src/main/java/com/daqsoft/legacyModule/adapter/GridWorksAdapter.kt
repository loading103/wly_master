package com.daqsoft.legacyModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.ItemLegacyWorkBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.legacyModule.adapter
 *@date:2020/5/21 16:52
 *@author: caihj
 *@des:TODO
 **/
class GridWorksAdapter(context: Context) : RecyclerViewAdapter<ItemLegacyWorkBinding, LegacyStoryListBean>(R.layout.item_legacy_work) {

    val mContext = context
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemLegacyWorkBinding, position: Int, item: LegacyStoryListBean) {
        Glide.with(mContext).load(item.vipHead).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
        mBinding.name = item.vipNickName
        if (item.images.isNotEmpty()) {
            mBinding.url = item.images[0]
        } else {
            if(item.cover.isNotEmpty()){
                mBinding.url = item.cover
            }else{
                mBinding.image.visibility = View.GONE
            }
        }
        if (item.resourceRegionName.isNullOrEmpty()) {
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.text = item.resourceRegionName
        }
        if (item.video.isNotEmpty()) {
            mBinding.ivVideo.visibility = View.VISIBLE
        } else {
            mBinding.ivVideo.visibility = View.GONE
        }
        if (item.resourceRegionName.isEmpty()) {
            // 判断是否关联地址和类型
            mBinding.tvCityType.visibility = View.INVISIBLE
        } else {
            mBinding.tvCityType.visibility = View.VISIBLE
        }
            if(item.resourceRegionName.isNotEmpty()){
                mBinding.tvCityType.visibility = View.VISIBLE
                mBinding.tvCityType.text = item.resourceRegionName + "·" + ResourceType.getName(item.resourceType)
                mBinding.tvCityType.setTextColor(mContext.resources.getColor(R.color.txt_gray))
            }

            // 判断是否需要添加表填
            var ssb = SpannableStringBuilder()
            if (item.tagName.isNotEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)), 0, ssb
                        .length, Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.title+","+item.content)
            mBinding.tvContent.text = ssb

            if (ssb.isEmpty()) {
                mBinding.tvContent.visibility = View.INVISIBLE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }

            if (item.images.isNotEmpty()&&item.images.size >1) {
                mBinding.tvImageNumber.text = mContext.getString(
                    R.string
                        .home_work_image_number, item.images.size.toString()
                )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        ARouter.getInstance()
                            .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                            .withInt("type",0)
                            .withString("id", item.id.toString())
                        .navigation()
                }
                }
        mBinding.likeNumber = item.likeNum.toString()
    }

}