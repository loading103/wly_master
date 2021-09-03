package com.daqsoft.provider

import android.widget.ImageView
import com.bumptech.glide.Glide


internal fun String.getRealImageUrl(): String {
    if (!this.isNullOrEmpty()) {

        if (!this.contains(","))
            return this

        val imgList = this.split(",")
        if (!imgList.isNullOrEmpty()) {
            return imgList[0]
        }
    }
    return ""
}


internal fun ImageView.loadWithDefaultGray(url:String){
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder_img_fail_240_180)
        .into(this)
}


internal fun ImageView.loadWithDefaultGreen(url:String){
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder_img_fail_h158)
        .into(this)
}

