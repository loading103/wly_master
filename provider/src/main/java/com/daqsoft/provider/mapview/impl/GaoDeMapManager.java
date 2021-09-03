package com.daqsoft.provider.mapview.impl;

import android.graphics.Bitmap;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.daqsoft.provider.mapview.IMapLifeCycleManager;
import com.daqsoft.provider.mapview.IMapManager;
import com.daqsoft.provider.mapview.bean.MapLocation;
import com.daqsoft.provider.mapview.bean.RectLat;

import java.util.List;


/**
 * 高德地图管理实现类
 *
 * @author MouJunFeng
 * @version 1.0.0
 * @time 2018-3-14
 * @since JDK 1.8
 */

public class GaoDeMapManager implements IMapManager {
    private MapView mapView;
    private AMap map;
    /**
     * UI设置
     */
    private UiSettings mUiSettings;
    /**
     * 高德地图生命周期管理类
     */
    private IMapLifeCycleManager mapLifeCycleManager;
    /**
     * 高德地图出行规划管理类
     */
    //    private IGaodeTravelPlanning gaodeTravelPlanning;
    /**
     * 以前的market
     */
    private Marker oldMarket;
    /**
     * 偏移量
     */
    private int offsetX = 0;
    /**
     * 偏移量
     */
    private int offsetY = 0;
    /**
     * 设置地图缩放等级
     */

    private static final float zoom = 8;

    public GaoDeMapManager(MapView mapView) {
        if (mapView != null) {
            this.mapView = mapView;
            mapView.onLowMemory();
            map = mapView.getMap();
            mUiSettings = map.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            mapLifeCycleManager = new MapLifeCycleManagerImpl(this);
        }
    }

    /**
     * 获得mapview控件
     *
     * @return
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * 获得map控制
     *
     * @return
     */
    public AMap getAMap() {
        return map;
    }

    /**
     * 获得旧的market
     *
     * @return
     */
    public Marker getOldMarket() {
        return oldMarket;
    }

    /**
     * 设置旧的market
     *
     * @param oldMarket
     */
    public void setOldMarket(Marker oldMarket) {
        this.oldMarket = oldMarket;
    }


    /**
     * 设置偏移量
     *
     * @param offsetX x
     * @param offsetY y
     */
    public void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


    @Override
    public void addMarket(MapLocation bean, int bitmap) {
        LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(bitmap))
                .position(latLng).draggable(true).title(bean.getTitle()).setInfoWindowOffset
                        (offsetX, offsetY).zIndex
                        (2);
        Marker marker = map.addMarker(markerOptions);
        marker.setObject(bean.getT());
    }


    /**
     * 添加market
     *
     * @param bean
     * @param v
     * @param markerOptions
     */
    public void addMarket(MapLocation bean, View v,MarkerOptions markerOptions) {
        Marker marker = map.addMarker(markerOptions);
        marker.setObject(bean.getT());
    }

    /**
     * 添加market
     *
     * @param bean
     * @param v
     */
    public void addMarket(MapLocation bean, View v) {
        LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromView(v))
                .position(latLng).draggable(true).title(bean.getTitle()).setInfoWindowOffset
                        (offsetX, offsetY).zIndex(2);
        Marker marker = map.addMarker(markerOptions);
        marker.setObject(bean.getT());
    }

    @Override
    public Marker getMarket(String title) {
        List<Marker> mapScreenMarkers = map.getMapScreenMarkers();
        for (int i = 0; i < mapScreenMarkers.size(); i++) {
            Marker marker = mapScreenMarkers.get(i);
            if (marker.getTitle() == null) {
                return null;
            }
            if (marker.getTitle().equals(title)) {
                return marker;
            }
        }
        return null;
    }

    /**
     * 清除所有的market
     */
    @Override
    public void clearMarket() {
        List<Marker> mapScreenMarkers = map.getMapScreenMarkers();
        for (int i = 0; i < mapScreenMarkers.size(); i++) {
            Marker marker = mapScreenMarkers.get(i);
            // 移除当前Marker
            marker.remove();
        }
    }

    /**
     * 获取所有market
     * @return
     */
    public List<Marker> getMarkets() {
        return map.getMapScreenMarkers();
    }

    @Override
    public void setLocation(double latitude, double Longitude) {
        // 设置中心点和缩放比例
        LatLng latLng = new LatLng(latitude, Longitude);
        map.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    @Override
    public void setLocation(LatLng latLng) {
        // 设置中心点和缩放比例
        map.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    @Override
    public void setMapRectBitmap(RectLat rectF, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        // 设置坐标点
        // 西南（左下）
        LatLng southwest = new LatLng(rectF.getLeft(), rectF.getBottom());
        // 东北（右上）
        LatLng northesst = new LatLng(rectF.getRight(), rectF.getTop());
        // 覆盖物——图片
        // 设置西南与东北坐标范围
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northesst)
                .include(southwest)
                .build();
        // 定义Ground显示的图片
        //        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.mipmap.ar);
        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromBitmap(bitmap);
        // 定义Ground覆盖物选项
        GroundOverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                // 位置，通过两个经纬度化成一个长方形，西南东北坐标范围
                .image(bdGround)
                // 图片
                .transparency(0.0f)
                // 透明度
                .zIndex(-1);
        // 这个表示在地图上还是地图下
        // 向地图中添加Ground覆盖物
        map.addGroundOverlay(ooGround);
        //        map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    /**
     * 设置map的market点击事件
     *
     * @param listener
     */

    public void setOnMarkerClickListener(AMap.OnMarkerClickListener listener) {
        map.setOnMarkerClickListener(listener);
    }

    public void setOnMapClickListener(AMap.OnMapClickListener listener) {
        map.setOnMapClickListener(listener);
    }

    @Override
    public IMapLifeCycleManager getIMapLifeCycleManager() {
        return mapLifeCycleManager;
    }

    @Override
    public void calculateBusRouteAsyn(MapLocation start, MapLocation end, int var2, String var3,
                                      int var4) {
        //        gaodeTravelPlanning.calculateBusRouteAsyn(start, end, var2, var3, var4);
    }

//    @Override
//    public void calculateDriverRouteAsyn(MapLocation start, MapLocation end, int var2,
//                                         List<LatLonPoint> var3,
//                                         List<List<LatLonPoint>> var4, String var5) {
//        //        gaodeTravelPlanning.calculateDriverRouteAsyn(start, end, var2, var3, var4, var5);
//    }

    @Override
    public void calculateWalkRouteAsyn(MapLocation start, MapLocation end, int var2) {
        //        gaodeTravelPlanning.calculateWalkRouteAsyn(start, end, var2);
    }
}
