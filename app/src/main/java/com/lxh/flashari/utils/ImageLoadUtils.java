package com.lxh.flashari.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.lxh.flashari.MyApplication;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.rxjava.RxJavaUtils;

public class ImageLoadUtils {

    public static void load(Context context, String url, ImageView imageView) {
        ReactiveNetwork.observeNetworkConnectivity(MyApplication.getInstance()).compose(RxJavaUtils.io_main()).subscribe(new CustomObserver<Connectivity>() {
            @Override
            public void onNext(Connectivity connectivity) {
                super.onNext(connectivity);
                Log.e("network", "onNext====");
                Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .into(imageView);
            }
        });
    }
}
