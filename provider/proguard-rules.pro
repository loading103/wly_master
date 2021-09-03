# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
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

-keep class com.daqsoft.provider.bean.**{
*;
}
-keep class com.daqsoft.provider.network.**{
*;
}
-keep class com.daqsoft.provider.net.**{
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

-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#华为推送
 -keepattributes *Annotation*
 -keepattributes Exceptions
 -keepattributes InnerClasses
 -keepattributes Signature
 -keepattributes SourceFile,LineNumberTable
 -keep class com.hianalytics.android.**{*;}
 -keep class com.huawei.updatesdk.**{*;}
 -keep class com.huawei.hms.**{*;}
 #小米
 -dontwarn com.xiaomi.push.**
  -keep class com.xiaomi.push.** { *; }
  -dontwarn com.coloros.mcsdk.**
  -keep class com.coloros.mcsdk.** { *; }
 #oppo
  -dontwarn com.heytap.**
  -keep class com.heytap.** { *; }

  -dontwarn com.mcs.**
  -keep class com.mcs.** { *; }
   #vivo
  -dontwarn com.vivo.push.**
  -keep class com.vivo.push.**{*; }
  -keep class com.vivo.vms.**{*; }