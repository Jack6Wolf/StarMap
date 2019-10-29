package com.startimes.startmap.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.baidu.location.LocationClientOption;
import com.startimes.startmap.persimmion.PersimionsManager;

/**
 * 定位相关管理类
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/3 11:38
 */
public class StarLocation {


    /**
     * 定位SDK初始化，建议在Application中创建
     *
     * @param context 该对象初始化需传入Context类型参数。推荐使用getApplicationConext()方法获取全进程有效的Context
     */
    public static void init(@NonNull Context context) {
        LocationService.getInstance().init(context);
    }


    /**
     * 判断Gps是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isGpsEnabled(@NonNull Context context) {
        LocationManager lm = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (lm == null)
            return false;
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断定位是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isLocationEnabled(@NonNull Context context) {
        LocationManager lm = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (lm == null)
            return false;
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开Gps设置界面
     */
    public static void openGpsSettings(@NonNull Activity context, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断wifi是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isWifiOpened(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return false;
        return wifiManager.isWifiEnabled();
    }

    /**
     * 打开系统wifi设置界面
     */
    public static void openWifiSettings(@NonNull Activity context, int requestCode) {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 业务层面上还未申请定位权限，直接调用该方法
     *
     * @param context  申请定位权限的页面
     * @param listener 定位结果异步回调{@link StarLocationListener}
     */
    public static void getLocation(@NonNull Context context, @NonNull StarLocationListener listener) {
        if (PersimionsManager.isPersimmions(context.getApplicationContext())) {
            getLocation(listener);
        } else {
            //没有权限
            listener.onLocationResults(StarLocationListener.FAILURE, 707, "没有申请相关权限", null);
        }
    }

    /**
     * 已有定位权限，可直接调用该方法获取定位
     *
     * @param listener 定位结果异步回调{@link StarLocationListener}
     * @throws IllegalStateException 没有初始化
     */
    public static void getLocation(@NonNull StarLocationListener listener) {
        if (LocationService.getInstance().isInit()) {
            LocationService.getInstance().registerListener(new LocationListener(listener));
            LocationService.getInstance().start();
        } else {
            throw new IllegalStateException("Initialization Not Performed,You Should Execute First StarLocation.init()!");
        }
    }

    /**
     * 设置自定义定位配置
     *
     * @param locationOption {@link LocationClientOption 定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等}
     * @throws IllegalStateException 没有初始化
     */
    public static void setLocationOption(@Nullable LocationClientOption locationOption) {
        if (LocationService.getInstance().isInit()) {
            LocationService.getInstance().setLocationOption(locationOption);
        } else {
            throw new IllegalStateException("Initialization Not Performed,You Should Execute First StarLocation.init()!");
        }
    }

    /**
     * 设置定位超时时间
     *
     * @param timeOut 定位超时时间（ms）
     * @throws IllegalStateException 没有初始化
     */
    public static void setTimeOut(int timeOut) {
        if (LocationService.getInstance().isInit()) {
            LocationService.getInstance().setTimeOut(timeOut);
        } else {
            throw new IllegalStateException("Initialization Not Performed,You Should Execute First StarLocation.init()!");
        }
    }

    /**
     * 停止定位SDK(连续定位，建议手动该调用)
     *
     * @throws IllegalStateException 没有初始化
     */
    public static void stopLocation() {
        if (LocationService.getInstance().isInit()) {
            LocationService.getInstance().stop();
        } else {
            throw new IllegalStateException("Initialization Not Performed,You Should Execute First StarLocation.init()!");
        }
    }
}
