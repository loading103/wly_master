package com.daqsoft.guidemodule.speakPlayer


/**
 * @Description  语音播放器的一些常量
 * @ClassName   SpeakConstants
 * @Author      Wongxd
 * @Time        2020/4/11 11:02
 */
internal object SpeakConstants {

    const val PERMISSION = "com.daqsoft.travelCultureModule.permission.SpeakPlayReceiver"


    //新音频
    const val ACTION_A_2_S_MP3 = "ACTION_A_2_S_MP3"
    const val EXTRA_MP3_PATH = "EXTRA_MP3_PATH"

    //播放控制（播放，暂停）
    const val ACTION_A_2_S_CTRL = "ACTION_A_2_S_CTRL"
    const val EXTRA_PLAY_CTRL = "EXTRA_PLAY_CTRL"
    const val PLAY_CTRL_PLAY = 1001
    const val PLAY_CTRL_PAUSE = 1002

    //播放进度
    const val ACTION_A_2_S_PRG = "ACTION_A_2_S_PRG"
    const val ACTION_S_2_A_PRG = "ACTION_S_2_A_PRG"
    const val EXTRA_PLAY_PRG = "EXTRA_PLAY_PRG"

    //MediaPlayer（Prepared，Completion）
    const val ACTION_S_2_A_STATUS = "ACTION_S_2_A_STATUS"
    const val EXTRA_STATUS = "EXTRA_STATUS"
    const val STATUS_COMPLETED = 2001
    const val STATUS_PREPARED = 2002
    const val EXTRA_DURATION = "EXTRA_DURATION"

    //缓冲进度
    const val ACTION_S_2_A_BUFFER_PRG = "ACTION_S_2_A_BUFFER_PRG"
    const val EXTRA_BUFFER_PRG = "EXTRA_BUFFER_PRG"

    //update frequency
    const val WHAT_CHANGE_PLAY_PROGRESS = 1
    const val HANDLER_DELAY = 1000L

    //监听来电
    const val ACTION_INCOMMING_CALL = "android.intent.action.PHONE_STATE"

}