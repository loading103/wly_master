package com.daqsoft.provider.bean

import com.daqsoft.provider.getRealImageUrl
import com.google.gson.annotations.SerializedName

/**
 * 我的关注社团实体类
 * @author 黄熙
 * @date 2020/2/29 0029
 * @version 1.0.0
 * @since JDK 1.8
 */
class MineFocusMassBean(
    /**
     * 图片
     */
    @SerializedName("image")
    var imageUrls: String,
    /**
     * 浏览量
     */
    var showNum: Int,
    /**
     * 粉丝
     */
    var num: Int,
    /**
     * 名称
     */
    var name: String,
    /**
     * id
     */
    var id: Int,
    /**
     * 类型
     */
    var type: String,
    /**
     * 关注状态
     */
    var resourceFansStatus: resourceFansStatus
) {
    fun getImage(): String = imageUrls.getRealImageUrl()
}

/**
 * 是否关注的状态
 */
class resourceFansStatus(
    /**
     * 关注的状态
     */
    var fansStaus: Boolean
)