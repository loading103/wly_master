package com.daqsoft.provider.businessview.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.daqsoft.provider.R
import kotlinx.android.synthetic.main.dialog_code_tip.*
import org.jetbrains.anko.append

/**
 * @Description 温馨提示统一弹窗
 * @ClassName   ProviderTipDialog
 * @Author      luoyi
 * @Time        2020/5/28 11:36
 */
class TipDialog : Dialog {

    private var content: String? = ""
    private var name: String? = ""
    private var onTipConfirmListener: OnTipConfirmListener? = null
    private var onTipToCanceListener: onTipCanceListener? = null
    lateinit var spanContent: SpannableStringBuilder

    constructor(context: Context?, builder: Builder) : super(context) {
        setContentView(R.layout.dialog_code_tip)
        if (builder != null) {
            content = builder.content
            name = builder.name
            onTipConfirmListener = builder.onTipConfirmListener
            onTipToCanceListener = builder.onTipCanceListener
            spanContent = builder.spanContent
        }
        initViewEvent()
    }

    private fun initViewEvent() {
        if (context != null) {
            /*  var firstStr: String = context!!.getString(R.string.venue_res_first)
              var twoStr: String = context!!.getString(R.string.venue_res_two, name)
              var threeStr: String = context!!.getString(R.string.venue_res_three)
              var spanContent: SpannableStringBuilder = SpannableStringBuilder("")
              spanContent.append(firstStr)
              var forregroundCSpan = ForegroundColorSpan(context.resources.getColor(R.color.main_selected))
              spanContent.append(twoStr, forregroundCSpan, 0, twoStr.length)
              spanContent.append(threeStr)*/
            if (spanContent.isNotEmpty()) {
                tv_tip_content?.text = spanContent
            }
            if (content != null && content!!.isNotEmpty()) {
                tv_tip_content?.text = content
            }

        }
        tv_cancel?.setOnClickListener {
            dismiss()
            onTipToCanceListener?.onCance()
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
        internal var name: String? = ""
        lateinit var spanContent: SpannableStringBuilder
        /**
         * 设置文本内容
         */
        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        fun setSpanContent(spanContent: SpannableStringBuilder): Builder {
            this.spanContent = spanContent
            return this
        }

        /**
         * 设置提示确认弹窗事件
         */
        fun setOnTipConfirmListener(onTipConfirmListener: OnTipConfirmListener): Builder {
            this.onTipConfirmListener = onTipConfirmListener
            return this
        }

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        /**
         * 设置提示取消弹窗事件
         */
        fun setOnTipCanceListener(onTipCanceListener: onTipCanceListener): Builder {
            this.onTipCanceListener = onTipCanceListener
            return this
        }

        fun create(context: Context): TipDialog {
            return TipDialog(context, this)
        }
    }

    interface OnTipConfirmListener {
        fun onConfirm()
    }

    interface onTipCanceListener {
        fun onCance()
    }
}