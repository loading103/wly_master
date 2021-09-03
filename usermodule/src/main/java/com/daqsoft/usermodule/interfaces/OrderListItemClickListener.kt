package com.daqsoft.usermodule.interfaces

import android.view.View
import com.daqsoft.provider.bean.OrderListBean

/**
 * 小电商订单列表item点击事件
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-12-5
 * @since JDK 1.8.0_191
 */
interface OrderListItemClickListener {
    fun click(item: OrderListBean.X?, view: View?)
}