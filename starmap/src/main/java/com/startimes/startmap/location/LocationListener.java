package com.startimes.startmap.location;

import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;


/**
 * 定位结果内部处理类
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/3 14:05
 */
public final class LocationListener extends BDAbstractLocationListener {
    private StarLocationListener mListener;
    /**
     * 错误信息长度
     */
    private final int MSG_SIZE = 8;

    private Handler mHandler;
    private boolean isTimeOut;

    public LocationListener(StarLocationListener listener) {
        mListener = listener;
        //连续定位则取消超时机制
        if (!LocationService.getInstance().isScanSpan()) {
            int timeOut = LocationService.getInstance().getTimeOut();
            //设置了超时才回调超时结果，否则不回调超时
            if (timeOut > 0) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    Looper.prepare();
                }
                mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            if (!isTimeOut) {
                                isTimeOut = true;
                                mListener.onLocationResults(StarLocationListener.TIMEOUT, 0, "", null);
                                //仅定位一次则解绑移除
                                if (!LocationService.getInstance().isScanSpan())
                                    unRegister();
                            }
                        }
                    }
                }, timeOut);
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    Looper.loop();
                }
            }
        }
    }

    /**
     * 定位结果
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (mListener != null) {
            int locType = bdLocation.getLocType();
            String[] msgLocations = LocationService.getInstance().msgLocations;
            if (msgLocations != null && msgLocations.length >= MSG_SIZE) {
                switch (locType) {
                    //GPS定位成功
                    case BDLocation.TypeGpsLocation:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.SUCCESS, locType, msgLocations[0], bdLocation);
                        }
                        break;
                    //无法获取有效定位依据，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位
                    case BDLocation.TypeCriteriaException:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, msgLocations[1], bdLocation);
                        }
                        break;
                    //没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位
                    case BDLocation.TypeNetWorkException:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, msgLocations[2], bdLocation);
                        }
                        break;
                    //通过requestOfflineLocaiton调用时对应的返回结果
                    case BDLocation.TypeOffLineLocation:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.SUCCESS, locType, msgLocations[3], bdLocation);
                        }
                        break;
                    //离线定位失败
                    case BDLocation.TypeOffLineLocationFail:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, "", bdLocation);
                        }
                        break;
                    //网络定位成功
                    case BDLocation.TypeNetWorkLocation:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.SUCCESS, locType, msgLocations[4], bdLocation);
                        }
                        break;
                    //一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件
                    case BDLocation.TypeServerDecryptError:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, msgLocations[5], bdLocation);
                        }
                        break;
                    //请您检查是否禁用获取位置信息权限，尝试重新请求定位
                    case BDLocation.TypeServerError:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, msgLocations[6], bdLocation);
                        }
                        break;
                    //请按照说明文档重新申请AK
                    case BDLocation.TypeServerCheckKeyError:
                        if (!isTimeOut) {
                            isTimeOut = true;
                            mListener.onLocationResults(StarLocationListener.FAILURE, locType, msgLocations[7], bdLocation);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (!LocationService.getInstance().isScanSpan())
            unRegister();
    }

    /**
     * 细化定位失败原因
     */
    @Override
    public void onLocDiagnosticMessage(int i, int i1, String s) {
        super.onLocDiagnosticMessage(i, i1, s);
        if (mListener != null) {
            mListener.onLocDiagnosticMessage(i, i1, s);
        }
    }

    /**
     * 定位完无论成功或失败都停止解绑
     */
    private void unRegister() {
        LocationService.getInstance().stop();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        LocationService.getInstance().unregisterListener(this);
    }
}
