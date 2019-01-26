package com.lxh.flashari.ui.originalImg;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.base.BaseMvpPresenter;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.model.UploadModel;
import com.lxh.flashari.model.UploadModelImpl;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.ConvertUtils;
import com.lxh.processmodule.IOperateWifiAidl;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import java.util.HashMap;

public class OriginalImgPresenter extends BaseMvpPresenter<OriginalImgView> {
    private UploadModel mUploadModel;
    private Bitmap mBitmap;

    public OriginalImgPresenter() {
        mUploadModel = UploadModelImpl.getInstance();
    }


    public void upload(String fileName) {
        if (mBitmap == null) {
            getView().showToast("么有获取图片");
            return;
        }
        byte[] picturePath = ConvertUtils.bitmap2Bytes(mBitmap, Bitmap.CompressFormat.JPEG);
        HashMap<String, String> map = new HashMap<>();
        map.put("x:phone", "12345678");
        Log.d("qiniu", "click upload");
        mUploadModel.uploadBytes(fileName, picturePath, (key, info, response) -> {

        }, new UploadOptions(map, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.i("qiniu", key + ": " + percent);
//                                progressbar.setVisibility(View.VISIBLE);
//                                int progress = (int)(percent*1000);
////											Log.d("qiniu", progress+"");
//                                progressbar.setProgress(progress);
//                                if(progress==1000){
//                                    progressbar.setVisibility(View.GONE);
//                                }
            }
        }, null));

    }

    public void downloadFile(String downloadFile, String directory) {
        getView().showWaitDialog();
        // Download file
        String url = ApiManager.BASE_WIFI_PATH + "/" + directory + "/" + downloadFile;
        AidlUtils.useOperateWifiAidl((Context) getView(), new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getOriginalImage(url, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle bundle) {
                            getView().waitDialogDismiss();
                            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_ORIGINAL_IMAGE)) {
                                mBitmap = bundle.getParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE);
                                if (mBitmap != null) {
                                    getView().setOriginalImg(mBitmap);
                                }
                            }
                        }

                        @Override
                        public void onFailed(String bundle) {
                            getView().waitDialogDismiss();
                        }
                    });
                } catch (RemoteException e) {
                    getView().showToast(e.getMessage());
                    e.printStackTrace();
                } finally {
                    getView().waitDialogDismiss();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                getView().waitDialogDismiss();
            }
        });
    }

}
