package com.daqsoft.provider.service;

//import android.app.NotificationChannel;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


import androidx.annotation.Nullable;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * 网络定位服务
 *
 * @author 严博
 * @version 1.0.0
 * @date 2019/5/18 0018 11:45
 * @since JDK 1.8
 */
public class LocationService extends Service {

    /**
     * 音乐播放（防止应用挂起）
     */
    private MediaPlayer mediaPlayer;
    /**
     * 创建前台通知
     */
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private boolean isFirst = true;
    /**
     * 两点之间的距离
     */
    private float distance;
    private List<LatLng> mLatList = new ArrayList<>();
    private String lastName = "";
    private long exitTime = 0L;
    double lat;
    double lon;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化位置
     */
    public void initLocation() {
            GaoDeLocation.getInstance().init(getApplicationContext(),
                    new GaoDeLocation.OnGetCurrentLocationLisner() {

                @Override
                public void onResult(String adCode, final String result, final double lat_,
                                     final double lon_, String adcode) {
                        lat = lat_;
                        lon = lon_;

                }

                @Override
                public void onError(String errormsg) {
//                    SmartApplication.isFirstLocation = true;
//                    stopService(SmartApplication.getIntent());
//                    LogUtils.e("获取定位位置出错了");
//                    FileUtil.writeLog(FileUtil.LOG_FILE_PATH, "获取定位位置出错了", true, "utf-8");
                }
            });

        }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
