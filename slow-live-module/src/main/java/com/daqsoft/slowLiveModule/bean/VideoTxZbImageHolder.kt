package com.daqsoft.slowLiveModule.bean

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cc.shinichi.library.tool.ui.ToastUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.OssUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayer
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoView.OnStateChangeListener
import com.dueeeke.videoplayer.player.VideoView.SCREEN_SCALE_DEFAULT
import com.dueeeke.videoplayer.util.L
import com.flyco.roundview.RoundLinearLayout
import com.jakewharton.rxbinding2.view.RxView
import com.tencent.rtmp.ITXLivePlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXLivePlayConfig
import com.tencent.rtmp.TXLivePlayer
import com.tencent.rtmp.ui.TXCloudVideoView
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   VideoImageHolder
 * @Author      luoyi
 * @Time        2020/3/31 13:49
 */
class VideoTxZbImageHolder : Holder<VideoImageBean> {

    var mVideoPlayer: VideoView<IjkPlayer>? = null
    var mImgeView: ArcImageView? = null
    var mContext: Context? = null
    var ll_root: RoundLinearLayout? = null
    var iv_live: ImageView? = null
    var tv_state: TextView? = null

    var rl_zb: RelativeLayout? = null
    var iv_detail: ImageView? = null
    var iv_play: ImageView? = null

    constructor(itemView: View, context: Context) : super(itemView) {
        this.mContext = context
        itemView.tag = this
    }

    override fun initView(itemView: View?) {
        mVideoPlayer = itemView?.findViewById(R.id.video_player_img)
        mImgeView = itemView?.findViewById(R.id.img_video_img)
        ll_root = itemView?.findViewById(R.id.ll_root)
        tv_state = itemView?.findViewById(R.id.tv_state)
        iv_live = itemView?.findViewById(R.id.iv_live)
        rl_zb = itemView?.findViewById(R.id.rl_zb)
        iv_play = itemView?.findViewById(R.id.iv_play)
        iv_detail = itemView?.findViewById(R.id.iv_detail)
    }
    override fun updateUI(data: VideoImageBean?) {
        data?.let {
            when (it.type) {
                0 -> {
                    // 图片
                    mImgeView?.let { it1->
                        mVideoPlayer?.visibility = View.GONE
                        mImgeView?.visibility = View.VISIBLE
                        GlideModuleUtil.loadDqImageWaterMark(it.imageUrl, mImgeView!!.context, mImgeView!!)
                    }
                }
                1 -> {
                    // 是直播
                    showVideoState(it)
                    if(it.VideoType=="1"){
                        mVideoPlayer?.visibility= View.GONE
                        mImgeView?.visibility = View.GONE
                        rl_zb?.visibility=View.VISIBLE
                        iv_detail?.let { it1 ->
                            Glide.with(mContext!!)
                                .load(it.imageUrl)
                                .error(R.mipmap.placeholder_img_fail_240_180)
                                .centerCrop()
                                .into(it1)
                        }

                        iv_play?.setOnClickListener { its->
                            ARouter.getInstance().build(ARouterPath.SlowLiveModule.SLOW_LIVE_PLAY_ACTIVITY)
                                .withString("liveUrl",it.videoUrl)
                                .withString("imageUrl",it?.imageUrl).navigation()
                        }
                    } else if(it.VideoType=="2") {
                        mVideoPlayer?.visibility= View.VISIBLE
                        mImgeView?.visibility = View.GONE
                        rl_zb?.visibility=View.GONE
                        // 视频
                        if (mVideoPlayer != null) {
                            playVideo(it)
                        } else {}
                    }else {
                        mVideoPlayer?.visibility= View.GONE
                        mImgeView?.visibility = View.GONE
                        rl_zb?.visibility=View.GONE
                    }
                }
                2 -> {
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
    }

}