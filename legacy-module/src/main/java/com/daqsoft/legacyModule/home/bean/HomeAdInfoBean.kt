package com.daqsoft.legacyModule.home.bean

internal data class HomeAdInfoBean(
    var adInfo: List<AdInfo> = listOf(),
    var positionMethod: String = "" // IMAGE
) {
    data class AdInfo(
        var adId: Int = 0, // 133
        var imgUrl: String = "", // https://file.geeker.com.cn/cultural-tourism-cloud/1586875018141/非遗.jpg
        var jumpUrl: String = "", // https://mp.weixin.qq.com/s/Je4_upISCod-Be24Ef6wHg
        var resourceId: String = "",
        var type: String = "" // OUTSIDE
    )
}