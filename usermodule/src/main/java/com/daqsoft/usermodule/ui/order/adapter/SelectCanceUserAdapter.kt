package com.daqsoft.usermodule.ui.order.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.provider.bean.UserContact
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemSelectCanceUserBinding

/**
 * @Description 选择取消点单的人
 * @ClassName   SelectCanceUserAdapter
 * @Author      luoyi
 * @Time        2020/8/4 16:49
 */
class SelectCanceUserAdapter :
    RecyclerViewAdapter<ItemSelectCanceUserBinding, OrderAttacthPersonBean> {

    var mContext: Context? = null
    var onSelectCanceUserListener: OnSelectCanceUserListener? = null

    constructor(context: Context) : super(R.layout.item_select_cance_user) {
        this.mContext = context
        emptyViewShow=false
    }

    override fun setVariable(
        mBinding: ItemSelectCanceUserBinding,
        position: Int,
        item: OrderAttacthPersonBean
    ) {
        item?.let {
            if (item.isSelect == 1) {
                mBinding.imgSelectStatus.setImageResource(R.mipmap.register_button_choose_selected)
            } else {
                mBinding.imgSelectStatus.setImageResource(R.mipmap.register_button_choose_normal)
            }
            mBinding.name = "" + it.userName
            mBinding.root.onNoDoubleClick {
                onSelectCanceUserListener?.onSelectStatus(position)
            }
        }
    }

    override fun payloadUpdateUi(
        mBinding: ItemSelectCanceUserBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "update_select_status") {
            var item = getData()[position]
            if (item.isSelect == 1) {
                mBinding.imgSelectStatus.setImageResource(R.mipmap.register_button_choose_selected)
            } else {
                mBinding.imgSelectStatus.setImageResource(R.mipmap.register_button_choose_normal)
            }
        } else if (payloads[0] == "select_status") {
            var item = getData()[position]
            if (item.isContainName) {
                mBinding.root.setBackgroundColor(mContext!!.resources.getColor(R.color.app_main_forbidden))
            } else {
                mBinding.root.setBackgroundColor(mContext!!.resources.getColor(R.color.white))
            }
        }
        super.payloadUpdateUi(mBinding, position, payloads)
    }

    interface OnSelectCanceUserListener {
        fun onSelectStatus(position: Int)
    }
}