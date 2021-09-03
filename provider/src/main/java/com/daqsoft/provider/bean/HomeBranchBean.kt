package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @des 首页品牌展播列表
 * @author PuHua
 * @Date 2019/12/11 9:59
 */
data class HomeBranchBean(
    // 品牌icon
    val brandIcon: String,
    // 品牌主图
    val brandImage: String,
    // 品牌log
    val brandLogo: String,
    // 站点ID
    val id: String,
    // 主图颜色
    val mainColor: String,
    // 名称
    val name: String,
    // 景区数量
    val scenicCount: String,
    // 景区名称字符串
    var relationResourceNameStr: String,
    // 站点数量
    val siteCount: String,
    // 站点名称
    val siteNameStr: String,
    // 口号
    val slogan: String,
    // 关联的资源数
    val relationResourceCount:Int
)
/**
 * @des 首页品牌详情
 * @author PuHua
 * @Date 2019/12/24 10:53
 */
data class BranchDetailBean(
    // 品牌icon
    val brandIcon: String,
    // 品牌主图
    val brandImage: String,
    // 品牌llogo
    val brandLogo: String,
    // 收藏状态
    var collectStatus: String,
    // 数据ID
    val id: String,
    // 名称
    val name: String,
    // 地区编码
    val region: String,
    // 景区数量
    val scenicCount: String,
    // 景区名称字符串
    val scenicNameStr: String,
    // 景区ID字符串
    val scenicStr: String,
    // 站点数量
    val siteCount: String,
    // 站点名称字符串
    val siteNameStr: String,
    // 品牌口号
    val slogan: String,
    // 品牌简介
    val suggest: String,
    // 点赞状态
    var thumbStatus: Boolean,
    //收藏数
    var collectCount:String,
    //点赞数
    var thumbCount:String,
    //主色调
    var mainColor:String,
    /**
     * 资源数量
     */
    var relationResourceCount: String,
    /**
     * 资源名称字符串
     */
    var relationResourceNameStr:String
)
//目的地
@Parcelize
data class BrandMDD(
    val id: Int,
    val images: String,
    val logo: String,
    val name: String,
    val region: String,
    val regionMemo: String,
    val scenicCount: Int,
    val scenicNameStr: String,
    val siteLabelNames: String,
    val slogan: String,
    val videoImage: String,
    val videoUrl: String
) : Parcelable

data class BrandSiteInfo(
    /**
     * 音频地址
     */
    var audioUrl: String,
    /**
     * 720全景地址
     */
    var fullViewUrl: String,
    /**
     * 视频封面地址
     */
    var videoCover: String,
    /**
     * 视频url
     */
    var videoUrl: String,
    /**
     * 标签名称
     */
    var labelName: String,
    /**
     * 地区名称
     */
    var regionName: String,
    /**
     * 封面图
     */
    var images: String,
    /**
     * 名称
     */
    var name: String,
    /**
     * 资源类型
     */
    var resourceType: String,
    /**
     * 资源ID
     */
    var resourceId: Int,
    /**
     * 金牌解说URL
     */
    var commentaryUrl: String,
    /**
     * 地区编码
     */
    var region: String
)