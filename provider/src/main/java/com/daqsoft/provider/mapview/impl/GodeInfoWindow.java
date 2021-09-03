package com.daqsoft.provider.mapview.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.daqsoft.provider.R;
import com.daqsoft.provider.mapview.MyMapView;


/**
 * 导游高德地图弹出popwindow
 *
 * @author MouJunFeng
 * @version 1.0.0
 * @time 2018-3-14
 * @since JDK 1.8
 */

public class GodeInfoWindow implements AMap.InfoWindowAdapter {
    /**
     * 上下文
     */
    private Activity context;
    /**
     * 回调监听
     */
    private IGuideAudioListener listener;
    /**
     * map控件
     */
    private MyMapView mapView;
    /**
     * 宽度
     */
    private int width;

    /**
     * 设置宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 设置上下文
     *
     * @param context
     */
    public GodeInfoWindow(Activity context) {
        this.context = context;
    }

    /**
     * 设置map控件
     *
     * @param mapView
     */
    public void setMapView(MyMapView mapView) {
        this.mapView = mapView;
    }


    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(IGuideAudioListener listener) {
        this.listener = listener;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_map_my_location, null);
//        if (width > 0) {
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
//            if (lp == null) {
//                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
//                        .LayoutParams
//                        .MATCH_PARENT);
//            }
//            lp.width = DensityUtils.dip2px(context, width);
//            v.setLayoutParams(l  p);
//        }
        render(marker, v);
        return v;
    }

    /**
     * 数据绑定
     *
     * @param marker
     * @param view
     */
    private void render(final Marker marker, View view) {
//        final MarketBean bean = (MarketBean) marker.getObject();
//        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
//        TextView txtContent = (TextView) view.findViewById(R.id.txt_content);
//        SimpleDraweeView imgTag = (SimpleDraweeView) view.findViewById(R.id.img_tag);
//        imgTag.setImageURI(bean.getCover());
//        txtTitle.setText(bean.getName());
//        txtContent.setText(bean.getSummary());
//        TextView txtPalyAudio = (TextView) view.findViewById(R.id.txt_palyAudio);
//        TextView txt_information = (TextView) view.findViewById(R.id.txt_information);
//        ImageView imgClose = (ImageView) view.findViewById(R.id.img_close);
//        TextView txt_map = (TextView) view.findViewById(R.id.txt_scenic_map);
//        TextView txt_daohang = (TextView) view.findViewById(R.id.txt_scenic_daohang);
////        SimpleDraweeView imgTagOther = (SimpleDraweeView) view.findViewById(R.id.img_tag_other);
////        imgTagOther.setImageURI(bean.getCover());
//        TextView txtTitleOther = (TextView) view.findViewById(R.id.txt_title_other);
//        txtTitleOther.setText(bean.getName());
//        LinearLayout llScenic = (LinearLayout) view.findViewById(R.id.ll_guide_dialog_scenic);
//        LinearLayout llOther = (LinearLayout) view.findViewById(R.id.ll_guide_dialog_other);
//        TextView tvScenicDistance = (TextView) view.findViewById(R.id.tv_dialog_distance);
//        TextView tvOtherDistance = (TextView) view.findViewById(R.id.tv_dialog_other_distance);
//        TextView tv_guid = (TextView) view.findViewById(R.id.tv_guid);
//        tv_guid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    LatLng latLng = SelfLatLng.getSelfLatLng();
//                    if (Utils.isnotNull(bean.getLatitude())
//                            && Utils.isnotNull(bean.getLongitude())
//                            && Utils.isnotNull(latLng.latitude)
//                            && Utils.isnotNull(latLng.longitude)) {
//
//                        MapNaviUtils.isMapNaviUtils(context, latLng.latitude, latLng.longitude,
//                                "我的位置", bean.getLatitude(), bean
//                                        .getLongitude(), Utils.isnotNull(bean.getAddress()) ?
//                                        Utils.tr(bean.getAddress()) : "目的地");
//                    } else {
//                        ShowToast.showText(context, "数据异常，无法进行导航操作");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        tvScenicDistance.getBackground().setAlpha(125);
//        // 语音不为空再显示，否则隐藏
//        if (Utils.isnotNull(bean.getAudioPath())) {
//            txtPalyAudio.setVisibility(View.VISIBLE);
//        } else {
//            txtPalyAudio.setVisibility(View.GONE);
//        }
//        // 如果是景区
//        if (bean.getSourceType().equals("sourceType_1")) {
//            llScenic.setVisibility(View.VISIBLE);
//            llOther.setVisibility(View.GONE);
//            tvScenicDistance.setText(SelfLatLng.CalculateLineDistance(bean.getLatitude(), bean
//                    .getLongitude()));
//        } else {
//            // 否则
//            llOther.setVisibility(View.VISIBLE);
//            llScenic.setVisibility(View.GONE);
//            tvOtherDistance.setText("距你" + SelfLatLng.CalculateLineDistance(bean.getLatitude(),
//                    bean.getLongitude()) + "," + bean.getAddress());
//        }
//        // 判断是否有景区导览，否则隐藏
//        if (Utils.isnotNull(bean.getLinkType())) {
//            txt_map.setVisibility(View.VISIBLE);
//        } else {
//            txt_map.setVisibility(View.GONE);
//        }
//        // 播放语音讲解
//        txtPalyAudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) {
//                    listener.palyAudio(bean);
//                }
//            }
//        });
//        // 详情查看
//        txt_information.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SpotDetailActivity.class);
//                intent.putExtra(Constant.IntentKey.BEAN, bean);
//                context.startActivity(intent);
//            }
//        });
//        // 关闭弹框
//        imgClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (marker.isInfoWindowShown()) {
//                    marker.hideInfoWindow();
//                }
//            }
//        });
//        // 导航
//        txt_daohang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!Utils.gethaveNet(context)) {
//                    ShowToast.showText(context, "网络错误，无法进行导航操作");
//                } else {
//                    LatLng latLng = SelfLatLng.getSelfLatLng();
//                    if (Utils.isnotNull(bean.getLatitude())
//                            && Utils.isnotNull(bean.getLongitude())
//                            && Utils.isnotNull(latLng.latitude)
//                            && Utils.isnotNull(latLng.longitude)) {
//
//                        MapNaviUtils.isMapNaviUtils(context, latLng.latitude, latLng.longitude,
//                                "我的位置", bean.getLatitude(), bean
//                                        .getLongitude(), Utils.isnotNull(bean.getAddress()) ?
//                                        Utils.tr(bean.getAddress()) : "目的地");
//                    } else {
//                        ShowToast.showText(context, "数据异常，无法进行导航操作");
//                    }
//                }
//            }
//        });
//        // 景区地图查看
//        txt_map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = null;
//                //  等于1，系统内部的跳转，返回的是id
//                if (Constant.LINKTYPE_SYSTEM.equals(bean.getLinkType())
//                        && Utils.isnotNull(bean.getLink())) {
//                    intent = new Intent(context, MapInformationActivity.class);
//                    // 网络请求根地址
//                    intent.putExtra("url", Config.BASEURL);
//                    // 网页页面根地址
//                    intent.putExtra("htmlurl", Config.HTMLURL);
//                    // 语言编码
//                    intent.putExtra("lang", Config.LANG);
//                    // 站点编码
//                    intent.putExtra("sitecode", Config.SITECODE);
//                    // 地区编码
//                    intent.putExtra("region", Config.REGION);
//                    // 微信的账号APPID
//                    intent.putExtra("appid", Config.WECHAT_APPID);
//                    // 地区名称
//                    intent.putExtra("city", Config.CITY_NAME);
//                    // 当前地区的经度
//                    intent.putExtra("lat", Config.COMMON_LAT);
//                    // 当前地区的纬度
//                    intent.putExtra("lng", Config.COMMON_LNG);
//                    // 当前地区的相关介绍信息
//                    intent.putExtra("about", Config.about);
//                    intent.putExtra(Constant.IntentKey.ID, Integer.parseInt(bean.getLink()));
//                    context.startActivity(intent);
//                } else if (Constant.LINKTYPE_OUTSIDE.equals(bean.getLinkType())) {
//                    // 等于2，跳转的是外部配置，返回的是url,进行网页跳转
//                    intent = new Intent();
//                    intent.putExtra("htmlUrl", bean.getLink());
//                    intent.putExtra("isShare", false);
//                    intent.putExtra("tag", false);
//                    intent.setClassName(context, "com.daqsoft.android.base.WebActivity");
//                    context.startActivity(intent);
//                } else {
//                    ShowToast.showText(context, "数据异常，请稍后重试！");
//                }
//            }
//        });
//        //        imgTag.setImageURI(bean.get);

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public interface IGuideAudioListener<T> {
        void palyAudio(T t);
    }
}
