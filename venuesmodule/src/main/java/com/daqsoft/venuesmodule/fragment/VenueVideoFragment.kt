package com.daqsoft.venuesmodule.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.utils.ThumbnilUtits
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.event.UpdatePlayerStateEvent
import com.daqsoft.venuesmodule.databinding.VenueFragVideoBinding
import com.daqsoft.venuesmodule.viewmodel.VenueVideoViewModel
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.ArrayList


/**
 * @Description
 * @ClassName   VenueVideoFragment
 * @Author      luoyi
 * @Time        2020/3/25 11:35
 */
class VenueVideoFragment : BaseFragment<VenueFragVideoBinding, VenueVideoViewModel>() {

    var videoUrl: String? = ""


    companion object {
        const val VIDEO_URL = "videoUrl"
        fun newInstance(videoUrl: String): VenueVideoFragment {
            val bundle = Bundle()
            bundle.putString(VIDEO_URL, videoUrl)
            val venueVideoFragment = VenueVideoFragment()
            venueVideoFragment.arguments = bundle
            return venueVideoFragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.venue_frag_video
    }


    override fun injectVm(): Class<VenueVideoViewModel> {
        return VenueVideoViewModel::class.java
    }

    override fun initView() {
        getParams()
        EventBus.getDefault().register(this)
        mBinding.jcVideo.backButton.visibility = View.GONE
        if (!videoUrl.isNullOrEmpty()) {
            mBinding.jcVideo.setUp(StringUtil.enCodeVideoUrl(videoUrl!!), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
//            initVideThumb(videoUrl!!)
            Glide.with(this)
                .load(StringUtil.getVideoCoverUrl(videoUrl!!))
                .into(mBinding.jcVideo.thumbImageView);
            var layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.dip2px(context!!, 40f).toInt()
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.bottomMargin = context!!.resources.getDimensionPixelSize(R.dimen.dp_27)
            mBinding.jcVideo.layoutBottom.layoutParams = layoutParams
        }
    }

    private fun getParams() {
        try {
            videoUrl = arguments?.getString(VIDEO_URL)
        } catch (e: Exception) {
        }

    }

    private fun initVideThumb(videoUrl: String) {
        val observable = Observable.create<Bitmap> { emitter ->
            val bitmap = ThumbnilUtits.getThumbnil(videoUrl, 1, MediaStore.Images.Thumbnails.MINI_KIND)
            try {
                emitter.onNext(bitmap)
            } catch (e: Exception) {

            }

        }
        val consumer = Consumer<Bitmap?> { bit ->
            if (bit != null) {
                Glide.with(this)
                    .load(bit)
                    .into(mBinding.jcVideo.thumbImageView);
            } else {
            }
        }
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
    }

    override fun initData() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePlayerEvent(event: UpdatePlayerStateEvent) {
        when (event.state) {
            2 -> {
                // 暂停
                mBinding.jcVideo.pasuePlayer()
            }
        }
    }
}