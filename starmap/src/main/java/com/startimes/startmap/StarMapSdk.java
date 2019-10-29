package com.startimes.startmap;

import android.content.Context;
import android.support.annotation.NonNull;

import com.startimes.startmap.location.StarLocation;

/**
 * startimes map sdk整体管理类
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/4 15:16
 */
public class StarMapSdk {
    /**
     * 定位SDK初始化，建议在Application中创建
     *
     * @param context 该对象初始化需传入Context类型参数。推荐使用getApplicationConext()方法获取全进程有效的Context
     */
    public static void init(@NonNull Context context) {
        StarLocation.init(context.getApplicationContext());
    }
}
