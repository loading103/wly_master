package com.daqsoft.provider.event

/**
 * @Description 修改景区视频播放器状态
 * @ClassName   UpdateScenicVideoPlayerEvent
 * @Author      luoyi
 * @Time        2020/4/1 10:00
 */
class UpdateScenicVideoPlayerEvent (type:Int){
    /**
     * 1 暂停 2 stop
     */
    var type:Int=type
}