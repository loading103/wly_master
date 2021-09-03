package com.daqsoft.provider.uiTemplate.titleBar.found.holder

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.view.children
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.databinding.ItemFoundTemplateFourBinding
import com.daqsoft.provider.getRealImageUrl
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.provider.utils.GaosiUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import java.lang.Exception

/**
 * @Description
 * @ClassName   FoundTemplateFourHolder
 * @Author      luoyi
 * @Time        2020/12/8 10:57
 */
class FoundTemplateFourHolder(binding: ItemFoundTemplateFourBinding) : BaseOperationViewHolder<ItemFoundTemplateFourBinding>(binding) {
    override fun bindDataToUI(template: CommonTemlate) {
        var titlbarView = TitleBarFactoryView(binding.root.context).apply {
            isShowMore = false
            setData(template)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {

                    }

                }
        }
        var views = binding.llFoundTemplateFour.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.llFoundTemplateFour.removeView(v)
            }
        }

        binding.llFoundTemplateFour.addView(
            titlbarView,
            0
        )

    }

    fun setFoundsToUI(founds: MutableList<FoundAroundBean>, currentLoction: LatLng) {
        founds.forEach {
            run {
                it.currentPostion = currentLoction
            }
        }
        binding?.datas = ArrayList(founds)
        // 高斯模糊背景
        if (!founds.isNullOrEmpty()) {
            var firstItem = founds[0]
            Glide.with(binding.imgFoundFourBg)
                .asBitmap()
                .load(firstItem.image.getRealImageUrl())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        try {
                            var b = GaosiUtils.rsBlur(BaseApplication.context, resource, 25)
                            binding.imgFoundFourBg.setImageBitmap(b)
                        } catch (e: Exception) {

                        }


                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }
}