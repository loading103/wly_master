package com.daqsoft.itinerary

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 16:02
 * @Description：
 */
object ConfigInfo {

    /**
     * 标识环境 false正式  true开发&测试
     */
    private const val DEBUG: Boolean = true

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
     * 新疆-乌
     */
//    const val SITE_CODE = "site688790"

    /**
     * 颜斌测试站点
     */
    const val SITE_CODE="site488314"
    /**
     * 活动分享链接
     */
    const val SHARE_BASE_URL =
        "http://+$SITE_CODE.c-ctc.test.daqsoft.com/#/activity-detail/669"

    /**
     * 区分当前打包信息
     */
    const val APP_AREA = "xj"

    /**
     * 用于大旗云 版本更新id 游新疆
     */
    const val APP_VERSION_SYS_ID = "16357"
    /**
     * 版本更新地址
     */
    const val VERSION_URL = "http://app.daqsoft.com/appserives/Services.aspx"

    /**
     * 旅云测试环境
     * http://ctc-api.test.daqsoft.com/v2/
     * c.daqctc.com
     * www.daqctc.com/api/
     * 获取 文旅云接口地址
     */
    fun getBaseUrl(): String {
        return if (DEBUG) {
            "http://ctc-api.test.daqsoft.com/v2/"
        } else {
            "https://www.daqctc.com/api/"
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

}