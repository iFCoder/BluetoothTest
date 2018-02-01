package com.bftv.seele.bluetoothtest;

import android.app.Application;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public class ToothApplication extends Application{
    private static ToothApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ToothApplication getInstance() {
        return instance;
    }
}
