package com.lxh.flashari.rxjava;

import io.reactivex.observers.DefaultObserver;

public abstract class CustomObserver<T> extends DefaultObserver<T> {
    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
