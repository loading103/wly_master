package com.daqsoft.android

/**
 * 公共URL
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
object CommonURL {

    /**
     * 正式环境和站点
     */
    const val DEBUG: Boolean = false
    const val SITE_CODE = "site241962";
    /**
     * 测试环境和站点
     */
//    const val DEBUG: Boolean = false
//    const val SITE_CODE = "site010775";
//    const val DEBUG: Boolean = true
//    const val SITE_CODE = "site488314";


    /**
     * 小电商支付地址
     * 测试域名：http://pay.test.jkxds.net
     */
    const val ELECTRONIC_PAY_URL = "http://pay.test.jkxds.net"

    /**
     * 文件上传基本路径
     * http://file.geeker.com.cn
     */
    const val FILE_BASE_URL = "https://file.geeker.com.cn/"


    /**
     * 文海本地测试地址
     */
//    const val SITE_CODE="site454792"
    /**
     * 政府云测试站带你
     */
//    const val SITE_CODE = "site189918"
    /**
     * 区分当前打包信息
     */
    const val APP_AREA = "sc"

    /**
     * 用于大旗云 版本更新id 四川文旅云APP
     */
    const val APP_VERSION_SYS_ID = "81763"

    /**
     * 版本更新地址
     */
    const val VERSION_URL = "https://app.daqsoft.com/appserives/Services.aspx"

    /**
     * 活动分享链接
     */
    const val SHARE_BASE_URL =
        "https://+$SITE_CODE.c-ctc.test.daqsoft.com/#/activity-detail/669"

    /**
     * 四川投诉地址
     */
    const val COMPLAINT_URL = "https://site241962.c.dsichuan.com/#/sc-complain"
    /**
     * 微官网地址
     */
    const val WEB_SITE_URL = "https://site241962.c.dsichuan.com/"

    /**
     * 默认城市code
     */
    const val DEFAULT_ADCODE = "510000"

    /**
     * 旅云测试环境
     * http://ctc-api.test.daqsoft.com/v2/
     * c.daqctc.com
     * www.daqctc.com/api/
     * 获取 文旅云接口地址
     */
    fun getBaseUrl(): String {
        return if (DEBUG) {
//            "http://www.daqctc.com/api/"
//            "http://10.252.251.32:7030/"
            "http://ctc-api.test.daqsoft.com/v2/"
        } else {
//            "http://www.daqctc.com/api/"
            "http://zytf.dsichuan.com/api/"
        }
    }

    /**
     * 小电商跟地址
     * 测试域名：http://api.test.jkxds.net
     * 正式域名：http://api.jkxds.net
     */
    fun getElectronicBaseUrl(): String {
        return if (DEBUG) {
            "http://api.test.jkxds.net/culturalcloud/1.0/"
        } else {
            "https://api.jkxds.net/culturalcloud/1.0/"
        }
    }

    /**
     * 数据上报地址
     */
    fun getUploadDataUrl(): String {
        return if (DEBUG) {
            "http://bdsp.test.daqsoft.com/"
        } else {
            "http://bdsp.test.daqsoft.com/"
        }
    }

    /**
     * 获取微官网地址
     */
    fun getWebSiteUrl(): String {
        return if (DEBUG) {
            "http://${SITE_CODE}.c-ctc.test.daqsoft.com/#/"
        } else {
            "https://${SITE_CODE}.c.dsichuan.com/#/"
        }
    }

}