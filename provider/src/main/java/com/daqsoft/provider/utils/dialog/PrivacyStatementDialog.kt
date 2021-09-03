package com.daqsoft.provider.utils.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.event.UndateFinishEvent
import kotlinx.android.synthetic.main.layout_privacy_statement_dialog.*
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   PrivacyStatementDialog
 * @Author      luoyi
 * @Time        2020/5/15 13:58
 */
class PrivacyStatementDialog : AlertDialog {
    var mContext: Context? = null


    val private_xj: String="http://project.daqsoft.com/privacy/yxj.html"
    val private_ws: String="http://project.daqsoft.com/privacy/xj.html"
    val private_sc: String="http://project.daqsoft.com/privacy/zytf.html"
    constructor(context: Context?) : super(context, R.style.PrivacyStatementDialog) {
        mContext = context
        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_privacy_statement_dialog)
        initView()
        val dialogWindow: Window = this.window
        dialogWindow.setWindowAnimations(R.style.dialog_zoom)
    }


    private fun initView() {
        tv_confirm.onNoDoubleClick {
            dismiss()
            SPUtils.getInstance().put(SPUtils.Config.APP_IS_FIRST_LOAD, 1)

        }
        tv_confirm_no.onNoDoubleClick {
            dismiss()
            EventBus.getDefault().post(UndateFinishEvent())
        }
        var txtStatement = "您可以查看完整版"
        var txtUserMent = "用户协议"
        var txtPrivacy = "隐私政策"
        var spannaerStr = SpannableStringBuilder(txtStatement + txtUserMent + "及" + txtPrivacy)
        var start = txtStatement.length
        var end1 = txtStatement.length + txtUserMent.length
        spannaerStr.setSpan(
            ForegroundColorSpan(mContext!!.resources.getColor(R.color.colorPrimary)), start, end1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 用户协议
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "用户协议")
                    .withString("html",  BaseApplication.webSiteUrl+"user-publishing-protocol")
                    .navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        spannaerStr.setSpan(
            clickableSpan, txtStatement.length, end1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        var start2 = end1 + 1
        spannaerStr.setSpan(
            ForegroundColorSpan(mContext!!.resources.getColor(R.color.colorPrimary)), start2, start2 + txtPrivacy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 隐私协议
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                if (BaseApplication.appArea == "sc") {
                    goWebView(private_sc,"隐私声明")
                }else if (BaseApplication.appArea == "ws") {
                    goWebView(private_ws,"隐私声明")
                } else {
                    goWebView(private_xj,"隐私声明")
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        spannaerStr.setSpan(
            clickableSpan2, start2, start2 + txtPrivacy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_privacy_statement_info.text = spannaerStr
        tv_privacy_statement_info.movementMethod = LinkMovementMethod.getInstance();
    }

    private fun init() {
        setCancelable(false)
    }

    override fun show() {
        try {
            super.show()
        } catch (e: Exception) {
        }
    }


    private fun goWebView(url :String ,title :String ) {
        ARouter
            .getInstance()
            .build(ARouterPath.Provider.WEB_ACTIVITY)
            .withString("mTitle", title)
            .withString("html", url)
            .withString("isscar", "true")
            .navigation()
    }
}