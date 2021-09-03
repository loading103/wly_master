package com.daqsoft.slowLiveModule.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.utils.ThumbnilUtits
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.databinding.SlowLiveAtyVideoBinding
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerDqUI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.slow_live_aty_video.*
import java.lang.Exception

/**
 * @Description
 * @ClassName   VideoActivity
 * @Author      Wongxd
 * @Time        2020/4/16 15:10
 */
internal class VideoActivity : TitleBarActivity<SlowLiveAtyVideoBinding, BaseViewModel>() {

    companion object {

        private var videoUrl = ""
        private var mImgUrl = ""

        fun playUrl(ctx: Context?, url: String, imgUrl: String) {
            videoUrl = url
            mImgUrl = imgUrl
            ctx?.apply {
                startActivity(Intent(this, VideoActivity::class.java))
            }
        }
    }

    private lateinit var jcVideo: JCVideoPlayerDqUI


    override fun initView() {
        jcVideo = jc_video
        jcVideo.backButton.visibility = View.GONE
        if (!videoUrl.isNullOrEmpty()) {
            jcVideo.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
            initVideoThumb(videoUrl)
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.dip2px(this, 40f).toInt()
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.dp_27)
            jcVideo.layoutBottom.layoutParams = layoutParams
        }
    }

    @SuppressLint("CheckResult")
    private fun initVideoThumb(videoUrl: String) {

//        val observable = Observable.create<Bitmap> { emitter ->
//            val bitmap = ThumbnilUtits.getThumbnil(videoUrl, 1, MediaStore.Images.Thumbnails.MINI_KIND)
//            try {
//                emitter.onNext(bitmap)
//            } catch (e: Exception) {
//
//            }
//
//        }
//
//
//        val consumer = Consumer<Bitmap?> { bit ->
//            if (bit != null) {
//                Glide.with(this)
//                    .load(bit)
//                    .into(jcVideo.thumbImageView);
//            }
//        }

//        observable.subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(consumer)


        Glide.with(this)
            .load(mImgUrl)
            .into(jcVideo.thumbImageView);
    }

    override fun getLayout(): Int = R.layout.slow_live_aty_video

    override fun setTitle(): String = getString(R.string.slow_live_video)

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initData() {

    }


    override fun onStop() {
        super.onStop()
        try {
            jcVideo.release()
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            jcVideo.release()
        } catch (e: Exception) {
        }
    }


}


