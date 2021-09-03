package com.daqsoft.android

/**
 * 公共URL
 * @author luoyi
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
object CommonURL {


    /**
     * 标识环境 false正式  true开发&测试
     */
    const val DEBUG: Boolean = true

    /**
     * 文件上传基本路径
     * http://file.geeker.com.cn
     */
    const val FILE_BASE_URL = "https://file.geeker.com.cn/"
    /**
     * 站点编号
     */
//    const val SITE_CODE = "site833997"

//    const val SITE_CODE = "site694940"

//    const val SITE_CODE = "site241962"

    /**
     * 邹倩站点
     */
//    const val SITE_CODE = "site333474"
//    const val SITE_CODE="site583029"

    /**
     * 新疆
     */
//    const val SITE_CODE = "site688790"

    /**
     * 颜斌测试站点
     */
    const val SITE_CODE = "site488314"
//    const val SITE_CODE = "site833997"


    /**
     * 活动分享链接
     */
    const val SHARE_BASE_URL =
        "http://+$SITE_CODE.c-ctc.test.daqsoft.com/#/activity-detail/669"

    /**
     * 游新疆投诉地址
     */
    const val COMPLAINT_URL = "http://m.ucomplain.12301.cn/view/complaintmobile#/"

    /**
     * 区分当前打包信息
     */
    const val APP_AREA = "test"

    /**
     * 用于大旗云 版本更新id 游新疆
     */
    const val APP_VERSION_SYS_ID = "43547"
//    /**
//     * 测试
//     */
//    const val APP_VERSION_SYS_ID="93734"
    /**
     * 版本更新地址
     */
    const val VERSION_URL = "http://app.daqsoft.com/appserives/Services.aspx"

    /**
     * 默认城市code
     */
    const val DEFAULT_ADCODE = "650100"

    /**
     * 旅云测试环境
     * http://ctc-api.test.daqsoft.com/v2/
     * c.daqctc.com
     * www.daqctc.com/api/
     * 获取 文旅云接口地址
     */
    fun getBaseUrl(): String {
//        return if (DEBUG) {
//            "http://ctc-api.test.daqsoft.com/v2/"
//        } else {
//            "http://www.xn--efvu3ql9f.com/api/"
//        }

        return if (DEBUG) {
            "http://ctc-api.test.daqsoft.com/v2/"
//            "http://10.252.251.132:7030/"
        } else {
            "http://www.xn--efvu3ql9f.com/api/"
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
            "https://${SITE_CODE}.c.xn--efvu3ql9f.com/#/"
        }
    }
}