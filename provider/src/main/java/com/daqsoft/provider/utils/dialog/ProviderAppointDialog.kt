package com.daqsoft.provider.utils.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import kotlinx.android.synthetic.main.layout_provider_appoint_dialog.*

/**
 * @Description
 * @ClassName   ProviderAppointDialog
 * @Author      luoyi
 * @Time        2020/5/26 16:55
 */
class ProviderAppointDialog : Dialog {

    var mContext: Context? = null
    var onProviderAppointClickListener: OnProviderAppointClickListener? = null

    constructor(context: Context?) : super(context, R.style.PrivacyStatementDialog) {
        mContext = context
        setContentView(R.layout.layout_provider_appoint_dialog)
        init()
    }


    private fun init() {
        tv_cancel?.onNoDoubleClick {
            dismiss()
        }
        tv_confirm?.onNoDoubleClick {
            dismiss()
            onProviderAppointClickListener?.onProviderAppointClick()
        }
    }

    fun updateData(name: String) {
        tv_appoint_name?.text = "您即将离开本平台，前往${name}"

    }


    fun setTips(txt:String){
        tv_appoint_name?.text = txt
    }

    interface OnProviderAppointClickListener {
        fun onProviderAppointClick()
    }
}