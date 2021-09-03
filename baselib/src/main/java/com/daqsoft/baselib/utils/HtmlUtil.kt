package com.daqsoft.baselib.utils

/**
 * @Description
 * @ClassName   HtmlUtil
 * @Author      luoyi
 * @Time        2020/3/23 13:31
 */
object HtmlUtil {

    /**
     * 获取网页文本
     * @param content 内容文本
     */
    fun getHtmlText(cotent: String): String {
        var head = ""
        head = ("<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<link href=\"file:///android_asset/dq_content_1.0.0.css\" rel=\"stylesheet\">" +
                "<script type=\"text/javascript\" src=\"file:///android_asset/dq_content_1.0.0.js?"
                + java.lang.System.currentTimeMillis() + "\"></script>" +
                "</head>")
        var aa = "<html>$head<body>"
        var bb = "</body></html>"
        return aa + cotent + bb
    }
}