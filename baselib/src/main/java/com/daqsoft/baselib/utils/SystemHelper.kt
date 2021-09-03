package com.daqsoft.baselib.utils

import android.app.UiModeManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import com.daqsoft.baselib.base.BaseApplication


/**
 * @Description
 * @ClassName   SystemHelper
 * @Author      luoyi
 * @Time        2020/3/21 9:49
 */
object SystemHelper {

    /**
     * 拨打电话
     * @param phoneNum  电话号码
     */
    fun callPhone(context: Context, phoneNum: String) {
        val uiModeManager = context.getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            ToastUtils.showMessage("非常抱歉，暂不支持TV系统拨打电话")
        } else {
            if (!phoneNum.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                val data: Uri = Uri.parse("tel:$phoneNum");
                intent.data = data;
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                BaseApplication.context.startActivity(intent);
            }
        }
    }

    /**
     * 复制内容到剪贴板
     * @param content 复制内容
     */
    fun copyContentToClip(content: String) {
        //复制链接
        val mClipboardManager = BaseApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newRawUri("Label", Uri.parse(content))
        mClipboardManager.primaryClip = mClipData
    }
}