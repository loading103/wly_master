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



-keep class com.daqsoft.travelCultureModule.clubActivity.bean.**{
*;
}
-keep class com.daqsoft.travelCultureModule.clubActivity.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.contentActivity.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.country.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.hotel.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.onLineClick.bean.**{
    *;
    }

    -keep class com.daqsoft.travelCultureModule.panoramic.bean.**{
    *;
    }
-keep class com.daqsoft.travelCultureModule.resource.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.search.bean.**{
    *;
}
-keep class com.daqsoft.travelCultureModule.sidetour.bean.**{
*;
}
-keep class com.daqsoft.travelCultureModule.hotActivity.bean.**{
*;
}

-keep class com.daqsoft.travelCultureModule.redblack.bean.**{
*;
}
-dontwarn com.iflytek.speek.**
-keep class com.iflytek.speek.** {*;}
-keepattributes Signature