package com.daqsoft.guidemodule

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.daqsoft.guidemodule.fragment.GuideVpSpotFragment
import com.daqsoft.guidemodule.net.logI
import org.greenrobot.eventbus.EventBus
import java.io.IOException

internal object GuideSpeakPlayer : MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private var mediaPlayer: MediaPlayer? = null
    /**
     * 是否播放完毕
     */
    private var isCompletion = false

    /**
     * 是否准备完毕
     */
    private var isPrepared = false

    /**
     * 是否正在播放
     */
    private var isPlaying = false


    private var mTagFlag = ""

    private var mPos: Int = 0

    private var mAudioUlr = ""

    private var mSpotName = ""


    init {

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setOnCompletionListener(this)
            mediaPlayer?.setOnPreparedListener(this)
        } catch (e: Exception) {
            Log.d("mediaPlayer", "error", e)
        }

    }


    fun isPlayingByUrl(audioUrl: String, spotName: String, pos: Int): Boolean {
        return try {
            audioUrl == mAudioUlr && mSpotName == spotName && mPos == pos && isPlaying
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun prepareWithUrl(tagFlag: String, pos: Int, audioUrl: String, spotName: String) {

        if (mTagFlag == tagFlag && mPos == pos && mAudioUlr == audioUrl && mSpotName == spotName)
            return

        try {
            isPrepared = false
            isPlaying = false
            isCompletion = false
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(audioUrl)
            mediaPlayer?.prepare()

            mTagFlag = tagFlag
            mPos = pos
            mAudioUlr = audioUrl
            mSpotName = spotName
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun play() {
        postEvent(true)
        try {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {

        postEvent(false)
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onPrepared(arg0: MediaPlayer) {
        isPrepared = true
    }

    override fun onCompletion(arg0: MediaPlayer) {
        postEvent(false)
        isCompletion = true
    }

    private fun postEvent(playStatus: Boolean) {
        isPlaying = playStatus
        EventBus.getDefault().post(GuideSpeakStatusEvent(mTagFlag, isPlaying, mPos, mSpotName, mAudioUlr))
    }
}