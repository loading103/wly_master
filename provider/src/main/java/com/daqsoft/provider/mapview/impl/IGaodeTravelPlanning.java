package com.daqsoft.provider.mapview.impl;

import com.daqsoft.provider.mapview.bean.MapLocation;

import java.util.List;

/**
 * 公交出行规划接口
 *
 * @author MouJunFeng
 * @time 2018-3-16
 * @since JDK 1.8
 * @version 1.0.0
 */

public interface IGaodeTravelPlanning  {
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

    /**
     * 路径规划(驾车)
     *
     * @param start 起点
     * @param end   终点
     * @param var2  驾车模式
     * @param var3  途经点
     * @param var4  避让区域
     * @param var5  避让道路
     */
//    void calculateDriverRouteAsyn(MapLocation start, MapLocation end, int var2, List<LatLonPoint> var3, List<List<LatLonPoint>> var4, String var5);

    /**
     * 路径规划(步行)
     */
    void calculateWalkRouteAsyn(MapLocation start, MapLocation end, int var2);

}
