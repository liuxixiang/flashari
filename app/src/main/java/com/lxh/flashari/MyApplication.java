package com.lxh.flashari;

import android.app.Application;

import com.lxh.flashari.utils.Logger;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init();
    }
}
