package com.lxh.flashari.model;

import com.lxh.flashari.utils.FlashAirFileInfo;

import java.util.List;

public interface OperateWifiModel {


    void getAllSDFiles(String dir, OnLoadAllSDFilesListener l);

    void cancelAll();

    interface OnLoadAllSDFilesListener {
        void onLoadAllSDFiles(List<FlashAirFileInfo> fileInfos);
    }


}
