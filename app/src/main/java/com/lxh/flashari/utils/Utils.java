package com.lxh.flashari.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lxh.flashari.MyApplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class Utils {

    private static WindowManager windowManager;

    private static WindowManager getWindowManager(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    public static String accessToFlashAir(String uri) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String result = null;
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = inputStreamToString(in);
            in.close();
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }

    private static String inputStreamToString(InputStream stream)
            throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int num;
        while (0 < (num = reader.read(buffer))) {
            sb.append(buffer, 0, num);
        }
        return sb.toString();
    }

    public static String convertByteWithUnit(long b) {
        if (b > 1024 * 1024 * 1024) {
            return String.format(Locale.ENGLISH, "%.2fGB", (float) b
                    / (1024 * 1024 * 1024));
        } else if (b > 1024 * 1024) {
            return String.format(Locale.ENGLISH, "%.2fMB", (float) b / (1024 * 1024));
        } else if (b > 1024) {
            return String.format(Locale.ENGLISH, "%.2fkB", (float) b / 1024);
        } else {
            return b + "byte";
        }
    }

    public static Application getApp() {
        return MyApplication.getInstance();
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager(context).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int dp2px(Context context, float dp) {
        return (int) (getDensity(context) * dp + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        return (int) (px / getDensity(context) + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        return (int) (getFontDensity(context) * sp + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        return (int) (px / getFontDensity(context) + 0.5f);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }
}
