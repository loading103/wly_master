package com.daqsoft.provider.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemUploadBinding
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 图片上传控件
 * @ClassName   ImageUploadView
 * @Author      PuHua
 * @Time        2020/1/22 11:48
 */
class ImageUploadView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    /**
     * 默认添加图片的按钮
     */
    var addImageSrc = resources.getDrawable(R.mipmap.story_add_pic)
    /**
     * 图片最大张数
     */
    var maxImages = 9
    /**
     * 所有图片
     */
    var images = mutableListOf("")


    /**
     * 图像适配器
     */
    var adapter = object : RecyclerViewAdapter<ItemUploadBinding, String>(
        R.layout.item_upload
    ) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemUploadBinding, position: Int, item: String) {

            setImage(item, mBinding.roundedItemImage, addImageSrc)


            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (item == "") {
                        // 添加图

                    } else {
                        // 预览图片

                    }
                }
        }

    }

    /**
     * 设置图片
     *
     */
    fun setImage(url: String, imageView: ImageView, placeholder: Drawable?) {
        val option = RequestOptions()
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .fallback(placeholder)

        Glide.with(imageView)
            .load(url)
            .apply(option)
            .into(imageView)
    }

    init {
        setAdapter(adapter)
        adapter.add(images)
    }

    public fun onActivityResult() {

    }


}