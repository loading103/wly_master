package com.daqsoft.guidemodule.speakPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.daqsoft.guidemodule.speakPlayer.SpeakPlayer.prefixAction


/**
 * @Description  接收 PlayService 广播的 Progress 和 Status
 * @ClassName   SpeakPlayReceiver
 * @Author      Wongxd
 * @Time        2020/4/11 11:10
 */
internal abstract class SpeakPlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            prefixAction + SpeakConstants.ACTION_S_2_A_PRG -> {
                val progress = intent.extras[SpeakConstants.EXTRA_PLAY_PRG] as Int
                onProgressUpdate(progress)
            }
            prefixAction + SpeakConstants.ACTION_S_2_A_STATUS -> {
                val audioStatus: Int = intent.extras[SpeakConstants.EXTRA_STATUS] as Int
                when (audioStatus) {
                    SpeakConstants.STATUS_PREPARED -> {
                        val duration: Int = intent.extras[SpeakConstants.EXTRA_DURATION] as Int
                        onPlayPrepared(duration)
                    }
                    SpeakConstants.STATUS_COMPLETED -> onPlayCompleted()
                }
            }
            prefixAction + SpeakConstants.ACTION_S_2_A_BUFFER_PRG -> {
                val percent: Int = intent.extras[SpeakConstants.EXTRA_BUFFER_PRG] as Int
                onBufferPrgUpdate(percent)
            }
            prefixAction + SpeakConstants.ACTION_INCOMMING_CALL -> {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    onStateRinging()
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    onStateIdle()
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    onStateOffHook()
                }
            }
        }
    }

    abstract fun onProgressUpdate(progress: Int)//同步后台播放进度
    abstract fun onPlayPrepared(duration: Int)//播放准备完成，同步状态及音频时长
    abstract fun onPlayCompleted()//播放完成，同步播放完成状态
    open fun onBufferPrgUpdate(percent: Int) {}//缓冲进度更新
    open fun onStateRinging() {}//来电-响铃
    open fun onStateIdle() {}//来电-挂断
    open fun onStateOffHook() {}//来电-接听

}