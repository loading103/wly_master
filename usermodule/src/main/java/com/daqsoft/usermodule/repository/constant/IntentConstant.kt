package com.daqsoft.usermodule.repository.constant

/**
 * @Description 跳转带参数名
 * @ClassName   IntentConstant
 * @Author      PuHua
 * @Time        2019/11/5 15:32
 */
class IntentConstant {
    companion object{
        /**
         * 页面传对象
         */
        const val OBJECT = "object"
        /**
         * 页面传类型
         */
        const val TYPE = "type"

        /**实物订单*/
        const val ORDER_TYPE_REAL= 1

        /**虚拟订单*/
        const val ORDER_TYPE_VIRTUAL= 2

        /**门票订单*/
        const val ORDER_TYPE_TICKET= 3

        /**酒店订单*/
        const val ORDER_TYPE_HOTEL= 5

        /**路线订单*/
        const val ORDER_TYPE_ROUTE= 6

    }
}