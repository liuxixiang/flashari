package com.lxh.flashari.service;

import android.os.IInterface;

public interface AidiCallback<T extends IInterface> {
    void onSucceed(T t);

    void onFailed(Throwable throwable);
}
