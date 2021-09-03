package com.daqsoft.servicemodule.uitils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.daqsoft.baselib.utils.SPUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @description: 高德地图获取当前位置
 * @author: 黄熙
 * @date 2018/5/7 9:17
 * @version: 1.0.0
 * @since: JDK 1.8
 */
public class GaoDeLocation {
    /**
     * 声明AMapLocationClient类对象
     */
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private OnGetCurrentLocationLisner lisner = null;

    public interface OnGetCurrentLocationLisner {
        void onResult(String result, double lat, double lon, String adcode, String city, String cityCode);

        void onError(String errorMsg);
    }

    public void init(Context context, OnGetCurrentLocationLisner lisner) {
        this.lisner = lisner;
        // 初始化定位
        mLocationClient = new AMapLocationClient(context);
        // 设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);

        if (mLocationOption.isOnceLocationLatest()) {
            mLocationOption.setOnceLocationLatest(true);
            // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }

        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(false);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    public static String sHA1(Context context) throws Exception {
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString =
                        Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1) hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 停止定位并销毁
     */
    public void stop() {
        if (null != mLocationClient) {
            // 停止定位后，本地定位服务并不会被销毁
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    /**
     * 声明定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    // 定位成功回调信息，设置相关消息获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLocationType();
                    //获取纬度
                    amapLocation.getLatitude();
                    //获取经度
                    amapLocation.getLongitude();
                    //获取精度信息
                    amapLocation.getAccuracy();
                    //
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    //定位时间
                    df.format(date);
                    //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getAddress();
                    //国家信息
                    amapLocation.getCountry();
                    //省信息
                    amapLocation.getProvince();
                    //城市信息
                    amapLocation.getCity();
                    //城区信息
                    amapLocation.getDistrict();
                    //街道信息
                    amapLocation.getStreet();
                    //街道门牌号信息
                    amapLocation.getStreetNum();
                    //城市编码
                    amapLocation.getCityCode();
                    //地区编码
                    amapLocation.getAdCode();
                    //获取当前定位点的AOI信息
                    amapLocation.getAoiName();

                    amapLocation.getSpeed();
                    if (lisner != null) {
                        Log.i("数据",
                                amapLocation.getAddress() + amapLocation.getLatitude() + "amapLocation.getLongitude()" +
                                        "=" + amapLocation.getLongitude() + "+amapLocation.getAdCode()=" + amapLocation.getAdCode());
                        String adCode = amapLocation.getAdCode();
                        saveAddressInfoToLocal(amapLocation, adCode);
                        if(amapLocation.getAddress()!=null){
                            SPUtils.getInstance().put(SPUtils.Config.ADDRESS, amapLocation.getAddress());
                        }
                        lisner.onResult(amapLocation.getAddress(),
                                amapLocation.getLatitude(), amapLocation.getLongitude(),
                                amapLocation.getAdCode(),amapLocation.getCity(),amapLocation.getCityCode());
                    }
                } else {
                    // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError",
                            "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo"
                                    + ":" + amapLocation.getErrorInfo());
                    if (lisner != null) {
                        lisner.onError(amapLocation.getErrorInfo());
                    }
                }
            }
        }
    };

    /**
     *  保存定位信息到本地
     * @param amapLocation
     * @param adCode
     */
    private void saveAddressInfoToLocal(AMapLocation amapLocation, String adCode) {
        if (adCode!=null){
            SPUtils.getInstance().put(SPUtils.Config.AD_CODE, amapLocation.getAdCode());
        }
        SPUtils.getInstance().put(SPUtils.Config.LATITUDE,String.valueOf(amapLocation.getLatitude()));
        SPUtils.getInstance().put(SPUtils.Config.LONGITUDE,String.valueOf(amapLocation.getLongitude()));
    }


    /**
     * 计算两个点之间的距离
     *
     * @param startLat 开始纬度
     * @param startLon 开始经度
     * @param endLat   结果纬度
     * @param endLon   结果经度
     * @return 距离
     */
    public static String CalculateLineDistance(String startLat, String startLon, String endLat, String endLon) {
        String result = "";
        if (startLat != null && startLat != "" && startLon != null && startLon != ""
                && endLat != null && endLat != "" && endLon != null && endLon != "") {
            LatLng startLatLng = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLon));
            LatLng endLatLng = new LatLng(Double.parseDouble(endLat), Double.parseDouble(endLon));
            Float distance = AMapUtils.calculateLineDistance(startLatLng, endLatLng);
            if (distance > 1000) {
                DecimalFormat df = new DecimalFormat("0.00");
                result = df.format(distance / 1000) + "KM";
            } else {
                result = distance + "M";
            }
        } else {
//            LogUtil.d("计算距离", "数据异常");
        }
        return result;
    }

}
