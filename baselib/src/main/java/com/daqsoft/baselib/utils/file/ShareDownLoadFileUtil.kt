package com.daqsoft.baselib.utils.file

import android.accounts.NetworkErrorException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseApplication
import java.io.*

/**
 * @Description
 * @ClassName   友盟分享大型图片处理
 */
object ShareDownLoadFileUtil {
    val path = Environment.getExternalStorageDirectory().absolutePath +  "/appshare/"
    var path1 = ""
    fun downNetworkImage(url: String?, ondownImage: DownImageListener?) {
        clearBitmap()
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

    fun saveBitmapToDisk(bitmap: Bitmap?, ondownImage: DownImageListener?) {
        if (bitmap != null) {
            val temp = File(path) //要保存文件先创建文件夹
            if (!temp.exists()) {
                temp.mkdir()
            }
            ////重复保存时，覆盖原同名图片
            val imageName ="share_picture"

            path1="$path$imageName.jpg"
            val file = File(path1) //将要保存图片的路径和图片名称
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            ondownImage?.onDownLoadImageSuccess()
        } else {
            throw NullPointerException()
        }
    }

    interface DownImageListener {
        fun onDownLoadImageSuccess()
    }


    /**
     * 获取下载到本地的图片的bitmap
     * @return
     */
    fun getBitmap(): Bitmap? {
        return try {
            val fis = FileInputStream(path1)
            BitmapFactory.decodeStream(fis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 清除下载的本地分享图片
     */
    fun clearBitmap() {
        val filePic = File(path)
        if (filePic.exists()) {
            filePic.delete()
        }
    }
}