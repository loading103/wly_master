package com.daqsoft.baselib.widgets.dkplayer;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

class DqIjkplayer extends IjkPlayer {
    public DqIjkplayer(Context context) {
        super(context);
    }

    @Override
    public void setOptions() {
        super.setOptions();
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
    }
}
