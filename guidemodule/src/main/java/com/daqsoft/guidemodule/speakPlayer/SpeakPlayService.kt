package com.daqsoft.guidemodule.speakPlayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.daqsoft.guidemodule.speakPlayer.SpeakPlayer.prefixAction


/**
 * @Description  语音播放服务
 * @ClassName   SpeakPlayService
 * @Author      Wongxd
 * @Time        2020/4/11 11:23
 */
internal class SpeakPlayService : Service() {


    companion object {
        internal class PlayHandler(private val speakPlayService: SpeakPlayService) : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                speakPlayService.notifyProgress()
                sendEmptyMessageDelayed(SpeakConstants.WHAT_CHANGE_PLAY_PROGRESS, SpeakConstants.HANDLER_DELAY)
            }
        }

    }

    inner class PlayReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                prefixAction + SpeakConstants.ACTION_A_2_S_MP3 -> {
                    val mp3Path: String = intent.extras[SpeakConstants.EXTRA_MP3_PATH] as String
                    onNewMp3(mp3Path)
                }
                prefixAction + SpeakConstants.ACTION_A_2_S_PRG -> {
                    val currentPosition = intent.extras[SpeakConstants.EXTRA_PLAY_PRG] as Int
                    mMediaPlayer.seekTo(currentPosition)
                }
                prefixAction + SpeakConstants.ACTION_A_2_S_CTRL -> {
                    val playCtrl: Int = intent.extras[SpeakConstants.EXTRA_PLAY_CTRL] as Int
                    when (playCtrl) {
                        SpeakConstants.PLAY_CTRL_PLAY -> onPlay()
                        SpeakConstants.PLAY_CTRL_PAUSE -> onPause()
                    }
                }
            }
        }
    }

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mPlayReceiver: PlayReceiver
    private lateinit var mPlayHandler: PlayHandler
    private var isPause = false


    fun onNewMp3(mp3Path: String) {
        isPause = false
        mMediaPlayer.reset()
        mMediaPlayer.setOnPreparedListener { notifyPrepared() }
        mMediaPlayer.setOnBufferingUpdateListener { _, percent -> notifyBufferPrg(percent) }
        mMediaPlayer.setOnCompletionListener { notifyCompleted() }
        mMediaPlayer.setDataSource(mp3Path)
        mMediaPlayer.prepareAsync()
    }

    fun onPlay() {
        mPlayHandler.sendEmptyMessage(SpeakConstants.WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.start()
    }

    fun onPause() {
        mPlayHandler.removeMessages(SpeakConstants.WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.pause()
        isPause = true
    }

    //通知播放活动（Activity）-准备完毕
    private fun notifyPrepared() {
        try {
            if (!isPause) {
                val duration = mMediaPlayer.duration
                val playStatus = Intent(prefixAction + SpeakConstants.ACTION_S_2_A_STATUS)
                playStatus.putExtra(SpeakConstants.EXTRA_STATUS, SpeakConstants.STATUS_PREPARED)
                playStatus.putExtra(SpeakConstants.EXTRA_DURATION, duration)
                sendBroadcast(playStatus,SpeakConstants.PERMISSION)
                onPlay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //通知播放活动（Activity）-缓冲进度
    private fun notifyBufferPrg(percent: Int) {
        try {
            val bufferPrg = Intent(prefixAction + SpeakConstants.ACTION_S_2_A_BUFFER_PRG)
            bufferPrg.putExtra(SpeakConstants.EXTRA_BUFFER_PRG, percent)
            sendBroadcast(bufferPrg,SpeakConstants.PERMISSION)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //通知播放活动（Activity）-更新进度
    private fun notifyProgress() {
        try {
            val currentTime = mMediaPlayer.currentPosition
            val playPrg = Intent(prefixAction + SpeakConstants.ACTION_S_2_A_PRG)
            playPrg.putExtra(SpeakConstants.EXTRA_PLAY_PRG, currentTime)
            sendBroadcast(playPrg,SpeakConstants.PERMISSION)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //通知播放活动（Activity）-播放完成
    private fun notifyCompleted() {
        try {
            val playStatus = Intent(prefixAction + SpeakConstants.ACTION_S_2_A_STATUS)
            playStatus.putExtra(SpeakConstants.EXTRA_STATUS, SpeakConstants.STATUS_COMPLETED)
            sendBroadcast(playStatus,SpeakConstants.PERMISSION)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mPlayHandler = PlayHandler(this)
        mMediaPlayer = MediaPlayer()
        mPlayReceiver = PlayReceiver()
        val filter = IntentFilter()
        filter.addAction(prefixAction + SpeakConstants.ACTION_A_2_S_MP3)
        filter.addAction(prefixAction + SpeakConstants.ACTION_A_2_S_PRG)
        filter.addAction(prefixAction + SpeakConstants.ACTION_A_2_S_CTRL)
        registerReceiver(mPlayReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mPlayReceiver)
        mMediaPlayer.stop()
        mMediaPlayer.release()
        mPlayHandler.removeMessages(SpeakConstants.WHAT_CHANGE_PLAY_PROGRESS)
    }

}