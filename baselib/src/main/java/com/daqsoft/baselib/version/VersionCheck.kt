package com.daqsoft.baselib.version

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.repository.HttpRepository
import com.daqsoft.baselib.version.service.UpdateService
import daqsoft.com.baselib.R
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 版本检测服务
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/10/17 0017
 * @since JDK 1.8
 */
object VersionCheck {

    /**
     * 弹框需要的权限
     */
    private val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    /**
     * 检查应用版本
     */
    private var localVersion = ""

    private var dialog: AlertDialog? = null

    fun checkUpdate(context: Activity, url: String, appId: String) {
        try {
            val packageInfo =
                context.applicationContext.packageManager.getPackageInfo(context.packageName, 0)
            localVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val httpRepository = HttpRepository()
        httpRepository.checkVersion(
            url,
            appId,
            "AppVersion",
            "daqsoft",
            "1",
            localVersion
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val result = response.body()!!.string()
                    if (TextUtils.isEmpty(result) || "3" == result || "2" == result) {
//                        Toast.makeText(context, "新版本检测失败!", Toast.LENGTH_LONG).show()
                    } else if ("0" == result) {
//                        Toast.makeText(context, "已是最新版本！", Toast.LENGTH_LONG).show()
                    } else {
                        val jnewdata = JSONObject(result)
                        if (jnewdata.get("IsUpdate").toString() == "1") {
                            alertUserDown2(
                                context, (jnewdata.get("AppUpdateInfo")).toString() + "",
                                jnewdata.get("DownPath").toString().trim { it <= ' ' },
                                jnewdata.getString("VersionCode") + ""
                            )
                        } else {
//                            Toast.makeText(context, "已是最新版本！", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                    Toast.makeText(context, "新版本检测失败!", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Toast.makeText(context, "新版本检测失败!", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * 弹出确认框
     *
     * @param context
     * @param text
     * @param url
     */
    fun alertUserDown2(
        context: Activity, text: String, url: String,
        version: String
    ) {
        dialog =
            AlertDialog.Builder(AppManager.instance.currentActivity(), R.style.LightDialog).create()
        dialog!!.show()
        val window = dialog!!.window
        window!!.setContentView(R.layout.include_version_window)
        val tvContent = window.findViewById(R.id.version_content) as TextView
        tvContent.text = "发现新版本v$version，建议更新"
        val tvVerionLogo = window.findViewById<TextView>(R.id.version_content_logo)
        if (!text.isNullOrEmpty()) {
            tvVerionLogo.text = text
        } else {
            tvVerionLogo.visibility = View.GONE
        }


        val btnCancel = window.findViewById(R.id.version_cancel) as TextView
        val btnSure = window.findViewById(R.id.version_sure) as TextView
        var vLine: View = window.findViewById(R.id.v_version_line)
        if (BaseApplication.isMustUpdate) {
            btnCancel.visibility = View.GONE
            vLine.visibility = View.GONE
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
        } else {
            btnCancel.visibility = View.VISIBLE
            vLine.visibility = View.VISIBLE
        }
        btnCancel.setOnClickListener { dialog?.dismiss() }
        btnSure.setOnClickListener {
            // 开启更新服务UpdateService
            // 这里为了把update更好模块化，可以传一些updateService依赖的值
            // 如布局ID，资源ID，动态获取的标题,这里以app_name为例
            checkUpdatePermission(context, url)

        }
    }

    /**
     * 检查版本更新的权限
     *
     * @param activity
     */
    fun checkUpdatePermission(activity: Activity, url: String) {
        var isGranted = false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isGranted = activity.packageManager.canRequestPackageInstalls()
        } else {
            isGranted = true
        }
        if (isGranted) {
            // 版本检测
            val updateIntent = Intent(context, UpdateService::class.java)
            updateIntent.putExtra("app_name", context.resources.getString(R.string.app_name))
            updateIntent.putExtra("updatepath", url)
            context.startService(updateIntent)
            dialog?.dismiss()
        } else {
            AlertDialog.Builder(activity).setCancelable(false).setTitle(
                "安装应用需要打开未知来源权限，请去设置中开启权限"
            ).setPositiveButton(
                "确定"
            ) { d, w ->
                val packageURI = Uri.parse("package:" + activity.packageName)
                // 注意这个是8.0新API
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    packageURI
                )
                activity.startActivityForResult(intent, 10001)
            }.show()
        }
    }

    /**
     * 监测是否有通知
     */
    fun checkNotify(activity: Activity) {
        if (!isNotificationEnabled(activity)) {
            val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
            dialog.show()
            val window = dialog.window
            window!!.setContentView(R.layout.include_version_window)
            val tvContent = window.findViewById(R.id.version_content) as TextView
            tvContent.text = "检测到您没有打开通知权限，是否去打开?"
            val btnCancel = window.findViewById(R.id.version_cancel) as TextView
            val btnSure = window.findViewById(R.id.version_sure) as TextView
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnSure.setOnClickListener {
                val localIntent = Intent()
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.action = "android.settings" + "" +
                            ".APPLICATION_DETAILS_SETTINGS"
                    localIntent.data =
                        Uri.fromParts("package", activity.packageName, null)
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.action = Intent.ACTION_VIEW
                    localIntent.setClassName(
                        "com.android.settings",
                        "com.android" + "" + ".settings.InstalledAppDetails"
                    )
                    localIntent.putExtra(
                        "com.android.settings" + ".ApplicationPkgName", activity.packageName
                    )
                }
                activity.startActivity(localIntent)
                dialog.dismiss()
            }
        }

    }


    @SuppressLint("NewApi")
    fun isNotificationEnabled(context: Context): Boolean {
        val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        /* Context.APP_OPS_MANAGER */
        var appOpsClass: Class<*>? = null
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass!!.getMethod(
                CHECK_OP_NO_THROW, Integer.TYPE,
                Integer.TYPE, String::class.java
            )
            val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue.get(Int::class.java) as Int
            return checkOpNoThrowMethod.invoke(
                mAppOps,
                value,
                uid,
                pkg
            ) as Int == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
