package com.daqsoft.usermodule.ui.order

import com.daqsoft.provider.ARouterPath

/**
 * @Description 订单详情/预订详情/消费码详情页面工厂
 * @ClassName   OrderDetailUIFactory
 * @Author      PuHua
 * @Time        2019/12/13 11:18
 */
class OrderDetailUIFactory {
    /**
     * @des 订单详情生成器
     * @author PuHua
     * @Date 2019/12/13 11:48
     */
    class Builder(type: String) {
        // 订单类型
        val type = type

        /**
         * 根据不同的订单类型跳转到不同的页面
         */
        fun build(): String {
            return when (type) {
                // 实物订单
                "1" -> ARouterPath.UserModule.USER_ELECTRONIC_IN_KIND_DETAIL_ACTIVITY
                // 虚拟订单
                "2" -> ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY
                // 门票订单
                "3" -> ARouterPath.UserModule.USER_ELECTRONIC_TICKET_DETAIL_ACTIVITY
                // 酒店订单
                "5" -> ARouterPath.UserModule.USER_ELECTRONIC_HOTEL_DETAIL_ACTIVITY
                // 路线订单
                "6" -> ARouterPath.UserModule.USER_ELECTRONIC_LINE_DETAIL_ACTIVITY

                else -> ARouterPath.UserModule.USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY
            }
        }
    }
}