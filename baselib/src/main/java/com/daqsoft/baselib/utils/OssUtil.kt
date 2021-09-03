package com.daqsoft.baselib.utils

import android.util.Log

/**
 * @Description 阿里云oss工具类
 * @ClassName   OssUtil
 * @Author      luoyi
 * @Time        2020/10/28 15:02
 */
object OssUtil {
    /**
     * 通过oss接口，处理图片
     */
    fun getImageUrl(imageUrl: String?, width: Int, height: Int): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var url =
            "${StringUtil.enCodeImageUrl(imageUrl)}?x-oss-process=image/resize,m_fixed,w_${width},h_${height}/format,webp"
        return url
    }

    /**
     * 通过oss接口，处理图片 添加水印
     */
    fun getImageUrlWatermark(imageUrl: String?, width: Int, height: Int): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var waterMark: String? = SPUtils.getInstance().getString("site_watermark")
        if (waterMark.isNullOrEmpty()) {
            return imageUrl
        } else {
            var url =
                "${StringUtil.enCodeImageUrl(imageUrl)}?x-oss-process=image/resize,m_fixed,w_${width},h_${height}/format,webp/watermark,image_${waterMark},t_50,g_se,x_10,y_10"
            Log.e("url=", url)
            return url
        }
    }

    /**
     * 通过oss接口，处理图片 添加水印
     */
    fun getImageUrlWatermark(imageUrl: String?): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var waterMark: String? = SPUtils.getInstance().getString("site_watermark")
        if (waterMark.isNullOrEmpty()) {
            return imageUrl
        } else {
            var url =
                "${StringUtil.enCodeImageUrl(imageUrl)}?x-oss-process=image/watermark,image_${waterMark},t_50,g_se,x_10,y_10"
            Log.e("url=", url)
            return url
        }
    }

}