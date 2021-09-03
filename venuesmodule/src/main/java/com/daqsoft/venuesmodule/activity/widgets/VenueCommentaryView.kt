package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.event.UpdatePlayerStateEvent
import com.daqsoft.venuesmodule.adapter.VenuesSpeakAdapter
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsSpeakingBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description 听解说view
 * @ClassName   VenueCommentaryView
 * @Author      luoyi
 * @Time        2020/3/26 16:43
 */
class VenueCommentaryView : FrameLayout, VenuesSpeakAdapter.MeidaPlayerListener {

    private var mContext: Context? = null

    private var binding: IncludeVenueDetailsSpeakingBinding? = null

    /**
     *  音频实体
     */
    var audioInfos: MutableList<AudioInfo>? = null
    /**
     *  解说适配器
     */
    var adapter: VenuesSpeakAdapter? = null
    /**
     *  音频播放器
     */
    private var mMediaPlayer: MediaPlayer? = null
    var onPlayerListener:OnAudioPlayerListener?=null
    /**
     *  用于标识是否暂停
     */
    var isPaused: Boolean = false

    var isPlay: Boolean = false

    var isChangedUrl: Boolean = false
    /**
     *  倒计时工具
     */
    var cutdownDisable: Disposable? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_speaking,
            this,
            false
        )
        addView(binding!!.root)
    }

    init {

    }

    /**
     *  设置数据
     *  @param bean AudioInfo
     */
    open fun setData(bean: MutableList<AudioInfo>) {
        this.audioInfos = bean
        UpdateUi()
    }

    private fun UpdateUi() {
        if (!audioInfos.isNullOrEmpty()) {
            adapter = VenuesSpeakAdapter(mContext!!)
            adapter?.listener = this
            binding?.recyclerVenuesDetailsListener?.layoutManager = LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL,
                false
            )

            binding?.recyclerVenuesDetailsListener?.adapter = adapter
            adapter?.clear()
            adapter?.isNeedMore = audioInfos!!.size > 2
            if (adapter?.isNeedMore!!) {
                binding?.vVenueShowMore?.visibility = View.VISIBLE
            } else {
                binding?.vVenueShowMore?.visibility = View.GONE
            }
            adapter?.add(audioInfos!!)
        }
        binding?.vVenueShowMore?.onNoDoubleClick {

            if (adapter != null) {
                if (adapter!!.isNeedMore) {
                    adapter!!.notifyDataSetChanged()
                    val drawable2 = mContext!!.getDrawable(R.mipmap.venue_arrow_up)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtVenueCommentaryMore?.setCompoundDrawables(null, null, drawable2, null)
                    binding?.txtVenueCommentaryMore?.text = "收起全部解说"
                    adapter!!.isNeedMore = false
                } else {
                    adapter!!.notifyDataSetChanged()
                    val drawable2 = mContext!!.getDrawable(R.mipmap.venue_arrow_down)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtVenueCommentaryMore?.setCompoundDrawables(null, null, drawable2, null)
                    binding?.txtVenueCommentaryMore?.text = "查看全部解说"
                    adapter!!.isNeedMore = true
                }
            }
        }
    }

    /**
     * 启动播放
     */
    override fun startAudioPlayer(url: String) {
        try {
          onPlayerListener?.onStartPlayer()
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer()
                mMediaPlayer!!.setOnCompletionListener {
                    // 播放完成
                    if (!isChangedUrl) {
                        adapter?.updatePlayCompletion()
                    }
                }
                mMediaPlayer!!.setOnPreparedListener {
                    // 准备完成开始播放
                    isChangedUrl = false
                    mMediaPlayer!!.start()
                    startUpdateProgress()
                }
            }
            if (!isPaused) {
                mMediaPlayer!!.setDataSource(url)
                mMediaPlayer!!.prepareAsync()

                isPlay = true
            } else {
                mMediaPlayer!!.start()
                isPlay = true
                startUpdateProgress()
            }
        } catch (e: Exception) {
            isPlay = false
        } finally {
            isPaused = false
        }
    }

    /**
     * 修改进度条
     */
    private fun startUpdateProgress() {
        // 发送消息暂停视频播放
        EventBus.getDefault().post(UpdatePlayerStateEvent(2))
        val duration = mMediaPlayer?.duration
        if (cutdownDisable != null) {
            cutdownDisable!!.dispose()
            cutdownDisable = null
        }
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            // 按时间间隔发送整数的Observable
            .observeOn(AndroidSchedulers.mainThread())
            // 切换到主线程修改UI
            .takeWhile { isPlay }
            .subscribe {
                val curTime = mMediaPlayer?.currentPosition
                var show = curTime
                if (curTime!! < duration!!) {
                    adapter?.updateProgress(show!!, duration)
                    return@subscribe
                }
            }
    }


    /**
     * 改变资源文件
     */
    override fun changeAudioPlayer(url: String) {
        try {
            isChangedUrl = true
            isPaused = false
            mMediaPlayer?.stop()
            mMediaPlayer?.seekTo(0)
            mMediaPlayer?.setDataSource(url)
            mMediaPlayer?.prepareAsync()
            isPlay = true
            startUpdateProgress()
        } catch (e: Exception) {
            Log.e("play", "" + e.message)
        }
    }

    /**
     *  暂停播放
     */
    override fun pauseAudioPlayer() {
        try {
            mMediaPlayer?.pause()
            isPaused = true
            isPlay = false
//            adapter?.pausePlaying()
        } catch (e: Exception) {
            Log.e("ss", e.message)
        }
    }

    /**
     * 修改暂停Ui
     */
    fun updatePauseUi() {
        adapter?.pausePlaying()
    }

    /**
     * 停止播放
     */
    override fun stopAudioPlayer() {
        try {
            isPaused = false
            isPlay = false
            mMediaPlayer?.stop()
        } catch (e: Exception) {
            Log.e("ssss", e.message)
        }
    }

    fun releaseAudioPlayer() {
        try {
            cutdownDisable?.dispose()
            isPlay = false
            pauseAudioPlayer()
            stopAudioPlayer()
            mMediaPlayer?.reset()
            mMediaPlayer?.release()
            mMediaPlayer = null

        } catch (e: Exception) {

        }
    }

    interface  OnAudioPlayerListener{
        /**
         * 启动播放
         */
        fun onStartPlayer()

    }
}