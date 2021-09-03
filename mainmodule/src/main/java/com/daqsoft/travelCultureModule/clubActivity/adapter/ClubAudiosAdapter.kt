package com.daqsoft.travelCultureModule.clubActivity.adapter

import android.content.Context
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubAudioBinding
import com.daqsoft.travelCultureModule.clubActivity.bean.AudioBean

/**
 * @Description 社团语言播放
 * @ClassName   ClubAudiosAdapter
 * @Author      luoyi
 * @Time        2020/5/16 10:23
 */
class ClubAudiosAdapter : RecyclerViewAdapter<ItemClubAudioBinding, AudioBean> {

    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_club_audio) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemClubAudioBinding, position: Int, item: AudioBean) {
        if (item.time > 0) {
            mBinding.tvAgTime.text = "00:00/${getTime(item.time.toInt())}"
        } else {
            mBinding.tvAgTime.text = "00:00"
        }

    }

    fun getTime(ms: Int): String {
        var s = (ms / 1000) % 60
        var m = ms / 1000 / 60
        var result = ""
        if (m < 10) {
            result = "0" + m
        } else {
            result = m.toString()
        }
        if (s < 10) {
            result = "$result:0$s"
        } else {
            result = "$result:$s"
        }
        return result
    }
}