package com.daqsoft.baselib.utils.file

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.OssUtil
import com.daqsoft.baselib.utils.StringUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * @Description
 * @ClassName   DownLoadFileUtil
 * @Author      luoyi
 * @Time        2020/3/25 18:17
 */
object DownLoadFileUtil {

    fun downNetworkImage(url: String?, ondownImage: DownImageListener?) {
        Glide.with(BaseApplication.context)
            .asBitmap()
            .load(url)
            .into(
                object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            var bitmap: Bitmap? = resource
                            saveBitmapToDisk(bitmap, ondownImage)
                        } catch (e: Exception) {
                            throw NullPointerException()
                        }

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        throw NetworkErrorException()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
    }

    /**
     * 异步下载网络图片
     */
    fun downNetworkImage(url: String?, width: Int, height: Int) {
        if (url.isNullOrEmpty()) {
            return
        }
        Thread(Runnable {
            Glide.with(BaseApplication.context)
                .load(OssUtil.getImageUrlWatermark(url, width, height))
                .downloadOnly(width, height)
        }).start()
    }

    /**
     * @date 创建时间 2017/11/23 11:01
     * @author ly
     * @describe 保存图片
     * @version versionCode 2017/11/23
     */
    fun saveBitmapToDisk(bitmap: Bitmap?, ondownImage: DownImageListener?) {
        if (bitmap != null) {

            val path = Environment.getExternalStorageDirectory().absolutePath + if (BaseApplication.appArea == "sc") {
                "/zytfapp/"
            } else {
                "/wlapp"
            }
            val temp = File(path) //要保存文件先创建文件夹
            if (!temp.exists()) {
                temp.mkdir()
            }
            ////重复保存时，覆盖原同名图片
            val random = Random()
            val imageName = random.nextInt(100000).toString()
            val file = File("$path$imageName.jpg") //将要保存图片的路径和图片名称
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            ondownImage?.onDownLoadImageSuccess()
            galleryAddPic(file.absolutePath)
        } else {
            throw NullPointerException()
        }
    }

    private fun galleryAddPic(picFileFullName: String) {
        val mediaScanIntent = Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
        )
        val f = File(picFileFullName)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        BaseApplication.context.sendBroadcast(mediaScanIntent)
    }

    interface DownImageListener {
        fun onDownLoadImageSuccess()
    }
}