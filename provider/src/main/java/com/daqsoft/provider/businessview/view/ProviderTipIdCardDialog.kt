package com.daqsoft.provider.businessview.view

import android.app.Dialog
import android.content.Context
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import kotlinx.android.synthetic.main.layout_tip_id_card_dialog.*

/**
 * @Description
 * @ClassName   ProviderTipIdCardDialog
 * @Author      luoyi
 * @Time        2020/8/5 15:14
 */
class ProviderTipIdCardDialog : Dialog {

     var onTipIdCardListener: OnTipIdCardListener? = null

    constructor(context: Context) : super(context) {
        setContentView(R.layout.layout_tip_id_card_dialog)
        initView()
    }

    private fun initView() {
        tv_idcard_confirm_dialog?.onNoDoubleClick {
            onTipIdCardListener?.toContinue()
            dismiss()
        }
    }

    interface OnTipIdCardListener {
        fun toContinue()
    }
}