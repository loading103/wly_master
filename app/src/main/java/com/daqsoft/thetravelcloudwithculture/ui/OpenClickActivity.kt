package com.daqsoft.thetravelcloudwithculture.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MyNotificationExtra
import com.daqsoft.thetravelcloudwithculture.jpush.NotifyMessageOpenHelper
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber


class OpenClickActivity : AppCompatActivity() {

    companion object {
        /**消息Id */
        private const val KEY_MSGID = "msg_id"

        /**该通知的下发通道
         * 0为极光，1为小米，2为华为，3为魅族，4为oppo，5为vivo，8为FCM。*/
        private const val KEY_WHICH_PUSH_SDK = "rom_type"

        /**通知标题 */
        private const val KEY_TITLE = "n_title"

        /**通知内容 */
        private const val KEY_CONTENT = "n_content"

        /**通知附加字段 */
        private const val KEY_EXTRAS = "n_extras"

        /**jpush信息 */
        private const val J_MESSAGE_EXTRA = "JMessageExtra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOpenClick()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("onNewIntent")
    }

    /**
     * 处理点击
     * {"n_extras":{},"n_title":"111","n_content":"222","msg_id":67554204573485945,"rom_type":1,"_j_data_":"{\"data_msgtype\":1,\"push_type\":4,\"is_vip\":0}"}

     */
    private fun handleOpenClick() {
        Timber.e("用户点击打开了通知")
        var data: String? = null
        //获取华为平台附带的jpush信息
        if (intent.data != null) {
            data = intent.data.toString()
        }
        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(data) && intent.extras != null) {
            data = intent.extras!!.getString(J_MESSAGE_EXTRA)
        }

        if (data.isNullOrBlank()){
            ARouter
                .getInstance()
                .build( ARouterPath.MainModule.MAIN_ACTIVITY)
                .navigation()
            finish()
            return
        }

        val jsonObject = JSONObject(data)
        Timber.e("jsonObject ${jsonObject}")
        val msgId = jsonObject.optString(KEY_MSGID)
        val whichPushSDK = jsonObject.optInt(KEY_WHICH_PUSH_SDK).toByte()
        val title = jsonObject.optString(KEY_TITLE)
        val content = jsonObject.optString(KEY_CONTENT)
        val extras = jsonObject.optString(KEY_EXTRAS)
        // 点击上报
        JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK)
        val extra = Gson().fromJson(extras, MyNotificationExtra::class.java)
        val bundle = Bundle()
        bundle.putParcelable("notifyExtra",extra)

        Timber.e("initParam1---------"+Gson().toJson(extra))

        if (AppUtils.isExistActivity(this, MainActivity::class.java)){
            NotifyMessageOpenHelper.pageJump(extra)
            finish()
            return
        }

        ARouter
            .getInstance()
            .build(ARouterPath.MainModule.MAIN_ACTIVITY)
            .withBundle("notifyBundle",bundle)
            .navigation()
        finish()
    }

}