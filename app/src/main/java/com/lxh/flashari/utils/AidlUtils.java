package com.lxh.flashari.utils;

import android.content.Context;
import android.os.IBinder;

import com.lxh.flashari.service.AidiCallback;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;

public class AidlUtils {
    public static  void useOperateWifiAidl(Context context, AidiCallback<IOperateWifiAidl> callback) {
        IBinder iBinder = Andromeda.with(context).getRemoteService(IOperateWifiAidl.class);
        if (null == iBinder) {
            callback.onFailed(new Throwable("IBinder is null"));
            return;
        }
        IOperateWifiAidl operateWifi = IOperateWifiAidl.Stub.asInterface(iBinder);
        if(operateWifi == null) {
            callback.onFailed(new Throwable("IOperateWifiAidl is IBinder is null"));
        }
        callback.onSucceed(operateWifi);
    }
}
