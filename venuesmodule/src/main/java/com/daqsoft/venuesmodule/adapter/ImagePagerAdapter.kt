package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.repository.bean.ActivityInfo

/**
 * 图片的轮播图适配器
 * @author 黄熙
 * @date 2020/1/14 0014
 * @version 1.0.0
 * @since JDK 1.8
 */
class ImagePagerAdapter(imgDatas: MutableList<ActivityInfo>, context: Context) : PagerAdapter() {
    /**
     * 图片适配器
     */
    var imgDatas = imgDatas

    var context = context

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun getCount(): Int {
        return this.imgDatas.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var index = position % this.imgDatas.size
        // 根据条目所在，从imgDatas集合中获取相对应的图片
        var imageView: ImageView = ImageView(context)
        var imageUrl: String = ""
        if (!imgDatas.get(index).images.isNullOrEmpty()) {
            var imageUrls = imgDatas.get(index).images.split(",")
            if (!imageUrls.isNullOrEmpty()) {
                imageUrl = imageUrls[0]
            }
        }
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .centerCrop()
            .into(imageView)
        // 把得到的imageView的对象，添加到viewPager，也就是contrainer中
        container.addView(imageView)
        return imageView
    }

    /**
     * 要销毁，防止内存泄漏
     */
    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View)
    }
}
