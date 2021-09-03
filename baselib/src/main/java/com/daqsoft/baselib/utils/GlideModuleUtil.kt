package com.daqsoft.baselib.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import daqsoft.com.baselib.R

/**
 * @Description glide加载图片库封装工具类
 * @ClassName   GlideModule
 * @Author      luoyi
 * @Time        2020/10/28 14:54
 */
object GlideModuleUtil {


    /**图片加载
     *  加载图片 不传context的情况
     *  @param url 图片地址
     *  @param imageView imageView
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImage(
        url: String?, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(url, imageView.context, imageView, errorDrawable, placeHolderDrawable)
    }

    /**图片加载
     *  加载图片 不传context的情况
     *  @param url 图片地址
     *  @param imageView imageView
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImage(
        url: String?, context: Context, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(url, context, imageView, errorDrawable, placeHolderDrawable)
    }

    /**图片加载 增加宽高
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 图片高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImage(
        url: String?, width: Int, height: Int, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {

        loadDqBaseImage(
            OssUtil.getImageUrl(url, width, height),
            imageView.context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }

    /**图片加载 增加宽高
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 图片高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImage(
        url: String?, width: Int, height: Int, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        options: RequestOptions
    ) {

        loadDqBaseImage(
            OssUtil.getImageUrl(url, width, height),
            imageView.context,
            imageView,
            errorDrawable,
            placeHolderDrawable, options
        )
    }

    /**图片加载 增加宽高
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 图片高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImage(
        url: String?, width: Int, height: Int, context: Context, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrl(url, width, height),
            context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }

    /** 图片加载 添加水印
     * @param url 图片地址
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImageWaterMark(
        url: String?, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url),
            imageView.context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }

    /** 图片加载 添加水印
     * @param url 图片地址
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImageWaterMark(
        url: String?, context: Context, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url),
            context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }
    fun loadDqImageWaterMarkNoPlace(
        url: String?, context: Context, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage1(
            OssUtil.getImageUrlWatermark(url),
            context,
            imageView,
            errorDrawable
        )
    }
    /** 图片加载 添加水印
     * @param url 图片地址
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     *  @param options glide加载参数
     */
    fun loadDqImageWaterMark(
        url: String?, context: Context, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        options: RequestOptions
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url),
            context,
            imageView,
            errorDrawable,
            placeHolderDrawable, options
        )
    }

    /**  图片加载 添加水印
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImageWaterMark(
        url: String?, width: Int, height: Int, imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url, width, height),
            imageView.context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }

    /**  图片加载 添加水印
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImageWaterMark(
        url: String?,
        width: Int,
        height: Int,
        context: Context,
        imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url, width, height),
            context,
            imageView,
            errorDrawable,
            placeHolderDrawable
        )
    }

    /**  图片加载 添加水印
     * @param url 图片地址
     * @param width 图片宽度
     * @param height 高度
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    fun loadDqImageWaterMark(
        url: String?,
        width: Int,
        height: Int,
        context: Context,
        imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        options: RequestOptions
    ) {
        loadDqBaseImage(
            OssUtil.getImageUrlWatermark(url, width, height),
            context,
            imageView,
            errorDrawable,
            placeHolderDrawable, options
        )
    }

    /**
     * 基础加载图片方法
     * @param url 图片地址
     * @param context 上下文对象
     * @param imageView 显示imageview
     *  @param errorDrawable 错误占位图
     *  @param placeHolderDrawable 默认占位图
     */
    private fun loadDqBaseImage(
        url: String?,
        context: Context?,
        imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        options: RequestOptions? = null
    ) {
        var option: RequestOptions = options ?: RequestOptions().skipMemoryCache(true)
        if (errorDrawable > 0) {
            option.error(errorDrawable)
        }
        if (placeHolderDrawable > 0) {
            option.placeholder(placeHolderDrawable)
        }
        // 避免imageview 引用上下文对象 activity退出导致的context异常情况
        context?.let {
            Glide.with(it)
                .load(url)
                .apply(option)
                .into(imageView)
        }
    }
    private fun loadDqBaseImage1(
        url: String?,
        context: Context?,
        imageView: ImageView,
        errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        options: RequestOptions? = null
    ) {
        var option: RequestOptions = options ?: RequestOptions().skipMemoryCache(true)
        if (errorDrawable > 0) {
            option.error(errorDrawable)
        }
        // 避免imageview 引用上下文对象 activity退出导致的context异常情况
        context?.let {
            Glide.with(it)
                .load(url)
                .apply(option)
                .into(imageView)
        }
    }
    /**
     * 加载本地图片
     */
    fun loadLocalDqImage(
        url: Int?, imageView: ImageView, errorDrawable: Int = R.mipmap.placeholder_img_fail_240_180,
        placeHolderDrawable: Int = R.mipmap.placeholder_img_fail_240_180
    ) {
        var option: RequestOptions = RequestOptions().skipMemoryCache(true)
        if (errorDrawable > 0) {
            option.error(errorDrawable)
        }
        if (placeHolderDrawable > 0) {
            option.placeholder(placeHolderDrawable)
        }
        Glide.with(imageView)
            .load(url)
            .apply(option)
            .into(imageView)
    }

    /**
     * 加载圆角图片
     * @param url 图片地址
     * @param imageView 显示控件
     * @param placeholder 占位图
     * @param cornerRadius 圆角大小
     */
    fun loadCornerRadiusImage(
        url: String?,
        imageView: ImageView,
        placeholder: Int,
        cornerRadius: Int
    ) {
        val option = RequestOptions
            .bitmapTransform(
                RoundedCorners(
                    cornerRadius
                )
            )
            .placeholder(placeholder)
            .error(placeholder)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .fallback(placeholder)


        Glide.with(imageView)
            .load(url)
            .apply(option)
            .into(imageView)
    }
}