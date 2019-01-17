package com.lxh.flashari.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import org.qiyi.video.svg.IPCCallback.Stub;
public abstract class BaseCallback extends Stub {
    private Handler handler = new Handler(Looper.getMainLooper());

    public BaseCallback() {
    }

    public final void onSuccess(final Bundle result) throws RemoteException {
        this.handler.post(new Runnable() {
            public void run() {
                onSucceed(result);
            }
        });
    }

    public final void onFail(final String reason) throws RemoteException {
        this.handler.post(new Runnable() {
            public void run() {
                onFailed(reason);
            }
        });
    }

    public abstract void onSucceed(Bundle var1);

    public abstract void onFailed(String var1);
}