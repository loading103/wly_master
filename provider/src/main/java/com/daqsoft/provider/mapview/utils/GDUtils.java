package com.daqsoft.provider.mapview.utils;

import android.content.Context;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;

/**
 * Created by huangx on 2017/3/1.
 * 高德地图转换类
 * @author MouJunFeng
 * @time 2018-3-14
 * @since JDK 1.8
 * @version 1.0.0
 */

public class GDUtils {
    /*
	 * GPS坐标转换为高德地图坐标
	 * 输入GPS坐标，单位度，数据类型double，参数一为Lat,参数二为Lng
	 * 输出高德地图坐标，单位度，数据类型double[]，参数一为Lat,参数二为Lng
	 *
	 * */
    private static double pi = Math.PI;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;

    /**
     * 坐标换算
     * @param wgLat 纬度
     * @param wgLng 经度
     * @return
     */
    public static double[] transLatLng(double wgLat, double wgLng) {
        double[] ds = new double[2];
        double dLat = transLat(wgLng - 105.0, wgLat - 35.0, pi);
        double dLng = transLng(wgLng - 105.0, wgLat - 35.0, pi);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        ds[0] = wgLat - dLat;
        ds[1] = wgLng - dLng;
        return ds;
    }
    /**
     * 坐标换算
     * @param x 坐标
     * @param y 坐标
     * @return
     */
    private static double transLat(double x, double y, double pi) {
        double ret;
        ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    /**
     * 坐标换算
     * @param x 坐标
     * @param y 坐标
     *          @param  pi 密度
     * @return
     */
    private static double transLng(double x, double y, double pi) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }
    /**
     * 坐标换算
     * @param wgLat 坐标
     * @param wgLng 坐标
     * @return
     */
    public static double[] transLatLngToGD(double wgLat, double wgLng) {
        double[] ds = new double[2];
        double dLat = transLat(wgLng - 105.0, wgLat - 35.0, pi);
        double dLng = transLng(wgLng - 105.0, wgLat - 35.0, pi);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        ds[0] = wgLat + dLat;
        ds[1] = wgLng + dLng;
        return ds;
    }

    /**
     * 把GPS转换为高德（高德官网SDK）一般不建议用这种
     * @param latLng
     * @return
     */
    public static LatLng fromGpsToAmap(LatLng latLng , Context context){
        CoordinateConverter converter  = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 DPoint类型
        converter.coord(latLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
}
