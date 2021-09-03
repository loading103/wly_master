package com.daqsoft.itinerary.bean

data class ScenicTicketBean(
    val sales: Int,
    val needFaceRecognition: Boolean,
    val sptTitleList: List<SptTitleList>
)

data class SptTitleList(
    val isActivity: Boolean,
    val minSellPrice: Double,
    val sptTitle: String,
    val productList: List<ProductList>
)

data class ProductList(
    val allowRefund: String,
    val goodsSn: String,
    val intervalTimes: Int,
    val isActivity: Boolean,
    val isOnSale: Boolean,
    val lowerIntervalTimes: Int,
    val lowerTime: Int,
    val needFaceRecognition: Boolean,
    val needTicket: String,
    val productId: Int,
    val productName: String,
    val purchaseLimit: String,
    val saleTime: Long,
    val sellPrice: Double
)