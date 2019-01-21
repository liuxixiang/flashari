package com.lxh.flashari.model;

import android.text.TextUtils;

import com.lxh.flashari.R;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.api.WifiApiService;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class OperateWifiModelImpl implements OperateWifiModel {
    private WeakHashMap<String, Observable<String>> mFileCalls = new WeakHashMap<>();
    private List<FlashAirFileInfo> mFileInfos = new ArrayList();
    private WifiApiService mApiService = ApiManager.getInstance().getWifiApiService();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private OnLoadAllSDFilesListener mOnLoadAllSDFilesListener;

    private static final OperateWifiModel INSTANCE = new OperateWifiModelImpl();

    public static OperateWifiModel getInstance() {
        return INSTANCE;
    }


    @Override
    public void getAllSDFiles(String dir, OnLoadAllSDFilesListener l) {
        mOnLoadAllSDFilesListener = l;
        getFiles(dir);
    }

    @Override
    public void getThumbnails(String url, OnLoadThumbnailListener l) {
        Observable<ResponseBody> observable = mApiService.getThumbnail(url);
        ApiManager.getInstance().sendHttp(observable, new CustomObserver<ResponseBody>() {
            @Override
            public void onNext(ResponseBody responseBody) {
                super.onNext(responseBody);
                try {
                    byte[] bytes = responseBody.bytes();
                    l.onLoadThumbnail(bytes,null);
                } catch (IOException e) {
                    e.printStackTrace();
                    l.onLoadThumbnail(null,e);
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                l.onLoadThumbnail(null,e);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    private void getFiles(final String dir) {
        Observable<String> observable = mApiService.getListDCIM(ApiManager.BASE_WIFI_PATH + "/command.cgi?op=100&DIR=" + dir);
        ApiManager.getInstance().sendHttp(observable, new CustomObserver<String>() {
            @Override
            protected void onStart() {
                super.onStart();
                mFileCalls.put(dir, observable);
            }

            @Override
            public void onNext(String s) {
                super.onNext(s);
                List<FlashAirFileInfo> fileList = FlashAirUtils.getFileList(dir, s);
                if (fileList != null) {
                    for (FlashAirFileInfo flashAirFileInfo : fileList) {
                        if (!TextUtils.isEmpty(flashAirFileInfo.getFileName())) {
                            if ((flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpg"))
                                    || (flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpeg"))) {
                                mFileInfos.add(flashAirFileInfo);
                            } else {
                                getFiles(dir + "/" + flashAirFileInfo.getFileName());
                            }
                        }
                    }
                }
                this.onComplete();
            }


            @Override
            public void onError(Throwable e) {
                super.onError(e);
                this.onComplete();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                mFileCalls.remove(dir);
                if (mFileCalls.size() == 0 && mOnLoadAllSDFilesListener != null) {
                    mOnLoadAllSDFilesListener.onLoadAllSDFiles(mFileInfos);
                }
            }
        });
    }

    @Override
    public void cancelAll() {
        System.gc();
//        for (Observable observable : mCalls.keySet()) {
//            observable.unsubscribeOn()
//        }
//        mCalls.clear();
    }
}
