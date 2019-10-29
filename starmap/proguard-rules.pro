-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,Annotation,EnclosingMethod,MethodParameters
-keep class **.R$* {
*;
}

-keep class com.startimes.startmap.location.StarLocation {
    public <fields>;
    public <methods>;
}
-keep class  com.startimes.startmap.location.StarLocationListener {
     public <fields>;
     public <methods>;
}
-keep class  com.startimes.startmap.StarMapSdk {
     public <fields>;
     public <methods>;
}
-keep class  com.startimes.startmap.map.StarMapUtils {
     public <fields>;
     public <methods>;
}
-keep class  com.startimes.startmap.map.MapPlace {
     public <fields>;
     public <methods>;
}

#谷歌地图
-keep class com.google.android.gms.** { *;}
-dontwarn com.google.android.gms.*

#百度sdk
-libraryjars libs/BaiduLBS_Android.jar
-dontwarn com.baidu.*
-keep class com.baidu.** { *;}