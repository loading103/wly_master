package com.daqsoft.provider.mapview.impl;

import android.os.Bundle;

import com.daqsoft.provider.mapview.IMapLifeCycleManager;


/**
 * 地图生命周期管理实现类
 * @author MouJunFeng
 * @time 2018-3-14.
 * @since JDK 1.8
 * @version 1.0.0
 */

public class MapLifeCycleManagerImpl implements IMapLifeCycleManager {
    private GaoDeMapManager gaoDeMapManager;

    public MapLifeCycleManagerImpl(GaoDeMapManager gaoDeMapManager) {
        this.gaoDeMapManager = gaoDeMapManager;
    }

    @Override
    public void oncreate(Bundle bundle) {

        gaoDeMapManager.getMapView().onCreate(bundle);
    }

    @Override
    public void onDestroy() {
        gaoDeMapManager.getMapView().onDestroy();
    }

    @Override
    public void onResume() {
        gaoDeMapManager.getMapView().onResume();
    }

    @Override
    public void onPause() {
        gaoDeMapManager.getMapView().onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        gaoDeMapManager.getMapView().onSaveInstanceState(outState);
    }
}
