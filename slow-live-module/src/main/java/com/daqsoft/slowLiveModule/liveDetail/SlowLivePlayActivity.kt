package com.daqsoft.slowLiveModule.liveDetail

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.shinichi.library.tool.ui.ToastUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.timepicker.IMDensityUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.adapter.ProviderCommentAdapter
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.slowLiveModule.bean.VideoZbImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.bean.LiveDetailBean
import com.daqsoft.slowLiveModule.bean.VideoTxZbImageHolder
import com.daqsoft.slowLiveModule.bean.getpositionImages
import com.daqsoft.slowLiveModule.databinding.SlowLiveAtyLiveDetailBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemAroundBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemRvTagBinding
import com.daqsoft.slowLiveModule.databinding.SlowLivePlayBinding
import com.daqsoft.slowLiveModule.rv.dsl.linear
import com.daqsoft.slowLiveModule.video.VideoActivity
import com.tencent.rtmp.ITXLivePlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXLivePlayConfig
import com.tencent.rtmp.TXLivePlayer
import com.tencent.rtmp.ui.TXCloudVideoView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerDqUI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast


/**
 * @des     SlowLiveDetailActivity
 * @class   SlowLiveDetailActivity
 * @author  Wongxd
 * @date    2020-4-16  20:19
 *
 */
@Route(path = ARouterPath.SlowLiveModule.SLOW_LIVE_PLAY_ACTIVITY)
internal class SlowLivePlayActivity :
    TitleBarActivity<SlowLivePlayBinding, LiveDetailViewModel>(), ITXLivePlayListener {

    @JvmField
    @Autowired
    var liveUrl: String = ""

    @JvmField
    @Autowired
    var imageUrl: String = ""

    lateinit var mPlayConfig: TXLivePlayConfig

    private var mLivePlayer: TXLivePlayer? = null

    // player 播放链接类型
    private var mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4

    //播放是否开始了
    private var mPlayStarted = false


    override fun getLayout(): Int = R.layout.slow_live_play

    override fun injectVm(): Class<LiveDetailViewModel> = LiveDetailViewModel::class.java

    override fun setTitle(): String = getString(R.string.slow_live_live_detail)


    override fun initView() {
    }

    override fun initData() {
        if (!liveUrl.isNullOrEmpty()) {
            showLoadingDialog()
            if (liveUrl.startsWith("rtmp://")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP
            } else if (liveUrl.endsWith(".flv")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV
            } else if (liveUrl.endsWith(".m3u8")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS
            } else if (liveUrl.endsWith(".mp4")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4
            }
            mPlayConfig = TXLivePlayConfig()
            mLivePlayer = TXLivePlayer(this)

            mLivePlayer?.setPlayerView(mBinding.mPlayerView)
            mLivePlayer?.enableHardwareDecode(false)
            mLivePlayer?.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
            mLivePlayer?.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)
            val mPlayConfig = TXLivePlayConfig()
            mPlayConfig.setAutoAdjustCacheTime(true)
            mPlayConfig.setMinAutoAdjustCacheTime(1f)
            mPlayConfig.setMaxAutoAdjustCacheTime(5f)
            mLivePlayer?.setConfig(mPlayConfig)
            mLivePlayer?.setPlayListener(this)
            var result = mLivePlayer?.startPlay(liveUrl, mPlayType) // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
            if (result == 0) {
                mPlayStarted = true
            }
            if (result == 1) {
                ToastUtil.getInstance()._short(this,"播放失败,直播地址为空")
                dissMissLoadingDialog()
                finish()
            }
            if (result == 2) {
                ToastUtil.getInstance()._short(this,"播放失败,直播地址无效")
                dissMissLoadingDialog()
                finish()
            }
        }else{
            ToastUtil.getInstance()._short(this,"播放失败,直播地址为空")
            finish()
        }
    }


    override fun onPlayEvent(p0: Int, p1: Bundle?) {
        Log.e("------p0",p0.toString())
        when (p0) {
            //开始
            TXLiveConstants.PLAY_EVT_PLAY_BEGIN->{
                dissMissLoadingDialog()
            }
            //加载中
            TXLiveConstants.PLAY_EVT_PLAY_LOADING->{
            }
            //获取第一帧
            TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME->{
                mBinding.ivDetail?.visibility=View.GONE
                mBinding.ivPlay?.visibility=View.GONE
            }
            //播放失败
            TXLiveConstants.PLAY_ERR_NET_DISCONNECT->{
                dissMissLoadingDialog()
                ToastUtil.getInstance()._short(this,"链接超时")
            }
            TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND->{
                dissMissLoadingDialog()
                ToastUtil.getInstance()._short(this,"链接超时")
            }
            TXLiveConstants.PLAY_EVT_PLAY_END ->{
                mBinding.ivDetail?.visibility=View.GONE
                mBinding.ivPlay?.visibility=View.GONE
            }
            TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION ->
                onVideoSizeChanged(0f, 0f)
        }
    }

    override fun onNetStatus(p0: Bundle?) {

    }


    /**
     * 获取到视频宽高回调
     */
    fun onVideoSizeChanged(videoWidth: Float, videoHeight: Float) {
        if (mBinding.mPlayerView != null && videoWidth > 0 && videoHeight > 0) {
            val params = mBinding.mPlayerView?.layoutParams as FrameLayout.LayoutParams
            if (videoWidth / videoHeight > 0.5625f) { //横屏 9:16=0.5625
                params.width=IMDensityUtil.getScreenWidth(this)
                params.height = ((mBinding.mPlayerView?.width!! / videoWidth * videoHeight).toInt())
                params.gravity = Gravity.CENTER
                mBinding.mPlayerView?.requestLayout()
            }
        }
    }

    /**
     * 循环播放
     */
    private fun onReplay() {
        if (mPlayStarted && mLivePlayer != null) {
            mLivePlayer?.seek(0)
            mLivePlayer?.resume()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mPlayStarted = false
        if (mLivePlayer != null) {
            mLivePlayer?.stopPlay(true)
            mLivePlayer?.setPlayListener(null)
            mLivePlayer=null
        }
        mBinding.mPlayerView?.onDestroy()
    }
}