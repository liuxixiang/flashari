// IOperateWifiAidl.aidl
package com.lxh.processmodule;
import org.qiyi.video.svg.IPCCallback;

// Declare any non-default types here with import statements

interface IOperateWifiAidl {
    void getAllSDFiles(String url);
    void getThumbnail(String url,IPCCallback callback);
    void getOriginalImage(String url,IPCCallback callback);
}
