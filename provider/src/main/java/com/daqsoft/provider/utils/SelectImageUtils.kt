package com.daqsoft.provider.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ProviderPopupPictureSelectBinding
import com.daqsoft.provider.event.UpdateDissmissEvent
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

/**
 *@package:com.daqsoft.provider.utils
 *@date:2020/6/15 17:30
 *@author: caihj
 *@des:TODO
 **/
object SelectImageUtils {
    /**
     * 初始化头像选择的弹出框
     * @param activity 当前Activity
     * @param requestCameraCode 跳转相机请求码
     * @param requestAlbumCode 跳转相册请求码
     */
    var isshow:Boolean=false
    fun initPictureSelectPop(activity: FragmentActivity, requestCameraCode: Int, requestAlbumCode: Int):
            PopupWindow {
        val popUpWindow = Utils.initPopupWindow(
            activity,
            0.8f,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

//        val contentView = LayoutInflater.from(activity).inflate(R.layout.provider_popup_picture_select, null)
        val bingding = DataBindingUtil.inflate<ProviderPopupPictureSelectBinding>( LayoutInflater.from(activity), R.layout.provider_popup_picture_select, null, false)


        // 相机
        bingding.mPhotoCameraTv.setOnClickListener {
            isshow=true
            Handler().postDelayed({isshow=false},300)
            popUpWindow.dismiss()
            RxPermissions(activity)
                .request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        openCamera(activity, requestCameraCode)
                    } else {
                        Toast.makeText(activity, "相机不可用", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        // 相册
        bingding.mPhotoAlbumTv.setOnClickListener {
            isshow=true
            Handler().postDelayed({isshow=false},300)
            popUpWindow.dismiss()
            openAlbum(activity, requestAlbumCode)
        }
        popUpWindow.contentView = bingding.root
        popUpWindow.animationStyle = R.style.AnimBottom
        popUpWindow.setOnDismissListener {
            Utils.setWindowBackGroud(activity, 1f)
            if(!isshow){
                EventBus.getDefault().post(UpdateDissmissEvent())
            }

        }
        return popUpWindow
    }

    /**
     * 打开相机
     * @param activity 当前Activity
     * @param requestCode 请求码
     */
    fun openCamera(activity: Activity, requestCode: Int) {
        // 跳转到系统照相机
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity.packageManager) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            val mTmpFile = createFile(activity.applicationContext) ?: return
            val contentUri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", mTmpFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            // 授予目录临时共享权限
            cameraIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            activity.startActivityForResult(cameraIntent, requestCode)
        } else {
            Toast.makeText(activity,"相机不可用", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 打开相册
     * @param activity 当前Activity
     * @param requestCode 请求码
     */
    fun openAlbum(activity: Activity, requestCode: Int) {
        val albumIntent = Intent(Intent.ACTION_PICK, null)
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        activity.startActivityForResult(albumIntent, requestCode)
    }

    /**
     * 相机拍照返回的图片资源
     */
    var file: File? = null

    /**
     * 获取拍照相片存储文件
     * @param context 当前上下文
     */
    fun createFile(context: Context): File? {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val timeStamp = "${Date().time}"
            val imagePath = File(context.filesDir, "images")
            if (!imagePath.exists()) {
                imagePath.mkdirs()
            }
            file = File(imagePath, "$timeStamp.jpg")
        } else {
            val cacheDir = context.cacheDir
            val timeStamp = "${Date().time}"
            file = File(cacheDir, "$timeStamp.jpg")
        }
        return file
    }

    /**
     * 得到从相册中选中返回的文件
     * @param activity 当前Activity
     * @param data onActivityResult中的data
     */
    fun getFileFromAlbum(activity: Activity, data: Intent?): String? {
        val selectedImage = data?.data
        val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
        if (selectedImage == null) {
            return null
        }
        val cursor = activity.contentResolver.query(
            selectedImage,
            filePathColumns,
            null,
            null,
            null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumns[0])
        val filePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return filePath
    }

    /**
     * 获取相机返回的图片地址
     */
    fun getFileFromCamera(): File? = file

    /**
     * 相机地址置空
     */
    fun clearFile() {
        file = null
    }

    /**
     * 上传图片（只能单张上传）
     */
    fun uploadImg() {

    }
}