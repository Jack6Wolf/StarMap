package com.startimes.startmap.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.startimes.startmap.R;

/**
 * 百度定位API(单例)
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/3 11:48
 */
public class LocationService {
    /**
     * 请在主线程中声明LocationClient类对象，
     */
    private LocationClient client;

    /**
     * 默认参数配置
     */
    private LocationClientOption mOption;
    /**
     * 自定义参数配置
     */
    private LocationClientOption mDiyOption;
    /**
     * 定位超时时间（ms）
     */
    private int timeOut;

    /**
     * 百度定位各错误返回码的具体说明
     */
    public String[] msgLocations;

    private final Object objLock = new Object();

    private static final String TAG = "StarMap";


    private LocationService() {
    }

    private static class LazyHolder {
        private static final LocationService INSTANCE = new LocationService();
    }

    /**
     * 线程安全
     */
    public static final LocationService getInstance() {
        return LazyHolder.INSTANCE;
    }

    /***
     *初始化，设置默认定位配置
     * @param locationContext 该对象初始化需传入Context类型参数。推荐使用getApplicationConext()方法获取全进程有效的Context
     */
    public void init(@NonNull Context locationContext) {
        synchronized (objLock) {
            if (client == null) {
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
                msgLocations = locationContext.getResources().getStringArray(R.array.startmap_location_msg);
                Log.d(TAG, "StarMapSdk init success...");
            }
        }
    }

    /**
     * 是否初始化
     *
     * @return 返回是否初始化结果 <br>{@code true}:已初始化 {@code false}:还未初始化
     */
    public boolean isInit() {
        if (client != null)
            return true;
        return false;
    }

    /***
     *  设置定位监听
     * @param listener {@link BDAbstractLocationListener 实现定位监听。该接口会异步获取定位结果}
     * @return 返回注册监听结果 <br>{@code true}:成功 {@code false}:失败
     */
    public boolean registerListener(BDAbstractLocationListener listener) {
        boolean isSuccess = false;
        if (listener != null) {
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        Log.d(TAG, "registerListener:" + isSuccess);
        return isSuccess;
    }

    /**
     * 移除定位监听
     *
     * @param listener {@link BDAbstractLocationListener 实现定位监听。该接口会异步获取定位结果}
     */
    public void unregisterListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            client.unRegisterLocationListener(listener);
        }
    }

    /***
     * 设置自定义定位配置
     * @param option {@link LocationClientOption 定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等}
     * @return 返回设置配置结果 <br>{@code true}:成功 {@code false}:失败
     */
    public boolean setLocationOption(LocationClientOption option) {
        boolean isSuccess = false;
        if (option != null) {
            if (client.isStarted())
                client.stop();
            mDiyOption = option;
            client.setLocOption(option);
            isSuccess = true;
        }
        Log.d(TAG, "setLocationOption:" + isSuccess);
        return isSuccess;
    }

    /***
     * 默认的定位配置
     */
    private LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setLocationMode(LocationMode.Hight_Accuracy);
            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setCoorType("gcj02");
            //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setScanSpan(0);
            //可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            mOption.setIsNeedLocationDescribe(false);
            //可选，设置是否需要设备方向结果
            mOption.setNeedDeviceDirect(false);
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setLocationNotify(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIgnoreKillProcess(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationDescribe(false);
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.setIsNeedLocationPoiList(false);
            //可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.SetIgnoreCacheException(false);
            //可选，默认false，设置是否开启Gps定位
            mOption.setOpenGps(true);
            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            mOption.setIsNeedAltitude(false);

        }
        return mOption;
    }


    /**
     * 获取自定义定位配置
     *
     * @return 自定义Option设置
     */
    public LocationClientOption getOption() {
        if (mDiyOption == null) {
            mDiyOption = new LocationClientOption();
        }
        return mDiyOption;
    }

    /**
     * 启动定位SDK,调用start()之后只需要等待定位结果自动回调即可。
     * 开发者定位场景如果是单次定位的场景，在收到定位结果之后直接调用stop()函数即可。
     * 如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可
     */
    public void start() {
        synchronized (objLock) {
            if (client != null && !client.isStarted()) {
                client.start();
            }
        }
    }

    /**
     * 停止定位SDK
     */
    public void stop() {
        synchronized (objLock) {
            if (client != null && client.isStarted()) {
                client.stop();
            }
        }
    }

    /**
     * 某些特定的异常环境下重启定位。
     */
    public void reStart() {
        synchronized (objLock) {
            if (client != null && client.isStarted()) {
                client.restart();
            }
        }
    }

    /**
     * 设置定位超时时间
     *
     * @param timeOut 定位超时时间（ms）
     */
    public boolean setTimeOut(int timeOut) {
        boolean isSuccess = false;
        if (client != null) {
            this.timeOut = timeOut;
            isSuccess = true;
        }
        Log.d(TAG, "setTimeOut:" + isSuccess);
        return isSuccess;
    }

    /**
     * 获得定位超时时间
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * 设置定位精度,可选默认设置高精度
     *
     * @param locationMode 定位模式{@link LocationMode#Hight_Accuracy 高精度}<br>{@link LocationMode#Battery_Saving 低功耗}<br>{@link LocationMode#Device_Sensors 仅设备默认高精度}
     */
    public boolean setLocationMode(@Nullable LocationMode locationMode) {
        boolean isSuccess = false;
        if (client != null) {
            LocationClientOption locOption = client.getLocOption();
            if (locOption != null) {
                if (locationMode == null)
                    locationMode = LocationMode.Hight_Accuracy;
                locOption.setLocationMode(locationMode);
                isSuccess = true;
            }
        }
        Log.d(TAG, "setLocationMode:" + isSuccess);
        return isSuccess;
    }

    /**
     * 定位是否已经开启
     *
     * @return 返回是否开启结果 <br>{@code true}:开启 {@code false}:没开启
     */
    public boolean isStart() {
        if (client != null)
            return client.isStarted();
        return false;
    }

    /**
     * 是否开启连续定位
     *
     * @return 返回是否开启结果 <br>{@code true}:开启 {@code false}:没开启
     */
    public boolean isScanSpan() {
        LocationClientOption option = LocationService.getInstance().getOption();
        //开启了连续定位则
        if (option != null && option.getScanSpan() > 0) {
            return true;
        }
        return false;
    }
}
