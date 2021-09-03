package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ImageVideoBean
 * @Author      luoyi
 * @Time        2020/6/12 14:10
 */
class ImageVideoBean {
    /**
     * type 1 视频 2图片
     */
    var type: Int = 0
    var image: String? = ""
    var video: String? = ""

    constructor(type: Int, image: String, video: String) {
        this.type = type
        this.image = image
        this.video = video
    }
}