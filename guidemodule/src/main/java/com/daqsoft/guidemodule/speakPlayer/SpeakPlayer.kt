package com.daqsoft.guidemodule.speakPlayer

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.lang.Exception


/**
 * @Description  音频播放工具类
 * @ClassName   SpeakPlayer
 * @Author      Wongxd
 * @Time        2020/4/11 11:03
 */
internal object SpeakPlayer {

    const val PERMISSION = "com.daqsoft.travelCultureModule.permission.SpeakPlayReceiver"


    internal data class SpeakPlayBean(val eventTag: String, val audioName: String, val audioUrl: String) {
        override fun equals(other: Any?): Boolean {

            other?.let {
                if (it is SpeakPlayBean) {
                    return this.eventTag == it.eventTag && this.audioName == it.audioName && this.audioUrl == it.audioUrl
                }
            }
            return false
        }
    }

    private var mSpeakPlayBean = SpeakPlayBean("", "", "")

    var prefixAction: String = ""

    //启动音频播放服务
    fun startPlayService(context: Context, prefixAction: String) {
        context.startService(Intent(context, SpeakPlayService::class.java))
        if (prefixAction.isBlank()) {
            throw Exception("prefixAction can not be null or blank")
        } else {
            this.prefixAction = prefixAction
        }
    }

    //停止音频播放服务
    fun stopPlayService(context: Context) {
        context.stopService(Intent(context, SpeakPlayService::class.java))
    }

    //注册播放（进度、状态）接收器
    fun registerPlayReceiver(context: Context, playReceiver: SpeakPlayReceiver) {
        val filter = IntentFilter()
        filter.addAction(prefixAction + SpeakConstants.ACTION_S_2_A_PRG)
        filter.addAction(prefixAction + SpeakConstants.ACTION_S_2_A_STATUS)
        filter.addAction(prefixAction + SpeakConstants.ACTION_S_2_A_BUFFER_PRG)
        filter.addAction(prefixAction + SpeakConstants.ACTION_INCOMMING_CALL)
        context.registerReceiver(playReceiver, filter,PERMISSION,null)
    }

    //取消注册
    fun unregisterPlayReceiver(context: Context, playReceiver: SpeakPlayReceiver) {
        context.unregisterReceiver(playReceiver)
    }


    //是否是正在播放此音频
    fun isPlaying(bean: SpeakPlayBean): Boolean {
        return mSpeakPlayBean == bean
    }

    //通知PlayService-新音频
    fun notifyNewMp3(context: Context, bean: SpeakPlayBean) {
        mSpeakPlayBean = bean
        val newMp3 = Intent(prefixAction + SpeakConstants.ACTION_A_2_S_MP3)
        newMp3.putExtra(SpeakConstants.EXTRA_MP3_PATH, mSpeakPlayBean.audioUrl)
        context.sendBroadcast(newMp3,SpeakConstants.PERMISSION)
    }

    //通知PlayService-播放
    fun notifyPlay(context: Context) {
        val playCtrl = Intent(prefixAction + SpeakConstants.ACTION_A_2_S_CTRL)
        playCtrl.putExtra(SpeakConstants.EXTRA_PLAY_CTRL, SpeakConstants.PLAY_CTRL_PLAY)
        context.sendBroadcast(playCtrl,SpeakConstants.PERMISSION)
    }

    //通知PlayService-更新进度
    fun notifyProgress(context: Context, progress: Int) {
        val intentPrg = Intent(prefixAction + SpeakConstants.ACTION_A_2_S_PRG)
        intentPrg.putExtra(SpeakConstants.EXTRA_PLAY_PRG, progress)
        context.sendBroadcast(intentPrg,SpeakConstants.PERMISSION)
    }

    //通知PlayService-暂停
    fun notifyPause(context: Context) {
        val playCtrl = Intent(prefixAction + SpeakConstants.ACTION_A_2_S_CTRL)
        playCtrl.putExtra(SpeakConstants.EXTRA_PLAY_CTRL, SpeakConstants.PLAY_CTRL_PAUSE)
        context.sendBroadcast(playCtrl,SpeakConstants.PERMISSION)
    }

    //格式化播放时间
    fun getFormatPlayTime(progress: Int): String {
        val min = progress / 1000 / 60
        val sec = progress / 1000 % 60
        return String.format("%02d:%02d", min, sec)
    }

}