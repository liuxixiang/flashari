package com.lxh.flashari.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import org.qiyi.video.svg.IPCCallback.Stub;
public abstract class BaseCallback extends Stub {
    private Handler handler = new Handler(Looper.getMainLooper());

    public BaseCallback() {
    }

    public final void onSuccess(final Bundle result) {
        this.handler.post(() -> onSucceed(result));
    }

    public final void onFail(final String reason) {
        this.handler.post(() -> onFailed(reason));
    }

    public abstract void onSucceed(Bundle bundle);

    public abstract void onFailed(String bundle);
}