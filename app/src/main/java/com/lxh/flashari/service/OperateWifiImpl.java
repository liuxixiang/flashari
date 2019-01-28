package com.lxh.flashari.service;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lxh.flashari.GlideApp;
import com.lxh.flashari.MyApplication;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.common.event.EventConstants;
import com.lxh.flashari.glide.ProgressInterceptor;
import com.lxh.flashari.model.OperateWifiModel;
import com.lxh.flashari.model.OperateWifiModelImpl;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;
import org.qiyi.video.svg.IPCCallback;
import org.qiyi.video.svg.event.Event;

import java.util.ArrayList;

public class OperateWifiImpl extends IOperateWifiAidl.Stub {
    private OperateWifiModel mOperateWifiModel;

    private static OperateWifiImpl instance;

    public static OperateWifiImpl getInstance() {
        if (null == instance) {
            synchronized (OperateWifiImpl.class) {
                if (null == instance) {
                    instance = new OperateWifiImpl();
                }
            }
        }
        return instance;
    }

    private OperateWifiImpl() {
        mOperateWifiModel = OperateWifiModelImpl.getInstance();
    }

    //获取所有sd卡的图片
    @Override
    public void getAllSDFiles(String dir) {
        mOperateWifiModel.getAllSDFiles(dir, fileInfos -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Config.KeyCode.KEY_THUMBNAILS, (ArrayList<? extends Parcelable>) fileInfos);
            Andromeda.publish(new Event(EventConstants.THUMBNAILS_EVENT, bundle));
        });
    }

    @Override
    public void getThumbnail(String url, IPCCallback callback) {
//        mOperateWifiModel.getThumbnails(url, (imgs, error) -> {
//            try {
//                if (error == null && imgs != null && imgs.length > 0) {
//                    Bundle bundle = new Bundle();
//                    bundle.putByteArray(Config.KeyCode.KEY_THUMBNAIL_BYTE, imgs);
//                    callback.onSuccess(bundle);
//                } else {
//                    callback.onFail(error == null ? "error": error.toString());
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//        });
        new Thread(() -> {
            FutureTarget<Bitmap> futureTarget =
                    GlideApp.with(MyApplication.getInstance())
                            .asBitmap()
                            .fitCenter()
                            .load(url).submit(300, 300);
            try {
                Bitmap bitmap = futureTarget.get();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Config.KeyCode.KEY_THUMBNAIL_BITMAP, bitmap);
                callback.onSuccess(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void getOriginalImage(String url, IPCCallback callback) {
        new Thread(() -> {

            ProgressInterceptor.addListener(url, (progress, bytesRead, totalBytes) -> {
//                    progressDialog.setProgress(progress);
                Log.e("load", "progress=" + progress + "---totalBytes=" + totalBytes + "---bytesRead=" + bytesRead);
                Bundle bundle = new Bundle();
                bundle.putInt(Config.KeyCode.KEY_ORIGINAL_PROGRESS, progress);
                bundle.putLong(Config.KeyCode.KEY_ORIGINAL_TOTAL_BYTES, totalBytes);
                bundle.putLong(Config.KeyCode.KEY_ORIGINAL_READ_BYTES, bytesRead);
                try {
                    callback.onSuccess(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

            FutureTarget<Bitmap> futureTarget =
                    GlideApp.with(MyApplication.getInstance())
                            .asBitmap()
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    ProgressInterceptor.removeListener(url);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    ProgressInterceptor.removeListener(url);
                                    return false;
                                }
                            })
                            .load(url).submit();
            try {
                Bitmap bitmap = futureTarget.get();
                Bundle bundle = new Bundle();
                Log.e("load", "bitmap=="+bitmap);

                bundle.putParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE, bitmap);
                callback.onSuccess(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
