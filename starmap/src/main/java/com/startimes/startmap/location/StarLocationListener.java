package com.startimes.startmap.location;


import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;

/**
 * 定位结果异步回调
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/3 15:12
 */
public interface StarLocationListener {
    /**
     * 定位结果成功
     */
    int SUCCESS = 200;
    /**
     * 定位结果失败
     */
    int FAILURE = 500;
    /**
     * 定位超时
     */
    int TIMEOUT = 800;

    /**
     * 定位结果<br>
     * BDLocation.getLocType方法来获取本次定位的错误返回码:<br>
     * 61 GPS定位结果          GPS定位成功<br>
     * 62 定位失败             无法获取有效定位依据，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位<br>
     * 63 网络异常             没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位<br>
     * 66 离线定位结果         通过requestOfflineLocaiton调用时对应的返回结果<br>
     * 67 离线定位失败<br>
     * 161 网络定位结果        网络定位成功<br>
     * 162 请求串密文解析失败   一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件<br>
     * 167 服务端定位失败       请您检查是否禁用获取位置信息权限，尝试重新请求定位<br>
     * 505 AK不存在或者非法     请按照说明文档重新申请AK<br>
     * 707  没有定位权限        请申请权限<br>
     * <p>
     *
     * @param resultCode 定位结果CODE{@link #SUCCESS 成功}{@link #FAILURE 失败}{@link #TIMEOUT 超时}
     * @param msgCode    定位的错误返回码，来自于BaiduLocation
     * @param msg        定位的错误返回详细说明
     * @param bdLocation {@link BDLocation 回调的百度坐标类，内部封装经纬度、半径等信息。}
     */
    void onLocationResults(int resultCode, int msgCode, String msg, @Nullable BDLocation bdLocation);


    /**
     * 用于细化定位失败原因，开发者可以根据具体原因从产品角度提示用户进行相关操作，达到定位成功的目的，
     * 也有助于进一步分析定位失败原因。<br>
     * 详细说明如下：<br>
     * 161	    1	定位成功，建议您打开GPS<br>
     * 161	    2	定位成功，建议您打开Wi-Fi<br>
     * 67	    3	定位失败，请您检查您的网络状态<br>
     * 62	    4	定位失败，无法获取任何有效定位依据<br>
     * 62	    5	定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位<br>
     * 62	    6	定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试<br>
     * 62	    7	定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试<br>
     * 167 	    8	定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限<br>
     * 62	    9	定位失败，无法获取任何有效定位依据<br>
     * <p>
     *
     * @param locType           当前定位类型
     * @param diagnosticType    诊断类型(1-9)
     * @param diagnosticMessage 具体的诊断信息释义
     */
    void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage);
}
