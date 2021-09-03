package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ShareBean
 * @Author      luoyi
 * @Time        2020/6/1 16:54
 */
class SharePlatBean {
    /**
     * 分享平台标题
     */
    var sharePlatTitle: String? = ""
    /**
     * 分享平台图片
     */
    var sharePlatRes: Int? = 0
    /**
     * 分享平台类型
     */
    var sharePlatType: String? = ""


    constructor(title: String, plateRes: Int, plateType: String) {
        this.sharePlatTitle = title
        this.sharePlatRes = plateRes
        this.sharePlatType = plateType
    }


}