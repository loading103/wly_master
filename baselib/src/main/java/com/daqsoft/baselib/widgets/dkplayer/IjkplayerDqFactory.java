package com.daqsoft.baselib.widgets.dkplayer;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.PlayerFactory;

class IjkplayerDqFactory extends PlayerFactory<IjkPlayer> {

    public static IjkplayerDqFactory create() {
        return new IjkplayerDqFactory();
    }

    @Override
    public IjkPlayer createPlayer(Context context)
    {
        DqIjkplayer player = new DqIjkplayer(context);
        return player;
    }
}
