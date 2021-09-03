package com.daqsoft.usermodule.uitls

import android.content.Context
import com.daqsoft.baselib.utils.ToastUtils

/**
 * 工具类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-19
 * @since JDK 1.8.0_191
 */
object Utils {
    /**
     * 姓名脱敏
     */
    fun nameInvisible(name: String?): String {
        if (name?.length ?: 0 < 2) {
            if (!name.isNullOrEmpty()) {
                return name
            }
            return ""
        } else {
            return name?.replaceRange(0, 1, "*") ?: ""
        }
    }

    fun phoneInvisible(phone: String?): String {
        if (phone.isNullOrEmpty()) {
            return ""
        }
        if (phone.length == 11) {
            return phone.replaceRange(3, 6, "*") ?: ""
        }
        return phone
    }

    /**
     * 身份证信息脱敏
     */
    fun IDNumberInvisible(num: String?): String {
        if (num.isNullOrEmpty()) {
            return ""
        }
        var number: String = num.trimEnd()
        if (num.length > 18) {
            number = num.substring(0, 18)
        }
        if (RegexUtils.isIDCard15(number)) {
            return number?.replaceRange(8, 11, "******") ?: ""
        } else if (RegexUtils.isIDCard18(number)) {
            return number?.replaceRange(8, 14, "**********") ?: ""
        } else {
            return ""
        }
    }

    fun copy(content: String, context: Context): Boolean {
        // 得到剪贴板管理器
        try {
            val n1 = content.trim();
            val sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = n1
            } else {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip_data = android.content.ClipData.newPlainText("TSMS", n1)
                clipboard.primaryClip = clip_data;
            }
            return true;
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return false
    }

}