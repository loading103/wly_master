package com.daqsoft.provider.view.dialog

import android.app.Dialog
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import kotlinx.android.synthetic.main.layout_provider_tip_dialog.*

/**
 * @Description
 * @ClassName   SincerityDialog
 * @Author      luoyi
 * @Time        2020/5/30 17:11
 */
class SincerityDialog : Dialog {

    private var content: String? = ""
    private var onTipConfirmListener: OnTipConfirmListener? = null
    private var onTipToCanceListener: onTipCanceListener? = null

    constructor(context: Context?, builder: Builder) : super(context, R.style.PrivacyStatementDialog) {
        setContentView(R.layout.layout_provider_sincerity_dialog)
        if (builder != null) {
            content = builder.content
            onTipConfirmListener = builder.onTipConfirmListener
            onTipToCanceListener = builder.onTipCanceListener
        }
        initViewEvent()
    }

    private fun initViewEvent() {
        if (!content.isNullOrEmpty())
            tv_tip_content?.text = content

        tv_cancel?.onNoDoubleClick {
            dismiss()
            onTipToCanceListener?.onCance()
        }
        tv_confirm?.onNoDoubleClick {
            dismiss()
            // 跳转我的诚信页面
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                .navigation()
            onTipConfirmListener?.onConfirm()
        }
    }

    public fun updateTipContent(content: String) {
        this.content = content
        if (!content.isNullOrEmpty()) {
            tv_tip_content.text = content
        }
    }

    class Builder {
        internal var content: String? = ""

        internal var onTipConfirmListener: OnTipConfirmListener? = null
        internal var onTipCanceListener: onTipCanceListener? = null

        /**
         * 设置文本内容
         */
        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        /**
         * 设置提示确认弹窗事件
         */
        fun setOnTipConfirmListener(onTipConfirmListener: OnTipConfirmListener): Builder {
            this.onTipConfirmListener = onTipConfirmListener
            return this
        }

        /**
         * 设置提示取消弹窗事件
         */
        fun setOnTipCanceListener(onTipCanceListener: onTipCanceListener): Builder {
            this.onTipCanceListener = onTipCanceListener
            return this
        }

        fun create(context: Context): SincerityDialog {
            return SincerityDialog(context, this)
        }
    }

    interface OnTipConfirmListener {
        fun onConfirm()
    }

    interface onTipCanceListener {
        fun onCance()
    }
}