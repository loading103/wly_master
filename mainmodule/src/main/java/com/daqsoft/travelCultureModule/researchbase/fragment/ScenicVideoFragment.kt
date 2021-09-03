package com.daqsoft.travelCultureModule.researchbase.fragment
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicVideoBinding
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import com.daqsoft.travelCultureModule.resource.IjkPlayerCustomFactory
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * @Description
 * @ClassName   SenicVideoFragment
 * @Author      luoyi
 * @Time        2020/3/31 17:56
 */
class ScenicVideoFragment : BaseFragment<ItemScenicVideoBinding, BaseViewModel>() {

    var videoUrl: String? = null


    companion object {
        const val VIDEO_URL = "video_url"
        fun newInstance(videoUrl: String): ScenicVideoFragment {
            var bundle = Bundle()
            bundle.putString(VIDEO_URL, videoUrl)
            var frag = ScenicVideoFragment()
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_scenic_video
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        videoUrl = arguments?.getString(VIDEO_URL)
        EventBus.getDefault().register(this)
        if (!videoUrl.isNullOrEmpty()) {
            val controller = StandardVideoController(context!!)
            val prepareView = PrepareView(context!!)
            // 封面图
            val thumb = prepareView.findViewById<ImageView>(R.id.thumb)
            // 更改播放按钮
            val playButton = prepareView.findViewById<ImageView>(R.id.start_play)
            playButton.background = context!!.resources.getDrawable(R.drawable.dkplayer_custom_shape_play_bg)
            Glide.with(context!!).load(StringUtil.getVideoCoverUrl(StringUtil.enCodeVideoUrl(videoUrl!!)))
                .into(thumb)
            // 错误视图
            val errorView = ErrorView(context)
            prepareView.setClickStart()
            //播放完成视图
            val completeView = CompleteView(context!!)
            // 标题
            val titleView = TitleView(context!!)
            // 添加 完成/错误/准备组件
            controller.addControlComponent(completeView, errorView, prepareView, titleView)
            controller.addControlComponent(VodControlView(context!!))
            controller.addControlComponent(GestureView(context!!))
            controller.setCanChangePosition(true)
            mBinding.vScenicVideoPlayer?.setVideoController(controller)
            mBinding.vScenicVideoPlayer?.setUrl(StringUtil.enCodeVideoUrl(videoUrl!!))
            mBinding.vScenicVideoPlayer?.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
            // 使用IjkPlayer解码
            mBinding.vScenicVideoPlayer?.setPlayerFactory(IjkPlayerCustomFactory.create())
            // 播放状态监听
            mBinding.vScenicVideoPlayer?.addOnStateChangeListener(mOnVideoViewStateChangeListener)
        }
    }

    override fun initData() {
    }

    private val mOnVideoViewStateChangeListener: VideoView.OnStateChangeListener = object : VideoView.OnStateChangeListener {
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
                    EventBus.getDefault().post(UpdateAudioPlayerEvent(0))
                }
                VideoView.STATE_PREPARED -> {
                    // 需在此时获取视频宽高
                    if (mBinding.vScenicVideoPlayer != null) {
                        val videoSize: IntArray = mBinding.vScenicVideoPlayer!!.getVideoSize()
//                        L.d("视频宽：" + videoSize[0])
//                        L.d("视频高：" + videoSize[1])
                    }
                }
                VideoView.STATE_PLAYING -> {
                    EventBus.getDefault().post(UpdateAudioPlayerEvent(1))
                }
                VideoView.STATE_PAUSED -> {
                    EventBus.getDefault().post(UpdateAudioPlayerEvent(2))
                }
                VideoView.STATE_BUFFERING -> {
                }
                VideoView.STATE_BUFFERED -> {
                }
                VideoView.STATE_PLAYBACK_COMPLETED -> {
                    EventBus.getDefault().post(UpdateAudioPlayerEvent(2))
                }
                VideoView.STATE_ERROR -> {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dealVideoPlayer(event: UpdateScenicVideoPlayerEvent) {
        when (event.type) {
            1 -> {
                // 暂停
                mBinding?.vScenicVideoPlayer?.pause()
            }
            2 -> {
                // Stop
                mBinding?.vScenicVideoPlayer?.release()
            }
        }
    }

    /**
     * 获取返回事件
     */
    fun onBackPress(): Boolean {
        return mBinding?.vScenicVideoPlayer.onBackPressed()
    }
}