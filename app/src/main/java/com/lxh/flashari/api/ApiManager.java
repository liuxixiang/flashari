package com.lxh.flashari.api;

import android.util.Log;

import com.lxh.flashari.utils.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    public static final String BASE_PATH = "http://flashair";
    public static final String BASE_WIFI_PATH = "http://flashair";
    private static final ApiManager INSTANCE = new ApiManager();

    private ApiService mApiService;
    private WifiApiService mWifiApiService;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    public static ApiManager getInstance() {
        return INSTANCE;
    }

    private ApiManager() {
        init();
    }

    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.tag("OkHttp").e(message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = builder
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_PATH)
                .client(mOkHttpClient)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
        mWifiApiService = mRetrofit.newBuilder().baseUrl(BASE_WIFI_PATH).build().create(WifiApiService.class);
    }

    public ApiService getApiService() {
        return mApiService;
    }

    public WifiApiService getWifiApiService() {
        return mWifiApiService;
    }
}