-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*;}
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

-keepattributes InnerClasses
-keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
-keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
-keep class android.support.v7.widget.ChildHelper { *; }
-keep class android.support.v7.widget.ChildHelper$Bucket { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }

-keep class com.jakewharton.rxbinding2.view.ViewScrollChangeEventObservable { *; }
-keep class com.jakewharton.rxbinding2.view.RxViewKt { *; }
-keep class com.jakewharton.rxbinding2.view.ViewScrollChangeEventObservable$* { *; }
-keep class com.jakewharton.rxbinding2.view.ViewScrollChangeEventObservable$Listener { *; }
-keepattributes Exceptions,InnerClasses
-keep class com.google.android.material.appbar.AppBarLayout.**{
*;
}
-keep class  org.bouncycastle.**{*;}
-keep class com.daqsoft.provider.bean.**{
*;
}
-keep class com.daqsoft.thetravelcloudwithculture.home.bean.**{
*;
}
-keep class com.daqsoft.provider.network.home.bean.**{
*;
}
-keep class com.daqsoft.provider.network.**{
*;
}
-keep class com.daqsoft.provider.net.**{
*;
}
-keep class com.daqsoft.provider.network.net.**{
*;
}
-keep class com.daqsoft.provider.network.comment.beans.**{
*;
}
-keep class com.daqsoft.provider.network.venues.bean.**{
*;
}
-keep class com.daqsoft.venuesmodule.repository.bean.**{
*;
}
-keep class com.daqsoft.travelCultureModule.story.bean.**{
*;
}
# 高德地图
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep   class com.amap.api.services.**{*;}
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#极光推送
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
-keep class com.daqsoft.provider.view.**{
*;
}
