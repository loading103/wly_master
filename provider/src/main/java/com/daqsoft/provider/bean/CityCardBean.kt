package com.daqsoft.thetravelcloudwithculture.home.bean

import com.daqsoft.provider.bean.HomeAd

/**
 * @des 城市名片
 * @author PuHua
 * @Date 2019/12/20 9:53
 */
data class CityCardBean(
    // 封面图
    val coverImage: String,
    // 名称ID
    val id: Int,
    // 图片列表
    val images: List<String>,
    // 介绍
    val introduce: String,
    // logo
    val logo: String,
    // 名称
    val name: String,
    // 全景地址
    val panoramaUrl: String,
    // 地区编码
    val region: String,
    // 地区名称
    val regionNameStr: String,
    // 站点ID
    val siteId: Int,
    // 概述
    val summary: String,
    // 视频URL
    val video: String,
    // 视频封面
    val videoCover: String,
    // 天气
    val weather: Weather,
    /**
     * 英文介绍
     */
    var english: String,
    val positionFlag:Boolean = false,
    val tryRunFlag:Boolean = false,
    val regionName:String = "",
    val isFirst:Boolean = true,
    /**
     * 首页广告logo
     */
    var appIndexLog:HomeAd?
    )

/**
 * @des 城市名片根据站点id获取的
 * @author PuHua
 * @Date 2019/12/20 14:23
 */
data class CityCardDetail(
    // 封面图
    val coverImage: String,
    // 名片id
    val id: Int,
    // 图片
    val images: List<String>,
    // 介绍
    val introduce: String,
    // 名片logo
    val logo: String,
    // 名称
    val name: String,
    // 地区编码
    val region: String,
    // 站点id
    val siteId: Int,
    // 概述
    val summary: String,
    // 视频
    val video: String,
    //第二个视频
    val videoEx: String?,
    // 视频封面图
    val videoCover: String,
    //第二个视频封面
    val videoCoverEx: String,
    // 720全景
    val panoramaUrl: String
)

/**
 * @des 天气
 * @author PuHua
 * @Date 2019/12/20 9:54
 */
data class Weather(
    // 空气质量指数
    val api: Int,
    // 最高温度(摄氏度)
    val max: String,
    // 最低温度(摄氏度)
    val min: String,
    // 白天天气图标地址
    val pic: String,
    // 空气质量等级
    val qlty: String,
    // 生活建议
//    val suggestion: Suggestion,
    // 白天天气描述
    val txt: String,
    // 白天天气图标Unicode
    val unicode: String
)

/**
 * @des 生活建议
 * @author PuHua
 * @Date 2019/12/20 9:57
 */
data class Suggestion(
    // 体感舒适指数
    val comf: Comf,
    // 洗车指数
    val cw: Cw,
    // 穿衣指数
    val drsg: Drsg,
    // 感冒指数
    val flu: Flu,
    // 运动指数
    val sport: Sport,
    // 旅游指数
    val trav: Trav,
    // 紫外线指数
    val uv: Uv
)

/**
 * @des 体感舒适指数
 * @author PuHua
 * @Date 2019/12/20 9:58
 */
data class Comf(
    val brf: String,
    val txt: String
)

/**
 * @des 洗车指数
 * @author PuHua
 * @Date 2019/12/20 9:59
 */
data class Cw(
    val brf: String,
    val txt: String
)

/**
 * @des 穿衣指数
 * @author PuHua
 * @Date 2019/12/20 9:59
 */
data class Drsg(
    val brf: String,
    val txt: String
)

/**
 * @des  感冒指数
 * @author PuHua
 * @Date 2019/12/20 9:59
 */
data class Flu(
    val brf: String,
    val txt: String
)

/**
 * @des 运动指数
 * @author PuHua
 * @Date 2019/12/20 9:58
 */
data class Sport(
    val brf: String,
    val txt: String
)

/**
 * @des 旅游指数
 * @author PuHua
 * @Date 2019/12/20 9:59
 */
data class Trav(
    val brf: String,
    val txt: String
)

/**
 * @des 紫外线指数
 * @author PuHua
 * @Date 2019/12/20 9:59
 */
data class Uv(
    val brf: String,
    val txt: String
)