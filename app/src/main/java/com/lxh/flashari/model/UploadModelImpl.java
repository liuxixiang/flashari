package com.lxh.flashari.model;

import android.util.Log;

import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.api.WifiApiService;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUploadManager;
import com.lxh.flashari.utils.qiniu.Auth;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class UploadModelImpl implements UploadModel {
    private static final UploadModel INSTANCE = new UploadModelImpl();

    public static UploadModel getInstance() {
        return INSTANCE;
    }

    @Override
    public void uploadBytes(String fileName, byte[] picturePath,final UpCompletionHandler complete, final UploadOptions options) {
        final String token = Auth.create(Config.QINIU_ACCESS_KEY, Config.QINIU_SECRET_KEY).uploadToken(Config.BUCKET);
        HashMap<String, String> map = new HashMap<>();
        map.put("x:phone", "12345678");
        Log.d("qiniu", "click upload");
        FlashAirUploadManager.getInstance().getUploadManager(fileName).put(picturePath, fileName, token,complete,options);
    }
}
