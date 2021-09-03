package com.daqsoft.volunteer.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.lang.Exception
import java.util.*

/**
 *@package:com.daqsoft.volunteer.utils
 *@date:2020/6/30 10:24
 *@author: caihj
 *@des:二维码处理类
 **/
object QRCodeUtils {

    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    fun createQRCodeBitmap(content:String,
                           width:Int,
                           height:Int,
                           character_set:String = "UTF-8",
                           error_correction_level:String = "H",
                           margin:String = "1",
                           color_black:Int = Color.BLACK,
                           color_white:Int = Color.WHITE):Bitmap?{
        if(TextUtils.isEmpty(content)){
            return null
        }
        if(width < 0 || height < 0){
            return null
        }
        try {
            val hints = Hashtable<EncodeHintType,String>()
            if(character_set.isNotEmpty()){
                hints[EncodeHintType.CHARACTER_SET] = character_set
            }
            if(error_correction_level.isNotEmpty()){
                hints[EncodeHintType.ERROR_CORRECTION] = error_correction_level
            }
            if(margin.isNotEmpty()){
                hints[EncodeHintType.MARGIN] = margin
            }

            val bitMatrix = QRCodeWriter().encode(content,BarcodeFormat.QR_CODE,width,height,hints)
            val pixels = IntArray(width * height)
            for (y in 0 until height){
                for (x in 0 until width){
                    if(bitMatrix.get(x,y)){
                        pixels[y * width + x] = color_black
                    }else{
                        pixels[y * width + x] = color_white
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels,0,width,0,0,width,height)
            return bitmap
        }catch (e:Exception){
            return null
        }
    }
}