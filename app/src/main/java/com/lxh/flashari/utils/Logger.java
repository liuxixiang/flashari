package com.lxh.flashari.utils;


import com.lxh.flashari.BuildConfig;

import timber.log.Timber;

public class Logger {

    public static void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Timber.Tree tag(String tag) {
        return Timber.tag(tag);
    }

    public static void v(String s, Object... args) {
        Timber.v(s, args);
    }

    public static void d(String s, Object... args) {
        Timber.d(s, args);
    }

    public static void i(String s, Object... args) {
        Timber.i(s, args);
    }

    public static void w(String s, Object... args) {
        Timber.w(s, args);
    }

    public static void e(String s, Object... args) {
        Timber.e(s, args);
    }

    public static void wtf(String s, Object... args) {
        Timber.wtf(s, args);
    }

}
