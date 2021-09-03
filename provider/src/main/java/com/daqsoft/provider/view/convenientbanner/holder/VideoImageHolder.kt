package com.daqsoft.provider.view.convenientbanner.holder

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ImageLoadUtil
import com.daqsoft.baselib.utils.OssUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayer
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoView.OnStateChangeListener
import com.dueeeke.videoplayer.player.VideoView.SCREEN_SCALE_DEFAULT
import com.dueeeke.videoplayer.util.L
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   VideoImageHolder
 * @Author      luoyi
 * @Time        2020/3/31 13:49
 */
class VideoImageHolder : Holder<VideoImageBean> {

    public var mVideoPlayer: VideoView<IjkPlayer>? = null
    var mImgeView: ImageView? = null
    var mContext: Context? = null
    var mImgPannorTip: ImageView? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        mVideoPlayer = itemView?.findViewById(R.id.video_player_img)
        mImgeView = itemView?.findViewById(R.id.img_video_img)
        mImgPannorTip = itemView?.findViewById(R.id.img_720_tip)

    }

    override fun updateUI(data: VideoImageBean?) {
        data?.let {
            when (it.type) {
                0 -> {
                    // 图片
                    if (mImgeView != null) {
                        mVideoPlayer?.visibility = View.GONE
                        mImgPannorTip?.visibility = View.GONE
                        mImgeView?.visibility = View.VISIBLE
                        if(it.imageUrl.endsWith(".gif")){
//                            GlideModuleUtil.loadDqImageWaterMark(it.imageUrl, mImgeView!!.context, mImgeView!!)
                           ImageLoadUtil.CommonGifLoadCp(mContext,it.imageUrl,mImgeView!!)

                        }else{
                            GlideModuleUtil.loadDqImageWaterMark(it.imageUrl, mImgeView!!.context, mImgeView!!)
                        }

                    } else {
                    }
                }
                1 -> {
                    // 视频
                    if (mVideoPlayer != null) {
                        mImgeView?.visibility = View.GONE
                        mImgPannorTip?.visibility = View.GONE
                        mVideoPlayer?.visibility = View.VISIBLE
                        val controller = StandardVideoController(mContext!!)
                        val prepareView = PrepareView(mContext!!)
                        // 封面图
                        val thumb = prepareView.findViewById<ImageView>(R.id.thumb)
                        // 更改播放按钮
                        val playButton = prepareView.findViewById<ImageView>(R.id.start_play)
                        playButton.background = mContext!!.resources.getDrawable(R.drawable.dkplayer_custom_shape_play_bg)
                        var coverUrl = ""
                        coverUrl = if (!it.imageUrl.isNullOrEmpty()) {
                            it.imageUrl
                        } else {
                            StringUtil.getVideoCoverUrl(StringUtil.enCodeVideoUrl(it.videoUrl!!))
                        }
                        Glide.with(mContext!!).load(coverUrl).placeholder(R.mipmap.placeholder_img_fail_240_180).error(R.mipmap.placeholder_img_fail_240_180)
                            .into(thumb)
                        // 错误视图
                        val errorView = ErrorView(mContext)
                        prepareView.setClickStart()
                        //播放完成视图
                        val completeView = CompleteView(mContext!!)
                        val titleView = TitleView(mContext!!)
                        // 添加 完成/错误/准备组件
                        controller.addControlComponent(completeView, errorView, prepareView, titleView)
                        controller.addControlComponent(VodControlView(mContext!!))
                        controller.addControlComponent(GestureView(mContext!!))
                        controller.setCanChangePosition(true)
                        mVideoPlayer?.setVideoController(controller)
                        mVideoPlayer?.setUrl(StringUtil.enCodeVideoUrl(it.videoUrl!!.trim()))

                        mVideoPlayer?.setScreenScaleType(SCREEN_SCALE_DEFAULT)
                        // 使用IjkPlayer解码
                        mVideoPlayer?.setPlayerFactory(IjkPlayerFactory.create())
                        // 播放状态监听
                        mVideoPlayer?.addOnStateChangeListener(mOnVideoViewStateChangeListener)
                        mVideoPlayer?.setOnClickListener {
                            mVideoPlayer?.start()
                        }
                    } else {

                    }
                }
                2 -> {
                    mImgPannorTip?.visibility = View.VISIBLE
                    mVideoPlayer?.visibility = View.GONE
                    mImgeView?.visibility = View.VISIBLE
                    // 720
                    mImgeView?.let {
                        if (data.imageUrl.isNullOrEmpty()) {
                            Glide.with(mContext!!)
                                .load(R.mipmap.img_panaro_2_1)
                                .into(mImgeView!!)
                        } else {
                            Glide.with(mContext!!)
                                .asBitmap()
                                .load(OssUtil.getImageUrlWatermark(data.imageUrl))
                                .placeholder(R.mipmap.img_panaro_2_1)
                                .centerCrop()
                                .into(mImgeView!!)
                        }
                    }

                    RxView.clicks(mImgeView!!)
                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                            ARouter.getInstance()
                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("mTitle", data.name)
                                .withString("html", data.videoUrl)
                                .navigation()
                        }
                }
                else -> {
                }
            }
        }
    }

    private val mOnVideoViewStateChangeListener: OnStateChangeListener = object : OnStateChangeListener {
        override fun onPlayerStateChanged(playerState: Int) {
            when (playerState) {
                VideoView.PLAYER_NORMAL -> {
                }
                VideoView.PLAYER_FULL_SCREEN -> {
                }
            }
        }

        override fun onPlayStateChanged(playState: Int) {
            when (playState) {
                VideoView.STATE_IDLE -> {
                }
                VideoView.STATE_PREPARING -> {
                    EventBus.getDefault().post(UpdateAudioStateEvent(1))
                }
                VideoView.STATE_PREPARED -> {
                    EventBus.getDefault().post(UpdateAudioStateEvent(1))
                    // 需在此时获取视频宽高
                    if (mVideoPlayer != null) {
                        val videoSize: IntArray = mVideoPlayer!!.getVideoSize()
                        L.d("视频宽：" + videoSize[0])
                        L.d("视频高：" + videoSize[1])
                    }
                }
                VideoView.STATE_PLAYING -> {
                    EventBus.getDefault().post(UpdateAudioStateEvent(2))
                }
                VideoView.STATE_PAUSED -> {
                    EventBus.getDefault().post(UpdateAudioStateEvent(3))
                }
                VideoView.STATE_BUFFERING -> {
                }
                VideoView.STATE_BUFFERED -> {
                }
                VideoView.STATE_PLAYBACK_COMPLETED -> {
                    EventBus.getDefault().post(UpdateAudioStateEvent(3))
                }
                VideoView.STATE_ERROR -> {

                }
            }
        }
    }

    fun release() {
        if(mVideoPlayer!=null){
            mVideoPlayer?.release()
        }
    }
    private fun initVideThumb(videoUrl: String, imageView: ImageView) {
//            val observable = Observable.create<Bitmap> { emitter ->
//                val bitmap = ThumbnilUtits.getThumbnil(videoUrl, 1, MediaStore.Images.Thumbnails.MINI_KIND)
//                try {
//                    emitter.onNext(bitmap)
//                } catch (e: Exception) {
//
//                }
//
//            }
//            val consumer = Consumer<Bitmap?> { bit ->
//                if (bit != null) {
//                    Glide.with(imageView.context)
//                        .load(bit)
//                        .into(imageView);
//                } else {
//                }
//            }
//            observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(consumer)
//        }
    }
}