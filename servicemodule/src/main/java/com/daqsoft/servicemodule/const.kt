package com.daqsoft.servicemodule

import com.daqsoft.android.scenic.servicemodule.R

/**
 * desc :常量
 * @author 江云仙
 * @date 2020/4/9 9:55
 */
//火车城市查询
const val SERVICE_TRAIN="service_train"
//长途汽车城市查询
const val SERVICE_SUBWAY="service_subway"
//飞机城市查询
const val SERVICE_PLANE="service_plane"
//天气地址
const val WEATHER_URL="https://p-ued.daqsoft.com/weapp/weather/#/weather"
//查艺考
const val QUERY_EXAM="http://kj.scc.org.cn/GradingSearch.aspx?mid=5#top"
//网页跳转类型（天气查询 艺术基金）
const val WEB_WEATHER="web_weather"
const val WEB_EXAM="web_exam"
const val SERVICE_AD="service_ad"
//城市选择resultcode值
const val CHOOSE_CITY_START_RESULT_CODE=1001
const val CHOOSE_CITY_END_RESULT_CODE=1002
//游前服务
val TRAVEL_TITLE= arrayOf("机票","火车票","导游","旅行社","线路","攻略","空气质量")
val TRAVEL_COLOR_TYPE= arrayOf("blue","purple","yellow","blue","green","red","blue")
val TRAVEL_INTRODUCE= arrayOf("折扣机票","抢票有道","快捷查询","随心出发","推荐行程","游玩锦囊","健康出行")
val TRAVEL_ICON= arrayOf(R.mipmap.service_icon_airplane,R.mipmap.service_icon_train,R.mipmap.service_icon_guide,R.mipmap.service_icon_agency,R.mipmap.service_icon_line,R.mipmap.service_icon_strategy,R.mipmap.service_icon_air)
////交通服务
//val TRANSPORT_TITLE= arrayOf("租车","景区直通车","停车场")
//val TRANSPORT_COLOR_TYPE= arrayOf("purple","green","yellow")
//val TRANSPORT_INTRODUCE= arrayOf("方便出行","景区直达","停车无忧")
//val TRANSPORT_ICON= arrayOf(R.mipmap.service_icon_rent_car,R.mipmap.service_icon_direct_bus,R.mipmap.service_icon_park)

////交通服务
val TRANSPORT_TITLE= arrayOf("租车","停车场")
val TRANSPORT_COLOR_TYPE= arrayOf("purple","yellow")
val TRANSPORT_INTRODUCE= arrayOf("方便出行","停车无忧")
val TRANSPORT_ICON= arrayOf(R.mipmap.service_icon_rent_car,R.mipmap.service_icon_park)
////文旅助手
val TOUR_TITLE= arrayOf("景区导览","找厕所","识花君","AR","美食")
val TOUR_COLOR_TYPE= arrayOf("green","yellow","purple","blue","red")
val TOUR_INTRODUCE= arrayOf("快速抵达","智能服务","识你所见","场景交互","当地特色")
val TOUR_ICON= arrayOf(R.mipmap.service_icon_tour,R.mipmap.service_icon_toliet,R.mipmap.service_icon_scan,R.mipmap.service_icon_ar,R.mipmap.service_icon_food)
//公共服务
val PUBLIC_TITLE= arrayOf("投诉","一键求助")
val PUBLIC_INTRODUCE= arrayOf("有查必究","应急救援")
val PUBLIC_COLOR_TYPE= arrayOf("red","yellow")
val PUBLIC_ICON= arrayOf(R.mipmap.service_icon_complain,R.mipmap.service_icon_sos)


//游前服务
val TRAVEL_TITLE_WS= arrayOf("机票","火车票","导游","旅行社","线路","攻略")
val TRAVEL_COLOR_TYPE_WS= arrayOf("blue","purple","yellow","blue","green","red")
val TRAVEL_INTRODUCE_WS= arrayOf("折扣机票","抢票有道","快捷查询","随心出发","推荐行程","游玩锦囊")
val TRAVEL_ICON_WS= arrayOf(R.mipmap.service_icon_airplane,R.mipmap.service_icon_train,R.mipmap.service_icon_guide,R.mipmap.service_icon_agency,R.mipmap.service_icon_line,R.mipmap.service_icon_strategy,R.mipmap.service_icon_air)
//交通服务
val TRANSPORT_TITLE_WS= arrayOf("租车","停车场")
val TRANSPORT_COLOR_TYPE_WS= arrayOf("purple","yellow")
val TRANSPORT_INTRODUCE_WS= arrayOf("方便出行","停车无忧")
val TRANSPORT_ICON_WS= arrayOf(R.mipmap.service_icon_rent_car,R.mipmap.service_icon_park)
////文旅助手
val TOUR_TITLE_WS= arrayOf("景区导览","找厕所","识花君","美食")
val TOUR_COLOR_TYPE_WS= arrayOf("green","yellow","purple","red")
val TOUR_INTRODUCE_WS= arrayOf("快速抵达","智能服务","识你所见","当地特色")
val TOUR_ICON_WS= arrayOf(R.mipmap.service_icon_tour,R.mipmap.service_icon_toliet,R.mipmap.service_icon_scan,R.mipmap.service_icon_food)
//公共服务
val PUBLIC_TITLE_WS= arrayOf("一键求助","涉未成年人举报热线","文化市场举报平台")
val PUBLIC_INTRODUCE_WS= arrayOf("应急救援","","")
val PUBLIC_COLOR_TYPE_WS= arrayOf("yellow","green","purple")
val PUBLIC_ICON_WS= arrayOf(R.mipmap.service_icon_sos,R.mipmap.service_icon_18,R.mipmap.service_icon_culture_market)
