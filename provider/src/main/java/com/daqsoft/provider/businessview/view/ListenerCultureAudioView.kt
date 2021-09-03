package com.daqsoft.provider.businessview.view

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.adapter.ProviderCultureSpeakAdapter
import com.daqsoft.provider.businessview.adapter.ProviderSpeakAdapter
import com.daqsoft.provider.databinding.LayoutListenerCultureAudioBinding
import com.daqsoft.provider.network.venues.bean.AudioInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @Description 听解说
 * @ClassName   ListenerAudioView
 * @Author      luoyi
 * @Time        2020/4/1 16:11
 */
class ListenerCultureAudioView : FrameLayout, ProviderSpeakAdapter.MeidaPlayerListener, ProviderCultureSpeakAdapter.MeidaPlayerListener {

    val TAG_CLASS = "ListenerCultureAudioView"
    private var mContext: Context? = null
    private var binding: LayoutListenerCultureAudioBinding? = null
    /**
     *  音频实体
     */
    var audioInfos: MutableList<AudioInfo>? = null
    /**
     *  解说适配器
     */
    var adapter: ProviderCultureSpeakAdapter? = null
    /**
     *  音频播放器
     */
    private var mMediaPlayer: MediaPlayer? = null

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

    var onPlayerListener:OnAudioPlayerListener?=null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_listener_culture_audio,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置标题
     */
    fun setTitle(title:String){
        binding?.providerDetailsListener?.text = title
    }

    /**
     * 设置标题icon
     */
    fun setTitleIcon(icon:Int){
        binding?.providerDetailsListener?.setCompoundDrawablesWithIntrinsicBounds(mContext?.resources?.getDrawable(icon),null,null,null)
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
            adapter = ProviderCultureSpeakAdapter(mContext!!)
            adapter?.listener = this
            var  mLayoutManager= ViewPagerLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            scrollLisition(mLayoutManager)
            binding?.recyclerProviderDetailsListener?.layoutManager=mLayoutManager
            binding?.recyclerProviderDetailsListener?.setHasFixedSize(true)
            binding?.recyclerProviderDetailsListener?.setItemViewCacheSize(20)
            binding?.recyclerProviderDetailsListener?.adapter = adapter
            adapter?.clear()
            adapter?.add(audioInfos!!)

        }
    }

    private fun scrollLisition(mLayoutManager: ViewPagerLayoutManager) {
        mLayoutManager.setOnViewPagerListener(object :OnViewPagerListener{

            @SuppressLint("LongLogTag")
            override fun onInitComplete() {
                Log.e(TAG_CLASS, "初始化完成");
            }

            @SuppressLint("LongLogTag")
            override fun onPageRelease(isNext: Boolean, position: Int) {
                Log.e(TAG_CLASS, "释放位置:$position 下一页:$isNext");
//                binding?.recyclerProviderDetailsListener?.getChildAt(position)?.findViewById<ImageView>(R.id.img_commentary_opeartion)?.setBackgroundResource(R.mipmap.provider_details_commentary_play)
//                pauseAudioPlayer()
            }


            @SuppressLint("LongLogTag")
            override fun onPageSelected(position: Int, isBottom: Boolean) {
                Log.e(TAG_CLASS, "选中位置:$position  是否是滑动到底部:$isBottom");
//
            }

        })

    }


    /**
     * 启动播放
     */
    override fun startAudioPlayer(url: String) {
        try {
            if(onPlayerListener!=null){
                onPlayerListener?.onStartPlayer()
            }
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
        val duration = mMediaPlayer?.duration
        if (cutdownDisable != null) {
            try {
                cutdownDisable!!.dispose()
                cutdownDisable = null
            } catch (e: java.lang.Exception) {

            }
        }
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
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
            cutdownDisable?.dispose()
        } catch (e: Exception) {
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
        }
    }

    /**
     * 释放播放器资源
     */
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