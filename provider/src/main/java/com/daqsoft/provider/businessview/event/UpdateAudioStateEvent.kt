package com.daqsoft.provider.businessview.event

/**
 * @Description 停止停解说播放事件
 * @ClassName   UpdateAudioStateEvent
 * @Author      luoyi
 * @Time        2020/4/7 16:18
 */
class UpdateAudioStateEvent {
    /**
     * 1 准备播放 2 播放 3暂停/完成
     */
    var type: Int = 0

    constructor(type: Int) {
        this.type = type
    }
}