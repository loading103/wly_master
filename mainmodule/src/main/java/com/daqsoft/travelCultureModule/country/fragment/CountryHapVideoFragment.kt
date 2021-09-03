package com.daqsoft.travelCultureModule.country.fragment

import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.CountryHapFragVideoBinding
import com.daqsoft.provider.utils.ThumbnilUtits
import com.daqsoft.travelCultureModule.country.model.CountryHapVideoViewModel
import com.daqsoft.travelCultureModule.country.view.UpdatePlayerStateEvent
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

/**
 * desc :乡村游视频fragment
 * @author 江云仙
 * @date 2020/4/15 16:48
 */
class CountryHapVideoFragment : BaseFragment<CountryHapFragVideoBinding, CountryHapVideoViewModel> {

    var videoUrl: String = ""

    constructor(url: String) {
        this.videoUrl = url
    }

    override fun getLayout(): Int {
        return R.layout.country_hap_frag_video
    }


    override fun injectVm(): Class<CountryHapVideoViewModel> {
        return CountryHapVideoViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.jcVideo.backButton.visibility = View.GONE
        if (!videoUrl.isNullOrEmpty()) {
            mBinding.jcVideo.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
//            initVideThumb(videoUrl)
            var layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.dip2px(context!!, 40f).toInt()
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.bottomMargin = context!!.resources.getDimensionPixelSize(R.dimen.dp_27)
            mBinding.jcVideo.layoutBottom.layoutParams = layoutParams
        }
    }

    private fun initVideThumb(videoUrl: String) {
        val observable = Observable.create<Bitmap> { emitter ->
            val bitmap =ThumbnilUtits.getThumbnil(videoUrl, 1, MediaStore.Images.Thumbnails.MINI_KIND)
            try {
                emitter.onNext(bitmap)
            }catch (e:Exception){

            }

        }
        val consumer = Consumer<Bitmap?> { bit ->
            if(bit!=null){
                Glide.with(this)
                    .load(bit)
                    .into(mBinding.jcVideo.thumbImageView);
            }else{
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