package com.daqsoft.slowLiveModule.bean

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.slowLiveModule.R

internal fun String.getRealImages(): String {
    if (!this.isNullOrEmpty()) {

        if (!this.contains(","))
            return this

        val imgList = this.split(",")
        if (imgList.isNotEmpty()) {
            return imgList[0]
        }
    }
    return ""
}


internal fun ImageView.loadWithDefault(url:String){
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder_img_fail_h158)
        .into(this)
}

/**
 * 获取对应位置的图片
 */
internal fun String.getpositionImages(index :Int=0): String {
    if (!this.isNullOrEmpty()) {
        if (!this.contains(","))
            return this

        val imgList = this.split(",")
        if (imgList.isNotEmpty()) {
            if(imgList.size>index){
                return imgList[index]
            }else{
                return imgList[0]
            }

        }
    }
    return ""
}
