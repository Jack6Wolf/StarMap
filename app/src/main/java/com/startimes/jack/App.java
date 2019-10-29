package com.startimes.jack;

import android.app.Application;

import com.startimes.startmap.StarMapSdk;

/**
 * @author jack
 * @since 2019/9/3 17:42
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StarMapSdk.init(this);
    }
}
