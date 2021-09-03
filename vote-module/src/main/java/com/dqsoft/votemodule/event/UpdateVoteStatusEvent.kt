package com.dqsoft.votemodule.event

/**
 * @Description
 * @ClassName   UpdateVoteStatusEvent
 * @Author      luoyi
 * @Time        2020/11/25 16:37
 */
class UpdateVoteStatusEvent {
    val proId: String

    constructor(id: String) {
        this.proId = id
    }
}