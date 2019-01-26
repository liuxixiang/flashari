package com.lxh.flashari.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;

import com.lxh.flashari.R;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.api.WifiApiService;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUtils;
import com.lxh.processmodule.IOperateWifiAidl;

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
                    l.onLoadThumbnail(bytes, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    l.onLoadThumbnail(null, e);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                l.onLoadThumbnail(null, e);
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
    public void downloadFile(Context context, String downloadFile, String directory, OnDownOriginalImgListener listener) {
        // Download file
        String url = ApiManager.BASE_WIFI_PATH + "/" + directory + "/" + downloadFile;
        AidlUtils.useOperateWifiAidl(context, new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getOriginalImage(url, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle bundle) {
                            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_ORIGINAL_IMAGE)) {
                                Bitmap bitmap = bundle.getParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE);
                                if (bitmap != null) {
                                    listener.onSucceed(bundle, bitmap);
                                    return;
                                }
                            }
                            listener.onFailed(new Throwable("original image is null"));
                        }

                        @Override
                        public void onFailed(String bundle) {
                            listener.onFailed(new Throwable(bundle + ""));
                        }
                    });
                } catch (RemoteException e) {
                    listener.onFailed(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                listener.onFailed(throwable);
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
