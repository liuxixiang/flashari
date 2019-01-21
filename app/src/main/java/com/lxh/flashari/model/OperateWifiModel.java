package com.lxh.flashari.model;

import com.lxh.flashari.utils.FlashAirFileInfo;

import java.util.List;

public interface OperateWifiModel {


    void getAllSDFiles(String dir, OnLoadAllSDFilesListener l);
    void getThumbnails(String url, OnLoadThumbnailListener l);

    void cancelAll();

    interface OnLoadAllSDFilesListener {
        void onLoadAllSDFiles(List<FlashAirFileInfo> fileInfos);
    }

    interface OnLoadThumbnailListener {
        void onLoadThumbnail(byte[] imgs,Throwable error);
    }


}
