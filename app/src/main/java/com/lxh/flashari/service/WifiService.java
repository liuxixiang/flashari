package com.lxh.flashari.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.utils.Logger;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;

public class WifiService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Andromeda.registerRemoteService(IOperateWifiAidl.class, OperateWifiImpl.getInstance().asBinder());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void getFileCount() {
        ApiManager.getInstance().sendHttp(ApiManager.getInstance().getWifiApiService().getImgsNum("DCIM"),
                new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        super.onNext(s);
                        Logger.tag("network").e("getFileCount===" + s);
                    }
                });
    }
}
