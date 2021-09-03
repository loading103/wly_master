package com.daqsoft.provider.mapview.bean;

/**
 *  map地图添加覆盖负的bean对象
 * @author MouJunFeng
 * @time 2018-3-14
 * @since JDK 1.8
 * @version 1.0.0
 */

public class MapLocation<T> {
    /**
     *纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double Longitude;
    /**
     * 标题
     */
    private String title;
    /**
     * 是否显示
     */
    private boolean isShowInforWindow;
    /**
     * /T为传入的bean
     */
    private T t;


    public MapLocation(double latitude, double longitude) {
        this.latitude = latitude;
        Longitude = longitude;
    }

    public boolean isShowInforWindow() {
        return isShowInforWindow;
    }

    public void setShowInforWindow(boolean showInforWindow) {
        isShowInforWindow = showInforWindow;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public Class<?> getTClass(){
        return t.getClass();
    }

    @Override
    public String toString() {
        return "MapLocation{" +
                "latitude=" + latitude +
                ", Longitude=" + Longitude +
                ", title='" + title + '\'' +
                ", isShowInforWindow=" + isShowInforWindow +
                ", t=" + t +
                '}';
    }
}
