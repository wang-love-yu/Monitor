package com.qimai;

import android.app.Application;
import android.util.Log;

import com.qimai.qmmonitor.MonitorUtils;

public class App extends Application {

    private static final String TAG = "App";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        MonitorUtils.Companion.init(this);
    }
}
