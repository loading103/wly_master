package com.daqsoft.provider.network.home.bean

/**
 * 内容审核不通过原因实体类
 * @author 黄熙
 * @date 2020/2/20 0020
 * @version 1.0.0
 * @since JDK 1.8
 */
class NoPassMsgBean(
    /**
     * 语音列表
     */
    var voiceList: MutableList<NoPassResourceBean>,
    /**
     * 视频列表
     */
    var videoList: MutableList<NoPassResourceBean>,
    /**
     * 图片列表
     */
    var imageList: MutableList<NoPassResourceBean>,
    /**
     * 文本内容列表
     */
    var textList: MutableList<NoPassWordBean>

)

/**
 * 语音、视频、图片资源的实体类
 */
class NoPassResourceBean(
    /**
     * 资源地址
     */
    var url: String,
    /**
     * 不通过信息
     */
    var description: String
)

/**
 * 文字内容的实体类
 */
class NoPassWordBean(
    /**
     * 不通过原因信息
     */
    var label: String,
    /**
     * 不通过内容（需要标红字体，注意返回值没有双引号）
     */
    var context: String
)