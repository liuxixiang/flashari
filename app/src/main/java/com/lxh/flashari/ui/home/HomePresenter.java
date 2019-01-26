package com.lxh.flashari.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.common.base.BaseMvpPresenter;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.common.event.EventConstants;
import com.lxh.flashari.model.OperateWifiModel;
import com.lxh.flashari.model.OperateWifiModelImpl;
import com.lxh.flashari.model.UploadModel;
import com.lxh.flashari.model.UploadModelImpl;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.ui.originalImg.OriginalImgActivity;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.ConvertUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.qiyi.video.svg.Andromeda;
import org.qiyi.video.svg.event.Event;
import org.qiyi.video.svg.event.EventListener;

import java.util.HashMap;
import java.util.List;

public class HomePresenter extends BaseMvpPresenter<HomeView> implements EventListener {
    private UploadModel mUploadModel;
    private OperateWifiModel mOperateWifiModel;

    public HomePresenter() {
        mUploadModel = UploadModelImpl.getInstance();
        mOperateWifiModel = OperateWifiModelImpl.getInstance();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getThumbnails((List<FlashAirFileInfo>) msg.obj);
                    break;
            }
        }
    };

    public void onCreate() {
        Andromeda.subscribe(EventConstants.THUMBNAILS_EVENT, this);
    }

    public void getAllSDFiles() {
        AidlUtils.useOperateWifiAidl((Context) getView(), new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getAllSDFiles(Config.ROOT_DIR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }

    @Override
    public void onNotify(Event event) {
        if (EventConstants.THUMBNAILS_EVENT.equals(event.getName())) {
            Bundle bundle = event.getData();
            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAILS)) {
                List<FlashAirFileInfo> fileInfos = bundle.getParcelableArrayList(Config.KeyCode.KEY_THUMBNAILS);
                Message message = new Message();
                message.what = 1;
                message.obj = fileInfos;
                mHandler.sendMessage(message);
            }
        }
    }

    private void getThumbnails(List<FlashAirFileInfo> fileInfos) {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter((Context) getView(), fileInfos);
        thumbnailAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
            Intent mIntent = new Intent((Context) getView(), OriginalImgActivity.class);
            mIntent.putExtra(Config.KeyCode.KEY_FILE_INFO, flashAirFileInfo);
            ((Context) getView()).startActivity(mIntent);
        });
        thumbnailAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
            if (flashAirFileInfo != null) {
                mOperateWifiModel.downloadFile((Context) getView(), flashAirFileInfo.getFileName(), flashAirFileInfo.getDir(), new OperateWifiModel.OnDownOriginalImgListener() {
                    @Override
                    public void onSucceed(Bundle bundle, Bitmap bitmap) {
                        upload(flashAirFileInfo.getFileName(), bitmap);
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        getView().showToast(throwable.getMessage() + "");
                    }
                });
            }

        });

        getView().setThumbnailAdapter(thumbnailAdapter);
    }

    public void upload(String fileName, Bitmap bitmap) {
        byte[] picturePath = ConvertUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
        HashMap<String, String> map = new HashMap<>();
        map.put("x:phone", "12345678");
        mUploadModel.uploadBytes(fileName, picturePath, (key, info, response) -> {

        }, new UploadOptions(map, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.e("qiniu", key + ": " + percent);
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

}
