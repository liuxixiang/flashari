package com.lxh.flashari.model;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

public interface UploadModel {

    void uploadBytes(String fileName, byte[] picturePath, UpCompletionHandler complete, UploadOptions options);

    interface UploadBytesListener {
        void onUploadBytes(String key, ResponseInfo info, JSONObject res,Throwable error);
    }
}
