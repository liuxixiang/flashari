package com.lxh.flashari.service;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.model.OperateWifiModel;
import com.lxh.flashari.model.OperateWifiModelImpl;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.IPCCallback;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void getAllSDFiles(String dir, IPCCallback callback) throws RemoteException {
        mOperateWifiModel.getAllSDFiles(dir, new OperateWifiModel.OnLoadAllSDFilesListener() {
            @Override
            public void onLoadAllSDFiles(List<FlashAirFileInfo> fileInfos) {
                Bundle bundle = new Bundle();
                Log.e("lxh","fileInfos==" +  fileInfos);
                bundle.putParcelableArrayList("Thumbnails", (ArrayList<? extends Parcelable>) fileInfos);
                try {
                    callback.onSuccess(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
