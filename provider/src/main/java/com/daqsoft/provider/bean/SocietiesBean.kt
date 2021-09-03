package com.daqsoft.provider.bean

/**
 * @package name：com.daqsoft.provider.bean
 * @date 2020/9/16 16:35
 * @author zp
 * @describe
 */
data class SocietiesBean(
    val fansNum: Int,//粉丝数
    val id: Int,//数据id
    val image: String,//图片
    val lookNum: Int, //浏览量
    val name: String,//名称
    val resourceFansStatus: FansStatus,//是否关注状态
    val summary: String,//概况
    val type: String//类型
)

data class FansStatus(
    val fansStaus: Boolean
)