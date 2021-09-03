package com.daqsoft.provider.mapview;

import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.daqsoft.provider.mapview.bean.MapLocation;
import com.daqsoft.provider.mapview.bean.RectLat;

import java.util.List;

/**
 * 定义我们需要的地图方法
 *
 * @author MouJunFeng
 * @version 1.0.0
 * @time 2018-3-14.
 * @since JDK 1.8
 */

public interface IMapManager {
    /**
     * 添加标记点
     */
    void addMarket(MapLocation bean, int bitmap);

    /**
     * 清除market
     */
    void clearMarket();

    /**
     * 查找market 该market集合必须在页面能够显示
     *
     * @param title
     * @return
     */
    Marker getMarket(String title);

    /**
     * 定位当前位置
     */
    void setLocation(double latitude, double Longitude);

    /**
     * 定位当前位置
     */
    void setLocation(LatLng latLng);

    /**
     * 设置覆盖地图的图片
     */
    void setMapRectBitmap(RectLat rectLat, Bitmap bitmap);

    /**
     * 获得生命周期管理的接口
     *
     * @return
     */
    IMapLifeCycleManager getIMapLifeCycleManager();

    /**
     * 路径规划(公交)
     *
     * @param start 起点
     * @param end   终点
     * @param var2  示公交查询模式
     * @param var3  公交查询城市区号
     * @param var4  是否计算夜班车
     */
    void calculateBusRouteAsyn(MapLocation start, MapLocation end, int var2, String var3, int var4);

//    /**
//     * 路径规划(驾车)
//     *
//     * @param start 起点
//     * @param end   终点
//     * @param var2  驾车模式
//     * @param var3  途经点
//     * @param var4  避让区域
//     * @param var5  避让道路
//     */
//    void calculateDriverRouteAsyn(MapLocation start, MapLocation end, int var2, List<LatLonPoint> var3,
//                                  List<List<LatLonPoint>> var4, String var5);

    /**
     * 路径规划(步行)
     */
    void calculateWalkRouteAsyn(MapLocation start, MapLocation end, int var2);

}
