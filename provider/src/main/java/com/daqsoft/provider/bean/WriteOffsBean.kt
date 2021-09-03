package com.daqsoft.provider.bean

/**核销列表实体类*/
data class WriteOffsBean(
    val endTime: String,
    val orderId: Int,
    val orderStatus: Int,
    val startTime: String,
    val useNum: Int,
    val userName: String
)