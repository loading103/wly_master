package com.daqsoft.travelCultureModule.resource;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;

public class IjkPlayerCustomFactory extends IjkPlayerFactory {
    @Override
    public IjkPlayer createPlayer(Context context) {
        return new IjkCustomPlayer(context);
    }
}
