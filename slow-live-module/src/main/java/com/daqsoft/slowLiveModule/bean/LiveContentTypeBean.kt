package com.daqsoft.slowLiveModule.bean


internal data class LiveContentTypeBean(
    var backgroundImg: String = "",
    var channelCode: String = "", // mzb
    var english: String = "",
    var id: Int = 0, // 2185
    var logo: String = "",
    var name: String = "", // 慢直播
    var subset: List<Subset> = listOf()
) {
    data class Subset(
        var backgroundImg: String = "",
        var channelCode: String = "", // ckxj
        var content: String = "",
        var createTime: String = "", // 2020-04-13 20:23:14
        var createUser: Int = 0, // 22
        var english: String = "",
        var id: Int = 0, // 2186
        var logo: String = "",
        var name: String = "", // 此刻新疆
        var pid: Int = 0, // 2185
        var showChannel: Boolean = false, // true
        var siteId: Int = 0, // 75
        var sort: Int = 0, // 999
        var status: Int = 0, // 1
        var summary: String = "",
        var updateTime: String = "",
        var updateUser: String = "",
        var url: String = ""
    )
}

