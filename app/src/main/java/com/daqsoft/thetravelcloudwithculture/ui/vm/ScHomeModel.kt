package com.daqsoft.thetravelcloudwithculture.ui.vm

import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.bean.ValueResBean
import com.daqsoft.thetravelcloudwithculture.R

object ScHomeModel {

    /**
     * 个人中心 我的订单栏目
     */
    fun getMineOrderTab(): MutableList<ValueResBean> {
        var temp: MutableList<ValueResBean> = mutableListOf()
//        temp.add(ValueResBean("全部订单", "order", R.mipmap.mine_icon_all_bills))
        temp.add(ValueResBean("我的预约", "appointMent", R.mipmap.mine_icon_my_book))
        temp.add(ValueResBean("我的活动", "activity", R.mipmap.mine_icon_my_activity))
//        temp.add(ValueResBean("优惠券", "coupon", R.mipmap.mine_center_icon_coupon))
        temp.add(ValueResBean("消费码", "counsum", R.mipmap.mine_icon_code))
        return temp
    }
}