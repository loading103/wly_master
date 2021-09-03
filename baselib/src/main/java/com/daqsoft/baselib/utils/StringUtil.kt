package com.daqsoft.baselib.utils

import android.text.Html
import android.text.Spanned
import android.util.Log
import com.daqsoft.baselib.base.BaseApplication
import java.math.BigDecimal
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.LinkedHashMap

/**
 * 字符串工具
 *
 * @author luoyi
 * @date 2020/3/31  10:50
 */
object StringUtil {

    /**
     * 字符串数组格式为字符串
     *
     * @param strs
     * @return
     */
    fun arrayToString(strs: List<String>): String? {
        var temp = ""
        for (i in strs.indices) {
            temp = if (i == 0) {
                temp + "" + strs[i]
            } else {
                temp + "," + strs[i]
            }
        }
        return temp
    }

    /**
     * 处理html文本内容
     */
    fun getHtml(content: String): String {
        var head = ""
        head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<link href=\"file:///android_asset/dq_content_1.0.0.css\" rel=\"stylesheet\">" +
                "<script type=\"text/javascript\" src=\"file:///android_asset/dq_content_1.0.0.js?\"></script>" +
                "</head>"
        val aa = "<html>$head<body>"
        val bb = "</body></html>"
        return aa + content + bb
    }

    fun ToDBC(input: String): String? {
        val c = input.toCharArray()
        try {
            for (i in c.indices) {
                if (c[i] == 12288 as Char) {
                    c[i] = 32.toChar()
                    continue
                }
                if (c[i] > 65280 as Char && c[i] < 65375 as Char) c[i] = (c[i] - 65248)
            }
        } catch (e: java.lang.Exception) {
            return input
        }

        return String(c)
    }

    /**
     * 中文转义视频地址
     */
    fun enCodeVideoUrl(videoUrl: String): String {
        var result = videoUrl
        try {
            val pos = videoUrl.lastIndexOf("/")
            var temp = result.substring(0, pos)
            var chin = videoUrl.substring(pos, videoUrl.length)
            val str = URLEncoder.encode(chin, "utf-8")
            result = temp + str
        } catch (e: Exception) {
        }
        return result

    }

    /**
     * 中文转义图片地址
     */
    fun enCodeImageUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) {
            return imageUrl
        }
        var result = imageUrl!!
        try {
            val pos = imageUrl?.lastIndexOf("/")
            var temp = result?.substring(0, pos)
            var chin = imageUrl?.substring(pos, imageUrl.length)
            val str = URLEncoder.encode(chin, "utf-8")
            result = temp + str
        } catch (e: Exception) {
        }
        return result

    }

    /**
     *  处理官网地址
     */
    fun formatHtmlUrl(websiteUrl: String?): String? {
        if (websiteUrl.isNullOrEmpty()) {
            return websiteUrl
        }
        try {
            if (!websiteUrl!!.contains("https://") && !websiteUrl!!.contains("http://")) {
                return "http://$websiteUrl"
            }
            return websiteUrl
        } catch (e: Exception) {

        }
        return websiteUrl
    }

    /**
     * 取视频 的封面
     */
    fun getVideoCoverUrl(videoUrl: String): String {
        val url =
            BaseApplication.baseUrl + "config/ued/interceptionVideoImage?siteCode=${BaseApplication.siteCode}&videoUrl=${videoUrl}"
        return url
    }

    /**
     * 通过oss接口，处理图片
     * 固定宽高缩放
     */
    fun getImageUrl(imageUrl: String?, width: Int, height: Int): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var url =
            "${enCodeImageUrl(imageUrl)}?x-oss-process=image/resize,m_fixed,w_${width},h_${height}/format,webp"
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
                "${enCodeImageUrl(imageUrl)}?x-oss-process=image/resize,m_fixed,w_${width},h_${height}/format,webp/watermark,image_${waterMark},t_50,g_se,x_10,y_10"
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
                "${enCodeImageUrl(imageUrl)}?x-oss-process=image/watermark,image_${waterMark},t_50,g_se,x_10,y_10"
            Log.e("url=", url)
            return url
        }
    }

    /**
     * 通过oss接口，处理图片
     * 固定宽高，自动裁剪
     */
    fun getImageUrlFill(imageUrl: String?, width: Int, height: Int): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var url =
            "${enCodeImageUrl(imageUrl)}?x-oss-process=image/resize,m_fill,w_${width},h_${height}/format,webp"
        Log.e("url=", url)
        return url
    }

    /**
     * 通过oss接口，处理图片
     */
    fun getDqImageUrl(imageUrl: String?, width: Int, height: Int): String {
        if (imageUrl.isNullOrEmpty()) {
            return ""
        }
        var url =
            BaseApplication.baseUrl + "config/ued/image" + "?imageUrl=${enCodeImageUrl(imageUrl)}&width=${width}&height=${height}"
        Log.e("url=", url)
        return url
    }

    /**
     * 格式化bigdecimal
     */
    fun companreBigDecimal(price: BigDecimal): String {
        if (price == null) {
            return ""
        } else {
            if (BigDecimal(price.toBigInteger()).compareTo(price) == 0) {
                return price.intValueExact().toString()
            } else {
                return price.setScale(2, BigDecimal.ROUND_DOWN).toString()
            }
        }
    }

    /**
     * 获取网页文本
     */
    fun getHtmlText(html: String): String {
        var result = ""

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            result = Html.fromHtml(html).toString()

        }
        return result
    }

    /**
     * 拼接登录信息给url
     */
    fun getTokenUrl(url: String): String? {
        if (url.isNullOrEmpty()) {
            return url
        }
        var token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        if (url.contains("?")) {
            return "${url}&token=${token}"
        }
        return "${url}?token=${token}"
    }

    fun getWeChatUrl(url: String): String? {
        if (url.isNullOrEmpty()) {
            return ""
        }
        if(!url.isNullOrEmpty() && url.contains("?") && url.contains("source=")){
            return url
        }
        var token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        if (url.contains("?")) {

            return "${url}&appToken=${token}&source=android"
        }
        return "${url}?appToken=${token}&source=android"
    }

    /**
     * 拼接景信预约门票地址
     * @param url 景信预订地址
     * @param id 加密id
     */
    fun getJingxinUrl(url: String, id: String, siteId: String): String {
        if (url.isNullOrEmpty()) {
            return ""
        }
        var idStr = URLEncoder.encode(id, "utf-8")
        if (url.contains("?")) {
            return "${url}&identity=${idStr}&siteid=${siteId}"
        }

        return "${url}?identity=${idStr}&siteid=${siteId}"
    }

    /**
     * 获取电子商城地址
     */
    fun getShopUrl(shopUrl: String?, unid: String?): String? {
        var token = SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
        var encry = SPUtils.getInstance().getString("ecryption")
        if(!shopUrl.isNullOrEmpty() && shopUrl.contains("?") && shopUrl.contains("isNativeApp=")){
            return shopUrl
        }
        if (token.isNullOrEmpty()) {
            if(shopUrl!!.contains("?"))
                return "${shopUrl}&isNativeApp=1"
            else
                return "${shopUrl}?isNativeApp=1"
        }
        if(shopUrl!!.contains("?"))
            return "${shopUrl}&token=${token}&isNativeApp=1&unid=${unid}&encryption=${encry}"
        else
            return "${shopUrl}?token=${token}&isNativeApp=1&unid=${unid}&encryption=${encry}"
    }

    /**
     * 获取天气详情地址
     * @param region 地址
     */
    fun getWeatherUrl(region: String): String {
        var weatherUrl = "https://p-ued.daqsoft.com/weapp/weather/#/weather?region="
        return weatherUrl + region
    }

    /**
     * 获取url参数名称和值
     */
    fun getUrlPramNameAndValue(url: String): Map<String, String> {
        var regEx = "(\\?|&+)(.+?)=([^&]*)"
        var p: Pattern = Pattern.compile(regEx)
        var m: Matcher = p.matcher(url)
        var paramMap: LinkedHashMap<String, String> = LinkedHashMap<String, String>()
        while (m.find()) {
            var name = m.group(2)
            var value = m.group(3)
            paramMap.put(name, value)
        }
        return paramMap
    }

    /**
     * 格式化double ，保留两位数
     */
    fun getFormatDouble(value: Double): String? {
        return if (value != null) {
            String.format("%.2f", value)
        } else {
            ""
        }

    }
    /**
     * 志愿者服务要以https开头
     */
    fun getHttpsUrl(url: String): String{
        if (url.isNullOrEmpty()) {
            return ""
        }
        return url
    }

    /**
     * 获取大旗地址
     * @param url 地址
     * @param uuid 电商唯一id
     */
    fun getDqUrl(url: String, uuid: String?): String? {
        if (url.isNullOrEmpty()) {
            return ""
        }
        var dqUrl: String? = ""
        if (url.contains("c.jkxds.net")) {
            // 小电商地址
            dqUrl = getShopUrl(url, uuid)
        } else {
            dqUrl = getWeChatUrl(url)
        }
        return dqUrl
    }

    /**
     * 获取随机数
     */
    fun getRandomString(length: Int): String {
        val sb = StringBuffer("")
        try {
            val random = Random()

            for (i in 0 until length) {
                val number: Int = random.nextInt(3)
                var result: Long = 0
                when (number) {
                    0 -> {
                        result = Math.round(Math.random() * 25 + 65)
                        sb.append(result as Char)
                    }
                    1 -> {
                        result = Math.round(Math.random() * 25 + 97)
                        sb.append(result as Char)
                    }
                    2 -> sb.append(java.lang.String.valueOf(Random().nextInt(10)))
                }
            }
        } catch (e: Exception) {

        }

        return sb.toString()
    }

    fun getHtmlString(content:String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(content,Html.FROM_HTML_MODE_COMPACT);
        } else {
            Html.fromHtml(content);
        }
    }
}