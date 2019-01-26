package com.lxh.flashari.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.lxh.flashari.utils.FlashAirFileInfo;

import java.util.List;

public interface OperateWifiModel {


    void getAllSDFiles(String dir, OnLoadAllSDFilesListener l);
    void getThumbnails(String url, OnLoadThumbnailListener l);

    void downloadFile(Context context, String downloadFile, String directory, OnDownOriginalImgListener listener);

    void cancelAll();

    interface OnLoadAllSDFilesListener {
        void onLoadAllSDFiles(List<FlashAirFileInfo> fileInfos);
    }

    interface OnLoadThumbnailListener {
        void onLoadThumbnail(byte[] imgs,Throwable error);
    }


    interface OnDownOriginalImgListener {
        void onSucceed(Bundle bundle, Bitmap bitmap);

        void onFailed(Throwable throwable);
    }


}
