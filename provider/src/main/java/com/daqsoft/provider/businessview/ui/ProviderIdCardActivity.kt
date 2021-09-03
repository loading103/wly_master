package com.daqsoft.provider.businessview.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.UpdateUiDataEvent
import kotlinx.android.synthetic.main.activity_id_card_camera.*
import org.greenrobot.eventbus.EventBus
import java.io.*


/**
 * @Description
 * @ClassName   ProviderIdCardActivity
 * @Author      luoyi
 * @Time        2020/8/5 14:58
 */
@Route(path = ARouterPath.Provider.PROVIDER_PHOTO_IDCARD_ACTIVITY)
class ProviderIdCardActivity : Activity(), View.OnClickListener {

    var type: Int? = TYPE_IDCARD_FRONT

    companion object {
        /**
         * 拍摄类型-身份证正面
         */
        const val TYPE_IDCARD_FRONT = 1
        /**
         * 拍摄类型-身份证反面
         */
        const val TYPE_IDCARD_BACK = 2

        const val REQUEST_CODE = 0x13

        const val RESULT_CODE = 0x14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_id_card_camera)
        initView()
    }

    private fun initView() {
        setCameraPreView()
    }

    private fun setCameraPreView() {
        camera_surface.setOnClickListener(this)
        camera_flash.setOnClickListener(this)
        camera_close.setOnClickListener(this)
        camera_take.setOnClickListener(this)
        camera_result_ok.setOnClickListener(this)
        camera_result_cancel.setOnClickListener(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.camera_surface -> {
                camera_surface.focus()
            }
            R.id.camera_close -> {
                finish()
            }
            R.id.camera_take -> {
                takePhoto()
            }
            R.id.camera_flash -> {
                var isFlash = camera_surface.switchFlashLight()
                camera_flash.setImageResource(
                    if (isFlash) {
                        R.mipmap.camera_flash_on
                    } else {
                        R.mipmap.camera_flash_off
                    }
                )
            }
            R.id.camera_result_ok -> {
                goBack()
            }
            R.id.camera_result_cancel -> {
                camera_option.visibility = View.VISIBLE
                camera_surface.isEnabled = true
                camera_result.visibility = View.GONE
                camera_surface.startPreview()

            }
        }
    }


    private fun takePhoto() {
        camera_option.visibility = View.GONE
        camera_surface.isEnabled = false
        camera_surface.takePhoto(object : Camera.PictureCallback {
            override fun onPictureTaken(data: ByteArray?, camera: Camera) {
                camera.stopPreview()
                        //子线程处理图片，防止ANR
                        Thread(Runnable {
                            try {
                        val originalFile: File = getOriginalFile()
                        val originalFileOutputStream = FileOutputStream(originalFile)
                        originalFileOutputStream.write(data)
                        originalFileOutputStream.close()
                        var matrix =Matrix()
                        matrix.postRotate(readPictureDegree(originalFile.absolutePath).toFloat())
                        val bitmap: Bitmap = BitmapFactory.decodeFile(originalFile.path)
                        val rotionBit =Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        //计算裁剪位置
                        val left: Float =
                            camera_crop_container.left.toFloat() / camera_surface.width.toFloat()
                        val top: Float =
                            (camera_crop_container.top - camera_surface.top).toFloat() / camera_surface.height.toFloat()
                        val right: Float =
                            camera_crop_container.right.toFloat() / camera_surface.width.toFloat()
                        val bottom: Float =
                            camera_crop_container.bottom.toFloat() / camera_surface.height.toFloat()
                        //裁剪及保存到文件
                        val cropBitmap: Bitmap = Bitmap.createBitmap(
                            rotionBit,
                            (left * rotionBit.width).toInt(),
                            (top * rotionBit.height).toInt(),
                            ((right - left) * rotionBit.width).toInt(),
                            ((bottom - top) *rotionBit.height).toInt()
                        )
                        val cropFile: File = getCropFile()
                        val bos = BufferedOutputStream(FileOutputStream(cropFile))
                        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        bos.flush()
                        bos.close()
                        runOnUiThread { camera_result.visibility = View.VISIBLE }
                        return@Runnable
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    runOnUiThread {
                        camera_option.visibility = View.VISIBLE
                        camera_surface.isEnabled = true
                    }
                }).start()
            }
        })
    }

    /**
     * @return 拍摄图片原始文件
     */
    private fun getOriginalFile(): File {
        when (type) {
            TYPE_IDCARD_FRONT -> return File(externalCacheDir, "idCardFront.jpg")
            TYPE_IDCARD_BACK -> return File(externalCacheDir, "idCardBack.jpg")
        }
        return File(externalCacheDir, "picture.jpg")
    }

    /**
     * @return 拍摄图片裁剪文件
     */
    private fun getCropFile(): File {
        when (type) {
            TYPE_IDCARD_FRONT -> return File(externalCacheDir, "idCardFrontCrop.jpg")
            TYPE_IDCARD_BACK -> return File(externalCacheDir, "idCardBackCrop.jpg")
        }
        return File(externalCacheDir, "pictureCrop.jpg")
    }

    /**
     * 使用拍照结果，返回对应图片路径
     */
    private fun goBack() {
        val intent = Intent()
        intent.putExtra("result", getCropFile().path)
        Log.e("path=", "" + getCropFile().path)
        setResult(RESULT_CODE, intent)
        finish()
    }
    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation: Int = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

