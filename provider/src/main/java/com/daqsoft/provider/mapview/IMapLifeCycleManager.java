package com.daqsoft.provider.mapview;

import android.os.Bundle;

/**
 * map的生命周期管理接口
 * @author MouJunFeng
 * @time 2018-3-14.
 */

public interface IMapLifeCycleManager {
    /**
     * 获得MapView
     */

    /**
     * 地图的Oncreate方法
     * @param bundle
     */
    void oncreate(Bundle bundle);
    /**
     * 地图的onDestroy方法
     */
    void onDestroy();
    /**
     * 地图的onResume方法
     */
    void onResume();
    /**
     * 地图的onPause方法
     */
    void onPause();
    /**
     * 地图的onSaveInstanceState方法
     */
    void onSaveInstanceState(Bundle outState);
}
