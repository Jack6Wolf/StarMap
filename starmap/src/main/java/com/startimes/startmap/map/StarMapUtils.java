package com.startimes.startmap.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * SDK地图工具类
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/4 15:22
 */
public class StarMapUtils {

    /**
     * 百度地图
     */
    public static final String BAIDU = "com.baidu.BaiduMap";
    /**
     * google地图
     */
    public static final String GOOGLE = "com.google.android.apps.maps";
    /**
     * 腾讯地图
     */
    public static final String TENCENT = "com.tencent.map";
    /**
     * 高德地图
     */
    public static final String GAODE = "com.autonavi.minimap";

    /**
     * 设备安装地图的集合（剔除腾讯地图）
     */
    private static String[] mapAppPkgs = {GOOGLE, BAIDU, GAODE};
    private static final String TAG = "StarMap";

    /**
     * 是否支持谷歌服务
     *
     * @return 返回支持结果 <br>{@code true}:支持 {@code false}:不支持
     */
    public static boolean isGooglePlayServicesAvailable(@NonNull Context context) {
        GoogleApiAvailability googleApiInstance = GoogleApiAvailability.getInstance();
        if (googleApiInstance == null)
            return false;
        int googlePlayServicesAvailable = googleApiInstance.isGooglePlayServicesAvailable(context);
        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * 检测地图应用是否安装(目前只支持检测谷歌、百度、高德、腾讯)
     *
     * @return 返回手机安装的地图的包名集合
     */
    @NonNull
    public static List<String> getDeviceMapApps(@NonNull Context context) {
        List<String> list = new ArrayList<>();
        if (mapAppPkgs == null)
            return list;
        for (int i = 0; i < mapAppPkgs.length; i++) {
            if (isAppInstalled(context.getApplicationContext(), mapAppPkgs[i])) {
                list.add(mapAppPkgs[i]);
            }
        }
        return list;
    }

    /**
     * 判断手机是否安装某一应用
     *
     * @param packageName 应用包名
     * @return 返回安装结果 <br>{@code true}:已安装 {@code false}:未安装
     */
    private static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        //true为安装了，false为未安装
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 调用百度/谷歌/高德/腾讯地图地址解析
     *
     * @param packageName 地图应用的包名{@link #getDeviceMapApps(Context)}推荐来自于该方法
     * @param place       用户当前位置
     */
    public static void startMap(@NonNull Context context, @NonNull String packageName, @NonNull MapPlace place) {
        switch (packageName) {
            //跳转百度
            case BAIDU:
                invokeBaiDuMap(context, place);
                break;
            //google
            case GOOGLE:
                invokeGoogleMap(context, place);
                break;
            //腾讯
            case TENCENT:
                invokeQQMap(context, place);
                break;
            //高德
            case GAODE:
                invokeAuToNaveMap(context, place);
                break;
            default:
                Log.e(TAG, "You should pass in a valid parameter, and it comes from the getDeviceMapApps() method!");
                break;
        }
    }

    /**
     * 调用谷歌地图
     *
     * @param context 上下文对象
     * @param place   用户当前位置
     */
    private static void invokeGoogleMap(Context context, MapPlace place) {
        try {
            //google.navigation:q=Taronga+Zoo,+Sydney+Australia&mode=b
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("google.navigation:q=");
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            stringBuilder.append(latitude).append(",").append(longitude);

            Uri uri = Uri.parse(stringBuilder.toString());
            Intent intent = new Intent();
            intent.setPackage(GOOGLE);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "activity that can't display the intent.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用百度地图
     *
     * @param context 上下文对象
     * @param place   用户当前位置
     */
    private static void invokeBaiDuMap(Context context, MapPlace place) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("baidumap://map/direction?");
            //"baidumap://map/direction?origin=name:对外经贸大学|latlng:39.98871,116.43234&destination=西直门&coord_type=bd09ll&mode=transit&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            String coordType = place.getCoordType();
            stringBuilder.append("destination=");
            String address = place.getAddress();
            if (!TextUtils.isEmpty(address)) {
                stringBuilder.append("name:").append(address).append("|latlng:");
            }
            stringBuilder.append(latitude).append(",").append(longitude)
                    .append("&coord_type=");
            if (MapPlace.WGS.equals(coordType)) {
                stringBuilder.append("wgs84");
            } else if (MapPlace.GCJ.equals(coordType)) {
                stringBuilder.append("gcj02");
            } else {
                stringBuilder.append("bd09");
            }
            String packageName = context.getPackageName();
            if (!TextUtils.isEmpty(packageName)) {
                stringBuilder.append("&src=").append(packageName);
            }

            Uri uri = Uri.parse(stringBuilder.toString());
            Intent intent = new Intent();
            intent.setPackage(BAIDU);
            intent.setData(uri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "activity that can't display the intent.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 调用高德地图
     *
     * @param context 上下文对象s
     * @param place   用户当前位置
     */
    private static void invokeAuToNaveMap(Context context, MapPlace place) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
//                act=android.intent.action.VIEW
//                cat=android.intent.category.DEFAULT
//                dat=amapuri://route/plan/?dlat=39.98848272&dlon=116.47560823&dname=B&dev=0&t=0
//                pkg=com.autonavi.minimap
            stringBuilder.append("amapuri://route/plan/?");
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            stringBuilder.append("dlat=").append(latitude);
            stringBuilder.append("&dlon=").append(longitude);
            String address = place.getAddress();
            if (!TextUtils.isEmpty(address)) {
                stringBuilder.append("&dname=").append(address);
            }
            stringBuilder.append("&dev=");
            String coordType = place.getCoordType();
            if (MapPlace.GCJ.equals(coordType)) {
                //0:lat和lon是已经加密后的,不需要国测加密
                stringBuilder.append("0");
            } else {
                stringBuilder.append("1");
            }
            stringBuilder.append("&t=0");
            String packageName = context.getPackageName();
            if (!TextUtils.isEmpty(packageName)) {
                stringBuilder.append("&sourceApplication=").append(packageName);
            }

            Uri uri = Uri.parse(stringBuilder.toString());
            Intent intent = new Intent();
            intent.setPackage(GAODE);
            intent.setData(uri);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "activity that can't display the intent.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 调用腾讯地图
     * key:U5FBZ-ANB6S-VL6OD-6YUJW-HOHJF-CQFIB
     *
     * @param context 上下文对象s
     * @param place   用户当前位置
     */
    private static void invokeQQMap(Context context, MapPlace place) {
        try {
            //调起腾讯地图APP，并在图上标注位置，标注名称由腾讯地图自动生成
//            qqmap://map/geocoder?coord=39.916748,116.318221&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("qqmap://map/routeplan?");
            stringBuilder.append("fromcoord=CurrentLocation");
            String address = place.getAddress();
            if (!TextUtils.isEmpty(address)) {
                stringBuilder.append("&to=").append(address);
            }
            stringBuilder.append("&tocoord=");
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            stringBuilder.append(latitude).append(",").append(longitude).append("&referer=")
                    .append("U5FBZ-ANB6S-VL6OD-6YUJW-HOHJF-CQFIB").append("&type=drive");

            Uri uri = Uri.parse(stringBuilder.toString());
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setPackage(TENCENT);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "activity that can't display the intent.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
