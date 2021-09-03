package com.daqsoft.provider.view.databind

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.OperationTemplate
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.cardview.CardView
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean

/**
 * @Description databind 适配器
 * @ClassName   BindingAdapter
 * @Author      luoyi
 * @Time        2020/7/23 16:55
 */
object BindingAdapter {

    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: ArcImageView, url: String?) {
        GlideModuleUtil.loadDqImage(url, BaseApplication.context, view)
    }

    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        GlideModuleUtil.loadDqImage(url, BaseApplication.context, view)
    }


    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageRes")
    @JvmStatic
    fun loadImage(view: ImageView, url: Int) {
       view.setImageResource(url)
    }
    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageBg1")
    @JvmStatic
    fun loadImage1(view: ImageView, url: Int) {
        view.setBackgroundResource(url)
    }
    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageRadiusUrl")
    @JvmStatic
    fun loadImageRadius(view: ImageView, url: String?) {
        GlideModuleUtil.loadCornerRadiusImage(url, view, R.mipmap.placeholder_img_fail_240_180, 5)
    }

    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageRadiusUrls")
    @JvmStatic
    fun loadImageUrlRadius(view: ImageView, url: String?) {
        GlideModuleUtil.loadCornerRadiusImage(url?.getRealImages(), view, R.mipmap.placeholder_img_fail_240_180, view.context.resources.getDimension(R.dimen.dp_5).toInt())
    }

    /**
     * 加载圆角图片
     */
    @BindingAdapter("clickData")
    @JvmStatic
    fun onAdsCilck(view: ArcImageView, data: OperationTemplate?) {
        view?.onNoDoubleClick {
            data?.let {
                MenuJumpUtils.menuPageJumpUtils(it)
            }
        }
    }
    /**
     * 加载圆角图片
     */
    @BindingAdapter("clickData")
    @JvmStatic
    fun onContentCilck(view:CardView, data: HomeContentBean?) {
        view?.onNoDoubleClick {
            data?.let {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", it.id.toString())
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }
        }
    }

    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageUrls")
    @JvmStatic
    fun loadImages(view: ArcImageView, urls: String?) {
        GlideModuleUtil.loadDqImage(urls?.getRealImages(), BaseApplication.context, view)
    }

    /**
     * 加载圆角图片
     */
    @BindingAdapter("imageUrls")
    @JvmStatic
    fun loadImages(view: ImageView, urls: String?) {
        GlideModuleUtil.loadDqImage(urls?.getRealImages(), BaseApplication.context, view)
    }

    /**
     * 拼接内容和单位
     */
    @BindingAdapter("content", "unit")
    @JvmStatic
    fun splitText(view: TextView, content: String?, unit: String?) {
        if (!content.isNullOrEmpty() && !unit.isNullOrEmpty()) {
            view.text = "${content}${unit}"
        }
    }


}