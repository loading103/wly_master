package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ImageLoadUtil
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ItemProivderStoriesBinding
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ProviderStoriesAdapter
 * @Author      luoyi
 * @Time        2020/4/2 14:06
 */
class ProviderStoriesAdapter: RecyclerViewAdapter<ItemProivderStoriesBinding, StoreBean> {
    var context: Context? = null

    constructor(context: Context) : super(R.layout.item_proivder_stories) {
        this.context = context
    }

    override fun setVariable(mBinding:ItemProivderStoriesBinding, position: Int, item: StoreBean) {

        mBinding.headUrl = item.vipHead
        mBinding.name = item.vipNickName
        mBinding.likeNum = item.likeNum
        if(item.likeNum=="0"){
            mBinding.ivDz.visibility=View.GONE
            mBinding.txtProviderStoryLikeNum.visibility=View.GONE
        }else{
            mBinding.ivDz.visibility=View.VISIBLE
            mBinding.txtProviderStoryLikeNum.visibility=View.VISIBLE
        }
        // 图片数量
        if (!item.images.isNullOrEmpty()) {
            mBinding.txtProviderStoreImgNum.text = "${item.images.size}图"
            mBinding.txtProviderStoreImgNum.visibility = View.VISIBLE
            mBinding.txtProviderStoryAddress.visibility = View.VISIBLE
            mBinding.imgProviderStoryOne.visibility = View.VISIBLE
            GlideModuleUtil.loadDqImageWaterMark(item.images[0],context!!,mBinding.imgProviderStoryOne)
        } else {
            mBinding.txtProviderStoreImgNum.visibility = View.GONE
            mBinding.txtProviderStoryAddress.visibility = View.GONE
            mBinding.imgProviderStoryOne.visibility = View.GONE
        }
        // 视频
        if (!item.video.isNullOrEmpty()) {
            // 当有视频时
            mBinding.imgProviderStoryVideo.visibility = View.VISIBLE
            mBinding.txtProviderStoreImgNum.visibility = View.GONE
        } else {
            mBinding.imgProviderStoryVideo.visibility = View.GONE

        }
        // 判断是否需要添加表填
        var ssb = SpannableStringBuilder()

        if (!item.tagName.isNullOrEmpty()) {
            ssb.append("#" + item.tagName + "#")
            ssb.setSpan(
                ForegroundColorSpan(context!!.resources.getColor(R.color.colorPrimary)),
                0,
                ssb.length,
                Spanned
                    .SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        ssb.append(item.title)
        mBinding.txtProviderStoreContent.text = ssb

        if (ssb.isNullOrEmpty()) {
            mBinding.txtProviderStoreContent.visibility = View.GONE
        } else {
            mBinding.txtProviderStoreContent.visibility = View.VISIBLE
        }


        // 判断是否关联地址和类型
        if( item.resourceName.isEmpty()){
            if(item.resourceRegionName.isNullOrEmpty()){
                mBinding.txtProviderStoryAddress.visibility = View.GONE
            }else{
                mBinding.txtProviderStoryAddress.visibility = View.VISIBLE
                mBinding.txtProviderStoryAddress.text=item.resourceRegionName
            }
        }else{
            mBinding.txtProviderStoryAddress.visibility = View.VISIBLE
            mBinding.txtProviderStoryAddress.text=item.resourceName
        }
        // 类型
//        if (item.resourceRegionName.isNullOrEmpty()) {
//
//        } else {
//            val address = DividerTextUtils.convertDotString(
//                StringBuilder(), item.resourceRegionName,
//                item.resourceName
//            )
//
//            if(address.length>6){
//                mBinding.txtProviderStoryAddress.text= address.substring(0,7)+".."
//            }else{
//                mBinding.txtProviderStoryAddress.text=address
//            }
//            mBinding.txtProviderStoryAddress.visibility = View.VISIBLE
//        }
//
//        val typeStr = ResourceType.getName(item.resourceType)
//        mBinding.tvType.text = typeStr
//        if(typeStr.isEmpty()){
//            mBinding.tvType.visibility=View.GONE
//        }else{
//            mBinding.tvType.visibility=View.GONE
//        }

        if (!item.resourceRegionName.isNullOrEmpty()){
            mBinding.tvType.visibility = View.VISIBLE
            val typeStr = ResourceType.getName(item.resourceType)
            if(typeStr.isEmpty()){
                mBinding.tvType.text=item.resourceRegionName
            }else{
                val address = DividerTextUtils.convertDotString(StringBuilder(), item.resourceRegionName, typeStr)
                mBinding.tvType.text=address
            }
        }else{
            val typeStr = ResourceType.getName(item.resourceType)
            if(typeStr.isEmpty()){
                mBinding.tvType.visibility = View.GONE
            }else{
                mBinding.tvType.visibility = View.VISIBLE
                mBinding.tvType.text=typeStr
            }
        }

        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STORY_DETAIL)
                        .withString("id", item.id)
                        .withInt("type", 1)
                        .navigation()
                }
            }

    }
}