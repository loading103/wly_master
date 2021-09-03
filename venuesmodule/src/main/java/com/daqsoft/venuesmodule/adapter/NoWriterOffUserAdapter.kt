package com.daqsoft.venuesmodule.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.UserContact
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemNoWriterOffUserBinding

/**
 * @Description 不核销用户适配器
 * @ClassName   NoWriterOffUserAdapter
 * @Author      luoyi
 * @Time        2020/8/5 10:28
 */
class NoWriterOffUserAdapter : RecyclerViewAdapter<ItemNoWriterOffUserBinding, UserContact> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_no_writer_off_user) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemNoWriterOffUserBinding, position: Int, item: UserContact) {

    }
}