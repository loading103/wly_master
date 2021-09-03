package com.daqsoft.legacyModule.media.fgt

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.LegacyAudioAdapter
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentMediaBinding
import com.daqsoft.legacyModule.media.LegacyMediaViewModel
import com.daqsoft.legacyModule.media.bean.LegacyAudioBean
import com.daqsoft.legacyModule.media.bean.LegacyMediaListBean
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * @des     非遗音频
 * @class   LegacyMediaAudioFragment
 * @author  Wongxd
 * @date    2020-4-21 17:50
 */
internal class LegacyMediaAudioFragment : BaseFragment<LegacyModuleFragmentMediaBinding, LegacyMediaViewModel>(),LegacyAudioAdapter.MeidaPlayerListener {

    override fun getLayout(): Int = R.layout.legacy_module_fragment_media

    override fun injectVm(): Class<LegacyMediaViewModel> = LegacyMediaViewModel::class.java

    private val mMediaType = "voice"
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
    override fun initView() {


        mBinding.srl.setOnRefreshListener {
            mBinding.srl.finishRefresh()
            refreshData()
        }

        mBinding.srl.setOnLoadMoreListener {
            mModel.pageManager.nexPageIndex
            mModel.getMediaList(mMediaType)
            mBinding.srl.finishLoadMore()
        }

        adapter = LegacyAudioAdapter(context!!)
        adapter?.listener = this

        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
        }
        mBinding.rv.adapter = adapter

        initViewModel()
    }

    private fun initViewModel() {
        mModel.mediaList.observe(this, Observer { list ->
            mBinding.rv.visibility = View.VISIBLE

            if (list.isNullOrEmpty()) return@Observer


            if (mModel.pageManager.isFirstIndex) {
                adapter?.clear()
            }
            parseData(list)
        })
    }

    private fun parseData(list:MutableList<LegacyMediaListBean>){
        val audioList = mutableListOf<LegacyAudioBean>()
        for (legacyBean in list){
            val audioInfos = legacyBean.getAudioInfo()
            if(!audioInfos.isNullOrEmpty()){
                for (item in audioInfos){
                    val audios = LegacyAudioBean()
                    audios.name = legacyBean.name
                    audios.resourceId = legacyBean.resourceId
                    audios.resourceType = legacyBean.resourceType
                    audios.audioInfo = item
                    audioList.add(audios)
                }
            }
        }
        adapter?.add(audioList)
    }

    private fun refreshData() {
        mModel.pageManager.initPageIndex()
        mModel.getMediaList(mMediaType)
    }

    override fun initData() {
        refreshData()
    }

    private var adapter:LegacyAudioAdapter? = null

    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }
    override fun onDestroyView() {
        releaseAudioPlayer()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onStop() {
        releaseAudioPlayer()
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePlayerEvent(event: UpdateAudioPlayerEvent) {
        when (event.type) {
            1 -> {
                // 暂停
                updatePauseUi()
            }
        }
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