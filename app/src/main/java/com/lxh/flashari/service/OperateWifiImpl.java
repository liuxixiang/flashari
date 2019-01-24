package com.lxh.flashari.service;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.lxh.flashari.MyApplication;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.common.event.EventConstants;
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
                    Glide.with(MyApplication.getInstance())
                            .asBitmap()
                            .load(url).submit(350,300);
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
            FutureTarget<Bitmap> futureTarget =
                    Glide.with(MyApplication.getInstance())
                            .asBitmap()
                            .load(url).submit(1000,800);
            try {
                Bitmap bitmap = futureTarget.get();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE, bitmap);
                callback.onSuccess(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
