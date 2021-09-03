package com.daqsoft.provider.bean

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
data class ProductListBean(
    val mallUrl: String?,
    val productList: List<Product>?
){
    data class Product(
        val exchangeIntegral: Int?,
        val id: Int?,
        val image: String?,
        val marketPrice: Int?,
        val productName: String?,
        val url: String?
    )
}

