package com.daqsoft.servicemodule.bean

import com.chad.library.adapter.base.entity.MultiItemEntity


class BusLineDetailBean(var type: Int, var message: Buslines?) : MultiItemEntity {
    override fun getItemType(): Int {
        return type
    }
    companion object {
        val BUS_HEADER: Int=1
        val BUS_WALK = 2
        val BUS_BUS = 3
        val BUS_FOOTER = 4
    }
}