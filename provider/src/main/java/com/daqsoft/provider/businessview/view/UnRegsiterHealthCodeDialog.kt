package com.daqsoft.provider.businessview.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.view.dialog.SincerityDialog
import kotlinx.android.synthetic.main.layout_register_helth_code_dialog.*
import org.jetbrains.anko.append
import org.jetbrains.anko.sdk27.coroutines.onLongClick

/**
 * @Description
 * @ClassName   QrCodeDialog
 * @Author      luoyi
 * @Time        2020/3/25 16:06
 */
class UnRegsiterHealthCodeDialog : Dialog {

    /**
     * 标题
     */
    var title: String? = null
    /**
     * 二维码地址
     */
    private var qrCodeImageUrl: String? = null
    /**
     * 下载图片响应事件
     */
    private var onDownLoadListener: OnDownLoadListener? = null

    constructor(context: Context, builder: Builder) : super(context) {
        this.title = builder.title
        this.qrCodeImageUrl = builder.qrCodeImageUrl
        this.onDownLoadListener = builder.onDownLoadListener
        setContentView(R.layout.layout_register_helth_code_dialog)
        initView()
    }


    private fun initView() {
        if (context != null) {

            var sbstr: SpannableStringBuilder = SpannableStringBuilder("")
            var redFspan = ForegroundColorSpan(Color.parseColor("#ff4e4e"))
            var firstStr = context.getString(R.string.venue_unregister_health_code_first)
            var sconedStr = context.getString(R.string.venue_unregister_health_code_second)
            var threeStr = context.getString(R.string.venue_unregister_health_code_three)
            if (!qrCodeImageUrl.isNullOrEmpty()) {
                if (!qrCodeImageUrl!!.contains("http")) {
                    threeStr = "" + qrCodeImageUrl
                    img_qr_code?.visibility = View.GONE
                    tv_save_qr_code?.visibility = View.GONE
                } else {
                    sconedStr = sconedStr + "请前往"
                    tv_save_qr_code?.visibility = View.VISIBLE

                    img_qr_code?.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(qrCodeImageUrl)
                        .error(R.mipmap.placeholder_img_fail_240_180)
                        .into(img_qr_code)
                }
            }
            var fourStr = context.getString(R.string.venue_unregister_health_code_four)
            var blueFspan = ForegroundColorSpan(Color.parseColor("#36cd64"))
            sbstr.append(firstStr, redFspan, 0, firstStr.length)
            sbstr.append(sconedStr)
            sbstr.append(threeStr, blueFspan, 0, threeStr.length)
            sbstr.append(fourStr)
            tv_qrcode_dialog_content?.text = sbstr
        }
        img_qr_code?.onLongClick {
            if (!qrCodeImageUrl.isNullOrEmpty() && qrCodeImageUrl!!.contains("http")) {
                // 保存图片到本地
                downloadImage(qrCodeImageUrl)
                dismiss()
            } else {
                if (!qrCodeImageUrl.isNullOrEmpty()) {
                    ToastUtils.showMessage(qrCodeImageUrl)
                    dismiss()
                } else {
                    ToastUtils.showMessage("非常抱歉，未获取到二维码信息,无法下载~")
                    dismiss()
                }
            }
        }
        tv_qrcode_confirm_dialog.onNoDoubleClick {
            dismiss()
        }
    }

    private fun downloadImage(qrCodeImageUrl: String?) {
        if (!qrCodeImageUrl.isNullOrEmpty() && onDownLoadListener != null) {
            onDownLoadListener!!.onDownLoadImage(qrCodeImageUrl)
        }
    }


    /**
     *  修改dialog 数据
     */
    fun updateData(url: String, title: String) {
        this.qrCodeImageUrl = url
        this.title = title
        initView()
    }

    class Builder {
        internal var qrCodeImageUrl: String? = null

        internal var title: String? = null

        internal var onDownLoadListener: OnDownLoadListener? = null
        fun qrCodeImageUrl(url: String): Builder {
            qrCodeImageUrl = url
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun onDownLoadListener(onDownLoadListener: OnDownLoadListener): Builder {
            this.onDownLoadListener = onDownLoadListener
            return this
        }

        fun build(context: Context): UnRegsiterHealthCodeDialog {
            return UnRegsiterHealthCodeDialog(context, this)
        }
    }

    interface OnDownLoadListener {

        fun onDownLoadImage(url: String)
    }
}