package com.daqsoft.slowLiveModule.bean

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.OssUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.slowLiveModule.liveDetail.SlowLiveDetailActivity
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayer
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoView.*
import com.dueeeke.videoplayer.util.L
import com.flyco.roundview.RoundLinearLayout
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerDqUI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   VideoImageHolder
 * @Author      luoyi
 * @Time        2020/3/31 13:49
 */
class VideoZbImageHolder : Holder<VideoImageBean> {

    public var mVideoPlayer: VideoView<IjkPlayer>? = null
    var mImgeView: ArcImageView? = null
    var mContext: Context? = null
    var mImgPannorTip: ImageView? = null
    var ll_root: RoundLinearLayout? = null
    var iv_live: ImageView? = null
    var tv_state: TextView? = null

    var rl_zb: RelativeLayout? = null
    var jc_video: JCVideoPlayerDqUI? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        mVideoPlayer = itemView?.findViewById(R.id.video_player_img)
        mImgeView = itemView?.findViewById(R.id.img_video_img)
        mImgPannorTip = itemView?.findViewById(R.id.img_720_tip)
        ll_root = itemView?.findViewById(R.id.ll_root)
        tv_state = itemView?.findViewById(R.id.tv_state)
        iv_live = itemView?.findViewById(R.id.iv_live)
        rl_zb = itemView?.findViewById(R.id.rl_zb)
        jc_video = itemView?.findViewById(R.id.jc_video)

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
                        GlideModuleUtil.loadDqImageWaterMark(it.imageUrl, mImgeView!!.context, mImgeView!!)
                    } else {
                    }
                }
                1 -> {
                    // 是直播
                    showVideoState(it)
                    if(it.VideoType=="1"){
                        mVideoPlayer?.visibility= View.GONE
                        mImgeView?.visibility = View.GONE
                        mImgPannorTip?.visibility = View.GONE
                        jc_video?.visibility=View.VISIBLE
                        rl_zb?.visibility=View.VISIBLE
                        playZbVideo(it)
                    } else if(it.VideoType=="2") {
                        mVideoPlayer?.visibility= View.VISIBLE
                        mImgeView?.visibility = View.GONE
                        mImgPannorTip?.visibility = View.GONE
                        jc_video?.visibility=View.GONE
                        rl_zb?.visibility=View.GONE
                        // 视频
                        if (mVideoPlayer != null) {
                            playVideo(it)
                        } else {}
                    }else {
                        mVideoPlayer?.visibility= View.GONE
                        mImgeView?.visibility = View.GONE
                        mImgPannorTip?.visibility = View.GONE
                        jc_video?.visibility=View.GONE
                        rl_zb?.visibility=View.GONE
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
    /**
     * 如果是直播视频 播放直播
     */
    private fun playZbVideo(it: VideoImageBean) {
        if (!it.videoUrl.isNullOrEmpty()) {
            jc_video?.apply {
                IS_PLAYING_LIVE_URL = true
                totalTimeTextView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            }

            jc_video?.setUp(it.videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
            initVideoThumb(it?.getImageUrl())
            val layoutParams = mContext?.let { it1 -> Utils.dip2px(it1, 40f).toInt() }?.let { it2 ->
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    it2
                )
            }
            layoutParams?.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams?.bottomMargin = mContext?.resources?.getDimensionPixelSize(R.dimen.dp_27)
            jc_video?.layoutBottom?.layoutParams = layoutParams
            jc_video?.setOnJcVideoPlayerListener(object : JCVideoPlayer.OnJcVideoPlayerListener {
                override fun onComplete() {
                    EventBus.getDefault().post(UpdateAudioStateEvent(3))
                }

                override fun onPause() {
                    EventBus.getDefault().post(UpdateAudioStateEvent(1))
                }

                override fun onError() {
                    EventBus.getDefault().post(UpdateAudioStateEvent(1))
                }

                override fun onStartPlayer() {
                    EventBus.getDefault().post(UpdateAudioStateEvent(1))
                }

                override fun onStop() {
                    EventBus.getDefault().post(UpdateAudioStateEvent(3))
                }

            })
        }
    }


        private fun initVideoThumb(url: String?) {
            mContext?.let {
                jc_video?.thumbImageView?.let { it1 ->
                    Glide.with(it)
                        .load(url)
                        .centerCrop()
                        .into(it1)
                }
            };
        }

        /**
     * 如果是视频 播放视频
     */
    private fun playVideo(it: VideoImageBean) {
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
        Glide.with(mContext!!).load(coverUrl)
            .into(thumb)
        //视频状态 1直播 2回放
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
        mVideoPlayer?.setUrl(StringUtil.enCodeVideoUrl(it.videoUrl!!))

        mVideoPlayer?.setScreenScaleType(SCREEN_SCALE_DEFAULT)
        // 使用IjkPlayer解码
        mVideoPlayer?.setPlayerFactory(IjkPlayerFactory.create())
        // 播放状态监听
        mVideoPlayer?.addOnStateChangeListener(mOnVideoViewStateChangeListener)
        mVideoPlayer?.setOnClickListener {
            mVideoPlayer?.start()
        }
    }

    /**
     * 直播或是视频回放状态
     */
    private fun showVideoState(it: VideoImageBean?) {
        if(it?.VideoType=="1"){
            ll_root?.visibility=View.VISIBLE
            iv_live?.visibility=View.VISIBLE
            tv_state?.visibility=View.VISIBLE
            tv_state?.text="Live直播中"
            playImageAnim()
        }else if(it?.VideoType=="2"){
            ll_root?.visibility=View.VISIBLE
            iv_live?.visibility=View.GONE
            tv_state?.visibility=View.VISIBLE
            tv_state?.text="直播回放"
        }else{
            ll_root?.visibility=View.GONE
            iv_live?.visibility=View.GONE
            tv_state?.visibility=View.GONE
        }
    }

    private fun playImageAnim() {
        val drawable = iv_live?.background as AnimationDrawable
        if (!drawable.isRunning) {
            drawable.start()
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
       if (jc_video!=null &&   jc_video?.visibility==View.VISIBLE){
           jc_video?.release()
       }
    }
}