package com.daqsoft.travelCultureModule.resource;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkCustomPlayer extends IjkPlayer {
    public IjkCustomPlayer(Context context) {
        super(context);
    }

    @Override
    public void setOptions() {
        super.setOptions();
        if (mMediaPlayer != null) {
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            ;
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8);

            mMediaPlayer.setOption(1, "analyzemaxduration", 100L);
            mMediaPlayer.setOption(1, "probesize", 10240L);
            mMediaPlayer.setOption(1, "flush_packets", 1L);
            mMediaPlayer.setOption(4, "packet-buffering", 0L);
            mMediaPlayer.setOption(4, "framedrop", 1L);
        }
    }
}
