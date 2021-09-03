package com.daqsoft.provider.event

/**
 * @Description
 * @ClassName   UpdateAudioPlayerEvent
 * @Author      luoyi
 * @Time        2020/4/1 19:01
 */
class UpdateAudioPlayerEvent(type: Int) {
    /**
     * 1 开始播放 2暂停播放
     */
    var type: Int = type
}