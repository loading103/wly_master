package com.daqsoft.baselib.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.ImageLoadUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.base_qrcode_dialog.*

/**
 * @Description
 * @ClassName   QrCodeDialog
 * @Author      luoyi
 * @Time        2020/3/25 16:06
 */
class QrCodeDialog(context: Context, builder: Builder) : Dialog(context) {

    /**
     * 标题
     */
    var title: String? = null

    var isWeb: Boolean = false
    /**
     * 二维码地址
     */
    private var qrCodeImageUrl: String? = null
    /**
     * 下载图片响应事件
     */
    private var onDownLoadListener: OnDownLoadListener? = null

    init {
        this.title = builder.title
        this.isWeb = builder.isWeb
        this.qrCodeImageUrl = builder.qrCodeImageUrl
        this.onDownLoadListener = builder.onDownLoadListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isWeb){
            setContentView(R.layout.base_qrcode_dialog_web)
        }else{
            setContentView(R.layout.base_qrcode_dialog)
        }
        initView()
    }

    private fun initView() {
        if(isWeb){
            ImageLoadUtil.glidePersonLoadUrl(context,qrCodeImageUrl,img_qr_code)
        }else{
            tv_qrcode_dialog_title?.text = title
            Glide.with(context)
                .load(qrCodeImageUrl)
                .into(img_qr_code)
        }

        img_qr_code?.setOnLongClickListener(object :View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                // 保存图片到本地
                downloadImage(qrCodeImageUrl)
                dismiss()
                return true
            }


        })

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
        internal var isWeb: Boolean = false
        internal var onDownLoadListener: OnDownLoadListener? = null
        fun qrCodeImageUrl(url: String): Builder {
            qrCodeImageUrl = url
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }
        fun isWeb(isWeb: Boolean): Builder {
            this.isWeb = isWeb
            return this
        }

        fun onDownLoadListener(onDownLoadListener: OnDownLoadListener): Builder {
            this.onDownLoadListener = onDownLoadListener
            return this
        }

        fun build(context: Context): QrCodeDialog {
            return QrCodeDialog(context, this)
        }
    }

    interface OnDownLoadListener {

        fun onDownLoadImage(url: String)
    }
}